package br.ucsal.edugestao.diario.service;

import br.ucsal.edugestao.academico.dto.TurmaDisciplinaDto;
import br.ucsal.edugestao.academico.entity.Etapa;
import br.ucsal.edugestao.academico.entity.Matricula;
import br.ucsal.edugestao.academico.entity.TurmaDisciplina;
import br.ucsal.edugestao.academico.repository.EtapaRepository;
import br.ucsal.edugestao.academico.repository.MatriculaRepository;
import br.ucsal.edugestao.academico.repository.TurmaDisciplinaRepository;
import br.ucsal.edugestao.avaliacao.service.AutorizacaoAlteracaoService;
import br.ucsal.edugestao.config.RecursoNaoEncontradoException;
import br.ucsal.edugestao.diario.dto.AulaDto;
import br.ucsal.edugestao.diario.dto.FrequenciaItemDto;
import br.ucsal.edugestao.diario.dto.ResumoFrequenciaDto;
import br.ucsal.edugestao.diario.entity.Aula;
import br.ucsal.edugestao.diario.entity.Frequencia;
import br.ucsal.edugestao.diario.repository.AulaRepository;
import br.ucsal.edugestao.diario.repository.FrequenciaRepository;
import br.ucsal.edugestao.seguranca.service.AcessoService;
import br.ucsal.edugestao.usuario.entity.Perfil;
import br.ucsal.edugestao.usuario.entity.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FrequenciaService {

    private final AulaRepository aulaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final TurmaDisciplinaRepository turmaDisciplinaRepository;
    private final MatriculaRepository matriculaRepository;
    private final EtapaRepository etapaRepository;
    private final AutorizacaoAlteracaoService autorizacaoAlteracaoService;
    private final AcessoService acessoService;

    public FrequenciaService(
            AulaRepository aulaRepository,
            FrequenciaRepository frequenciaRepository,
            TurmaDisciplinaRepository turmaDisciplinaRepository,
            MatriculaRepository matriculaRepository,
            EtapaRepository etapaRepository,
            AutorizacaoAlteracaoService autorizacaoAlteracaoService,
            AcessoService acessoService
    ) {
        this.aulaRepository = aulaRepository;
        this.frequenciaRepository = frequenciaRepository;
        this.turmaDisciplinaRepository = turmaDisciplinaRepository;
        this.matriculaRepository = matriculaRepository;
        this.etapaRepository = etapaRepository;
        this.autorizacaoAlteracaoService = autorizacaoAlteracaoService;
        this.acessoService = acessoService;
    }

    @Transactional(readOnly = true)
    public List<TurmaDisciplinaDto> listarTurmasDoProfessorLogado() {
        Usuario logado = acessoService.obterUsuarioLogado();
        List<TurmaDisciplina> lista;
        if (logado.getPerfil() == Perfil.PROFESSOR) {
            lista = turmaDisciplinaRepository.findByProfessorIdOrderByTurmaNomeAscDisciplinaNomeAsc(logado.getId());
        } else {
            lista = turmaDisciplinaRepository.findAll();
        }
        return lista.stream().map(TurmaDisciplinaDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<AulaDto> listarAulas(Long turmaDisciplinaId) {
        TurmaDisciplina td = turmaDisciplinaRepository.findById(turmaDisciplinaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma/disciplina nao encontrada."));
        acessoService.validarProfessorDaTurmaDisciplina(td);
        return aulaRepository.findByTurmaDisciplinaIdOrderByDataDesc(turmaDisciplinaId).stream()
                .map(AulaDto::fromEntity)
                .toList();
    }

    @Transactional
    public AulaDto criarAula(AulaDto dto) {
        TurmaDisciplina td = turmaDisciplinaRepository.findById(dto.turmaDisciplinaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma/disciplina nao encontrada."));
        acessoService.validarProfessorDaTurmaDisciplina(td);

        Aula aula = new Aula();
        aula.setTurmaDisciplina(td);
        aula.setData(dto.data());
        aula.setConteudo(dto.conteudo().trim());
        aula.setHoras(dto.horas());
        return AulaDto.fromEntity(aulaRepository.save(aula));
    }

    @Transactional(readOnly = true)
    public List<FrequenciaItemDto> listarFrequenciasDaAula(Long aulaId) {
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aula nao encontrada."));
        acessoService.validarProfessorDaTurmaDisciplina(aula.getTurmaDisciplina());
        return frequenciaRepository.findByAulaIdOrderByMatriculaAlunoNomeAsc(aulaId).stream()
                .map(FrequenciaItemDto::fromEntity)
                .toList();
    }

    @Transactional
    public List<FrequenciaItemDto> registrarFrequencias(Long aulaId, List<FrequenciaItemDto> itens) {
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aula nao encontrada."));
        acessoService.validarProfessorDaTurmaDisciplina(aula.getTurmaDisciplina());

        Etapa etapaDaAula = localizarEtapaPorData(
                aula.getTurmaDisciplina().getTurma().getAnoLetivo().getId(),
                aula.getData()
        );
        autorizacaoAlteracaoService.validarEutilizarAutorizacaoSeEtapaFechada(
                etapaDaAula,
                aula.getTurmaDisciplina().getId()
        );

        List<FrequenciaItemDto> resultado = new ArrayList<>();
        for (FrequenciaItemDto item : itens) {
            Matricula matricula = matriculaRepository.findById(item.matriculaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Matricula nao encontrada: " + item.matriculaId()));
            Frequencia freq = frequenciaRepository.findByAulaIdAndMatriculaId(aulaId, matricula.getId())
                    .orElseGet(() -> {
                        Frequencia nova = new Frequencia();
                        nova.setAula(aula);
                        nova.setMatricula(matricula);
                        return nova;
                    });
            freq.setPresente(Boolean.TRUE.equals(item.presente()));
            freq.setJustificativa(item.justificativa());
            resultado.add(FrequenciaItemDto.fromEntity(frequenciaRepository.save(freq)));
        }
        return resultado;
    }

    // RN-02: Calculo do percentual de presenca considerando horas de aula
    @Transactional(readOnly = true)
    public ResumoFrequenciaDto calcularResumoFrequencia(Long matriculaId) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Matricula nao encontrada."));
        validarAcessoResumoFrequencia(matricula);
        List<Frequencia> registros = frequenciaRepository.findByMatriculaId(matriculaId);
        return montarResumo(matricula, registros);
    }

    @Transactional(readOnly = true)
    public ResumoFrequenciaDto calcularResumoFrequenciaInterno(Matricula matricula) {
        List<Frequencia> registros = frequenciaRepository.findByMatriculaId(matricula.getId());
        return montarResumo(matricula, registros);
    }

    private void validarAcessoResumoFrequencia(Matricula matricula) {
        try {
            Usuario logado = acessoService.obterUsuarioLogado();
            if (logado == null) {
                return;
            }
            if (logado.getPerfil() == Perfil.ALUNO || logado.getPerfil() == Perfil.RESPONSAVEL) {
                acessoService.validarAcessoAoAluno(matricula.getAluno().getId());
            } else if (logado.getPerfil() == Perfil.PROFESSOR) {
                boolean lecionaNaTurma = turmaDisciplinaRepository
                        .findByTurmaIdOrderByDisciplinaNomeAsc(matricula.getTurma().getId())
                        .stream()
                        .anyMatch(td -> td.getProfessor().getId().equals(logado.getId()));
                if (!lecionaNaTurma) {
                    throw new br.ucsal.edugestao.config.AcessoNegadoNegocioException(
                            "Professor nao vinculado a turma deste estudante."
                    );
                }
            }
        } catch (org.springframework.security.authentication.BadCredentialsException ignored) {
            // Contexto interno ou job agendado
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularPercentualPorDisciplina(Long matriculaId, Long turmaDisciplinaId) {
        List<Frequencia> registros = frequenciaRepository.findByMatriculaId(matriculaId).stream()
                .filter(f -> f.getAula().getTurmaDisciplina().getId().equals(turmaDisciplinaId))
                .toList();
        int totalHoras = registros.stream().mapToInt(f -> f.getAula().getHoras()).sum();
        if (totalHoras == 0) {
            return new BigDecimal("100.00");
        }
        int horasPresentes = registros.stream()
                .filter(Frequencia::isPresente)
                .mapToInt(f -> f.getAula().getHoras())
                .sum();
        return BigDecimal.valueOf(horasPresentes * 100.0)
                .divide(BigDecimal.valueOf(totalHoras), 2, RoundingMode.HALF_UP);
    }

    public ResumoFrequenciaDto montarResumo(Matricula matricula, List<Frequencia> registros) {
        int horasRegistradas = registros.stream().mapToInt(f -> f.getAula().getHoras()).sum();
        int horasPresentes = registros.stream()
                .filter(Frequencia::isPresente)
                .mapToInt(f -> f.getAula().getHoras())
                .sum();
        int horasFaltas = horasRegistradas - horasPresentes;

        BigDecimal percentual = horasRegistradas == 0
                ? new BigDecimal("100.00")
                : BigDecimal.valueOf(horasPresentes * 100.0)
                        .divide(BigDecimal.valueOf(horasRegistradas), 2, RoundingMode.HALF_UP);

        BigDecimal freqMinima = matricula.getTurma().getAnoLetivo().getFrequenciaMinima();
        boolean aprovado = percentual.compareTo(freqMinima) >= 0;
        boolean alerta80 = horasRegistradas > 0 && percentual.compareTo(new BigDecimal("80.00")) < 0;

        return new ResumoFrequenciaDto(
                matricula.getId(),
                matricula.getAluno().getId(),
                matricula.getAluno().getNome(),
                horasRegistradas,
                horasPresentes,
                horasFaltas,
                percentual,
                freqMinima,
                aprovado,
                alerta80
        );
    }

    private Etapa localizarEtapaPorData(Long anoLetivoId, LocalDate data) {
        return etapaRepository.findByAnoLetivoIdOrderByDataInicioAsc(anoLetivoId).stream()
                .filter(e -> !data.isBefore(e.getDataInicio()) && !data.isAfter(e.getDataFim()))
                .findFirst()
                .orElse(null);
    }
}
