package br.ucsal.edugestao.avaliacao.service;

import br.ucsal.edugestao.academico.dto.EtapaDto;
import br.ucsal.edugestao.academico.entity.*;
import br.ucsal.edugestao.academico.repository.EtapaRepository;
import br.ucsal.edugestao.academico.repository.MatriculaRepository;
import br.ucsal.edugestao.academico.repository.TurmaDisciplinaRepository;
import br.ucsal.edugestao.academico.repository.TurmaRepository;
import br.ucsal.edugestao.avaliacao.dto.BoletimDisciplinaDto;
import br.ucsal.edugestao.avaliacao.dto.BoletimEtapaDto;
import br.ucsal.edugestao.avaliacao.dto.ResultadoAlunoTurmaDto;
import br.ucsal.edugestao.avaliacao.entity.Boletim;
import br.ucsal.edugestao.avaliacao.entity.Nota;
import br.ucsal.edugestao.avaliacao.entity.Situacao;
import br.ucsal.edugestao.avaliacao.repository.BoletimRepository;
import br.ucsal.edugestao.avaliacao.repository.NotaRepository;
import br.ucsal.edugestao.comunicacao.service.ComunicadoService;
import br.ucsal.edugestao.config.RecursoNaoEncontradoException;
import br.ucsal.edugestao.config.RegraNegocioException;
import br.ucsal.edugestao.diario.dto.ResumoFrequenciaDto;
import br.ucsal.edugestao.diario.service.FrequenciaService;
import br.ucsal.edugestao.ia.service.PredicaoRiscoService;
import br.ucsal.edugestao.seguranca.service.AcessoService;
import br.ucsal.edugestao.usuario.dto.UsuarioResponse;
import br.ucsal.edugestao.usuario.entity.Perfil;
import br.ucsal.edugestao.usuario.entity.Usuario;
import br.ucsal.edugestao.usuario.repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class EtapaBoletimService {

    private final EtapaRepository etapaRepository;
    private final TurmaRepository turmaRepository;
    private final TurmaDisciplinaRepository turmaDisciplinaRepository;
    private final MatriculaRepository matriculaRepository;
    private final NotaRepository notaRepository;
    private final BoletimRepository boletimRepository;
    private final AlunoRepository alunoRepository;
    private final AvaliacaoService avaliacaoService;
    private final FrequenciaService frequenciaService;
    private final AcessoService acessoService;
    private final PredicaoRiscoService predicaoRiscoService;
    private final ComunicadoService comunicadoService;

    public EtapaBoletimService(
            EtapaRepository etapaRepository,
            TurmaRepository turmaRepository,
            TurmaDisciplinaRepository turmaDisciplinaRepository,
            MatriculaRepository matriculaRepository,
            NotaRepository notaRepository,
            BoletimRepository boletimRepository,
            AlunoRepository alunoRepository,
            AvaliacaoService avaliacaoService,
            FrequenciaService frequenciaService,
            AcessoService acessoService
    ) {
        this(
                etapaRepository,
                turmaRepository,
                turmaDisciplinaRepository,
                matriculaRepository,
                notaRepository,
                boletimRepository,
                alunoRepository,
                avaliacaoService,
                frequenciaService,
                acessoService,
                null,
                null
        );
    }

    @Autowired
    public EtapaBoletimService(
            EtapaRepository etapaRepository,
            TurmaRepository turmaRepository,
            TurmaDisciplinaRepository turmaDisciplinaRepository,
            MatriculaRepository matriculaRepository,
            NotaRepository notaRepository,
            BoletimRepository boletimRepository,
            AlunoRepository alunoRepository,
            AvaliacaoService avaliacaoService,
            FrequenciaService frequenciaService,
            AcessoService acessoService,
            PredicaoRiscoService predicaoRiscoService,
            ComunicadoService comunicadoService
    ) {
        this.etapaRepository = etapaRepository;
        this.turmaRepository = turmaRepository;
        this.turmaDisciplinaRepository = turmaDisciplinaRepository;
        this.matriculaRepository = matriculaRepository;
        this.notaRepository = notaRepository;
        this.boletimRepository = boletimRepository;
        this.alunoRepository = alunoRepository;
        this.avaliacaoService = avaliacaoService;
        this.frequenciaService = frequenciaService;
        this.acessoService = acessoService;
        this.predicaoRiscoService = predicaoRiscoService;
        this.comunicadoService = comunicadoService;
    }

    @Transactional
    public EtapaDto fecharEtapa(Long etapaId) {
        Etapa etapa = etapaRepository.findById(etapaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Etapa nao encontrada."));
        etapa.fechar();
        Etapa salva = etapaRepository.save(etapa);
        consolidarBoletinsDaEtapa(salva, false);
        if (predicaoRiscoService != null) {
            predicaoRiscoService.executarPredicaoParaAlunosAtivos();
        }
        return EtapaDto.fromEntity(salva);
    }

    @Transactional
    public EtapaDto publicarEtapa(Long etapaId) {
        Etapa etapa = etapaRepository.findById(etapaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Etapa nao encontrada."));
        etapa.publicar();
        Etapa salva = etapaRepository.save(etapa);
        consolidarBoletinsDaEtapa(salva, true);
        if (comunicadoService != null) {
            try {
                Usuario autor = acessoService.obterUsuarioLogado();
                comunicadoService.publicarAutomatico(
                        autor,
                        "Boletins Publicados - " + salva.getNome() + " (" + salva.getAnoLetivo().getAno() + ")",
                        "Os boletins e resultados consolidados da etapa " + salva.getNome()
                                + " foram publicados pela coordenacao pedagogica e ja estao disponiveis para consulta.",
                        Set.of(Perfil.ALUNO, Perfil.RESPONSAVEL)
                );
            } catch (Exception ignored) {
                // Ignora caso nao haja usuario logado no contexto de teste
            }
        }
        return EtapaDto.fromEntity(salva);
    }

    @Transactional(readOnly = true)
    public List<ResultadoAlunoTurmaDto> consultarResultadosDaTurma(Long turmaId, Long etapaId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma nao encontrada."));
        Etapa etapa = etapaId != null
                ? etapaRepository.findById(etapaId).orElseThrow(() -> new RecursoNaoEncontradoException("Etapa nao encontrada."))
                : etapaRepository.findByAnoLetivoIdOrderByDataInicioAsc(turma.getAnoLetivo().getId()).stream()
                        .findFirst()
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhuma etapa cadastrada para o ano letivo."));

        List<Matricula> matriculas = matriculaRepository.findByTurmaIdAndStatusOrderByAlunoNomeAsc(
                turmaId,
                StatusMatricula.ATIVA
        );
        List<ResultadoAlunoTurmaDto> resultados = new ArrayList<>();
        for (Matricula m : matriculas) {
            BoletimEtapaDto b = montarBoletimMatriculaEtapa(m, etapa);
            resultados.add(new ResultadoAlunoTurmaDto(
                    m.getId(),
                    m.getAluno().getId(),
                    m.getAluno().getNome(),
                    m.getAluno().getMatricula(),
                    b.mediaGeralEtapa(),
                    b.percentualFrequenciaGeral(),
                    b.situacao(),
                    b.disciplinas()
            ));
        }
        return resultados;
    }

    @Transactional(readOnly = true)
    public List<BoletimEtapaDto> consultarBoletimDoAlunoLogado() {
        Usuario logado = acessoService.obterUsuarioLogado();
        return consultarBoletinsDoAluno(logado.getId(), null);
    }

    // RN-05 e RN-06: Aluno e Responsavel so visualizam notas e boletins apos publicacao da etapa
    @Transactional(readOnly = true)
    public List<BoletimEtapaDto> consultarBoletinsDoAluno(Long alunoId, Long etapaId) {
        acessoService.validarAcessoAoAluno(alunoId);
        Usuario logado = acessoService.obterUsuarioLogado();
        boolean exigirPublicada = logado.getPerfil() == Perfil.ALUNO || logado.getPerfil() == Perfil.RESPONSAVEL;

        List<Matricula> matriculas = matriculaRepository.findByAlunoIdOrderByDataMatriculaDesc(alunoId);
        List<BoletimEtapaDto> resposta = new ArrayList<>();

        for (Matricula m : matriculas) {
            List<Etapa> etapas = etapaRepository.findByAnoLetivoIdOrderByDataInicioAsc(
                    m.getTurma().getAnoLetivo().getId()
            );
            for (Etapa e : etapas) {
                if (etapaId != null && !e.getId().equals(etapaId)) {
                    continue;
                }
                if (exigirPublicada && e.getStatus() != StatusEtapa.PUBLICADA) {
                    if (etapaId != null) {
                        throw new RegraNegocioException(
                                "RN-05: Notas e boletins da etapa " + e.getNome() + " ainda nao foram publicados pela escola."
                        );
                    }
                    continue;
                }
                resposta.add(montarBoletimMatriculaEtapa(m, e));
            }
        }
        return resposta;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarAlunosDoResponsavelLogado() {
        Usuario logado = acessoService.obterUsuarioLogado();
        return alunoRepository.findByResponsavelId(logado.getId()).stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    private void consolidarBoletinsDaEtapa(Etapa etapa, boolean publicar) {
        List<Turma> turmas = turmaRepository.findByAnoLetivoIdOrderByNomeAsc(etapa.getAnoLetivo().getId());
        for (Turma turma : turmas) {
            List<Matricula> matriculas = matriculaRepository.findByTurmaIdAndStatusOrderByAlunoNomeAsc(
                    turma.getId(),
                    StatusMatricula.ATIVA
            );
            for (Matricula m : matriculas) {
                BoletimEtapaDto calculado = montarBoletimMatriculaEtapa(m, etapa);
                Boletim boletim = boletimRepository.findByMatriculaIdAndEtapaId(m.getId(), etapa.getId())
                        .orElseGet(() -> {
                            Boletim novo = new Boletim();
                            novo.setMatricula(m);
                            novo.setEtapa(etapa);
                            return novo;
                        });
                boletim.setSituacao(calculado.situacao());
                if (publicar) {
                    boletim.setDataPublicacao(LocalDate.now());
                }
                boletimRepository.save(boletim);
            }
        }
    }

    public BoletimEtapaDto montarBoletimMatriculaEtapa(Matricula matricula, Etapa etapa) {
        AnoLetivo anoLetivo = matricula.getTurma().getAnoLetivo();
        BigDecimal mediaMinima = anoLetivo.getMediaMinima();
        BigDecimal freqMinima = anoLetivo.getFrequenciaMinima();

        List<TurmaDisciplina> disciplinasDaTurma = turmaDisciplinaRepository
                .findByTurmaIdOrderByDisciplinaNomeAsc(matricula.getTurma().getId());
        List<Nota> notasNaEtapa = notaRepository.findByMatriculaIdAndAvaliacaoEtapaId(matricula.getId(), etapa.getId());

        List<BoletimDisciplinaDto> disciplinasDto = new ArrayList<>();
        BigDecimal somaMedias = BigDecimal.ZERO;
        int qtdDisciplinasComNotas = 0;
        boolean algumaDisciplinaReprovada = false;

        for (TurmaDisciplina td : disciplinasDaTurma) {
            List<Nota> notasDisc = notasNaEtapa.stream()
                    .filter(n -> n.getAvaliacao().getTurmaDisciplina().getId().equals(td.getId()))
                    .toList();
            BigDecimal mediaDisc = avaliacaoService.calcularMediaPonderada(notasDisc);
            BigDecimal freqDisc = frequenciaService.calcularPercentualPorDisciplina(matricula.getId(), td.getId());
            boolean aprovadoMedia = notasDisc.isEmpty() || mediaDisc.compareTo(mediaMinima) >= 0;
            boolean aprovadoFreq = freqDisc.compareTo(freqMinima) >= 0;

            if (!notasDisc.isEmpty()) {
                somaMedias = somaMedias.add(mediaDisc);
                qtdDisciplinasComNotas++;
                if (!aprovadoMedia) {
                    algumaDisciplinaReprovada = true;
                }
            }

            disciplinasDto.add(new BoletimDisciplinaDto(
                    td.getId(),
                    td.getDisciplina().getNome(),
                    td.getDisciplina().getCodigo(),
                    mediaDisc,
                    freqDisc,
                    aprovadoMedia,
                    aprovadoFreq
            ));
        }

        BigDecimal mediaGeral = qtdDisciplinasComNotas == 0
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : somaMedias.divide(BigDecimal.valueOf(qtdDisciplinasComNotas), 2, RoundingMode.HALF_UP);

        ResumoFrequenciaDto resumoFreq = frequenciaService.calcularResumoFrequenciaInterno(matricula);
        BigDecimal freqGeral = resumoFreq.percentualPresenca();

        Situacao situacao;
        if (etapa.getStatus() == StatusEtapa.ABERTA && qtdDisciplinasComNotas == 0) {
            situacao = Situacao.EM_ANDAMENTO;
        } else if (!algumaDisciplinaReprovada && resumoFreq.aprovadoPorFrequencia() && qtdDisciplinasComNotas > 0) {
            situacao = Situacao.APROVADO;
        } else if (algumaDisciplinaReprovada || !resumoFreq.aprovadoPorFrequencia()) {
            situacao = Situacao.RETIDO;
        } else {
            situacao = Situacao.EM_ANDAMENTO;
        }

        Boletim boletimSalvo = boletimRepository.findByMatriculaIdAndEtapaId(matricula.getId(), etapa.getId())
                .orElse(null);

        return new BoletimEtapaDto(
                boletimSalvo != null ? boletimSalvo.getId() : null,
                matricula.getId(),
                matricula.getAluno().getId(),
                matricula.getAluno().getNome(),
                matricula.getAluno().getMatricula(),
                matricula.getTurma().getNome(),
                matricula.getTurma().getSerie(),
                anoLetivo.getAno(),
                etapa.getId(),
                etapa.getNome(),
                etapa.getStatus(),
                boletimSalvo != null ? boletimSalvo.getDataPublicacao() : null,
                boletimSalvo != null ? boletimSalvo.getSituacao() : situacao,
                mediaMinima,
                freqMinima,
                mediaGeral,
                freqGeral,
                disciplinasDto
        );
    }
}
