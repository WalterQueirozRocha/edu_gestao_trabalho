package br.ucsal.edugestao.avaliacao.service;

import br.ucsal.edugestao.academico.entity.Etapa;
import br.ucsal.edugestao.academico.entity.FormulaMedia;
import br.ucsal.edugestao.academico.entity.Matricula;
import br.ucsal.edugestao.academico.entity.TurmaDisciplina;
import br.ucsal.edugestao.academico.repository.EtapaRepository;
import br.ucsal.edugestao.academico.repository.MatriculaRepository;
import br.ucsal.edugestao.academico.repository.TurmaDisciplinaRepository;
import br.ucsal.edugestao.avaliacao.dto.AvaliacaoDto;
import br.ucsal.edugestao.avaliacao.dto.NotaItemDto;
import br.ucsal.edugestao.avaliacao.entity.Avaliacao;
import br.ucsal.edugestao.avaliacao.entity.Nota;
import br.ucsal.edugestao.avaliacao.repository.AvaliacaoRepository;
import br.ucsal.edugestao.avaliacao.repository.NotaRepository;
import br.ucsal.edugestao.config.RecursoNaoEncontradoException;
import br.ucsal.edugestao.config.RegraNegocioException;
import br.ucsal.edugestao.seguranca.service.AcessoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final NotaRepository notaRepository;
    private final TurmaDisciplinaRepository turmaDisciplinaRepository;
    private final EtapaRepository etapaRepository;
    private final MatriculaRepository matriculaRepository;
    private final AutorizacaoAlteracaoService autorizacaoAlteracaoService;
    private final AcessoService acessoService;

    public AvaliacaoService(
            AvaliacaoRepository avaliacaoRepository,
            NotaRepository notaRepository,
            TurmaDisciplinaRepository turmaDisciplinaRepository,
            EtapaRepository etapaRepository,
            MatriculaRepository matriculaRepository,
            AutorizacaoAlteracaoService autorizacaoAlteracaoService,
            AcessoService acessoService
    ) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.notaRepository = notaRepository;
        this.turmaDisciplinaRepository = turmaDisciplinaRepository;
        this.etapaRepository = etapaRepository;
        this.matriculaRepository = matriculaRepository;
        this.autorizacaoAlteracaoService = autorizacaoAlteracaoService;
        this.acessoService = acessoService;
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoDto> listarAvaliacoes(Long turmaDisciplinaId, Long etapaId) {
        if (turmaDisciplinaId != null) {
            TurmaDisciplina td = turmaDisciplinaRepository.findById(turmaDisciplinaId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Turma/disciplina nao encontrada."));
            acessoService.validarProfessorDaTurmaDisciplina(td);
        }
        List<Avaliacao> lista;
        if (turmaDisciplinaId != null && etapaId != null) {
            lista = avaliacaoRepository.findByTurmaDisciplinaIdAndEtapaIdOrderByDataAsc(turmaDisciplinaId, etapaId);
        } else if (turmaDisciplinaId != null) {
            lista = avaliacaoRepository.findByTurmaDisciplinaIdOrderByDataAsc(turmaDisciplinaId);
        } else if (etapaId != null) {
            lista = avaliacaoRepository.findByEtapaIdOrderByDataAsc(etapaId);
        } else {
            lista = avaliacaoRepository.findAll();
        }
        try {
            var logado = acessoService.obterUsuarioLogado();
            if (logado != null && logado.getPerfil() == br.ucsal.edugestao.usuario.entity.Perfil.PROFESSOR) {
                lista = lista.stream()
                        .filter(a -> a.getTurmaDisciplina().getProfessor().getId().equals(logado.getId()))
                        .toList();
            }
        } catch (Exception ignored) {
            // Contexto sem autenticacao
        }
        return lista.stream().map(AvaliacaoDto::fromEntity).toList();
    }

    @Transactional
    public AvaliacaoDto criarAvaliacao(AvaliacaoDto dto) {
        if (dto.peso() == null || dto.peso().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O peso da avaliacao deve ser maior que zero.");
        }
        TurmaDisciplina td = turmaDisciplinaRepository.findById(dto.turmaDisciplinaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma/disciplina nao encontrada."));
        acessoService.validarProfessorDaTurmaDisciplina(td);

        Etapa etapa = etapaRepository.findById(dto.etapaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Etapa nao encontrada."));

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setTurmaDisciplina(td);
        avaliacao.setEtapa(etapa);
        avaliacao.setDescricao(dto.descricao().trim());
        avaliacao.setData(dto.data());
        avaliacao.setPeso(dto.peso());
        return AvaliacaoDto.fromEntity(avaliacaoRepository.save(avaliacao));
    }

    @Transactional(readOnly = true)
    public List<NotaItemDto> listarNotasDaAvaliacao(Long avaliacaoId) {
        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Avaliacao nao encontrada."));
        acessoService.validarProfessorDaTurmaDisciplina(avaliacao.getTurmaDisciplina());
        return notaRepository.findByAvaliacaoIdOrderByMatriculaAlunoNomeAsc(avaliacaoId).stream()
                .map(NotaItemDto::fromEntity)
                .toList();
    }

    @Transactional
    public List<NotaItemDto> lancarNotas(Long avaliacaoId, List<NotaItemDto> itens) {
        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Avaliacao nao encontrada."));
        acessoService.validarProfessorDaTurmaDisciplina(avaliacao.getTurmaDisciplina());

        // RN-04: Bloqueia alteracao em etapa FECHADA ou PUBLICADA sem autorizacao
        autorizacaoAlteracaoService.validarEutilizarAutorizacaoSeEtapaFechada(
                avaliacao.getEtapa(),
                avaliacao.getTurmaDisciplina().getId()
        );

        List<NotaItemDto> resultado = new ArrayList<>();
        for (NotaItemDto item : itens) {
            if (item.valor() == null
                    || item.valor().compareTo(BigDecimal.ZERO) < 0
                    || item.valor().compareTo(new BigDecimal("10.00")) > 0) {
                throw new RegraNegocioException("Valor da nota deve estar entre 0 e 10.");
            }
            Matricula matricula = matriculaRepository.findById(item.matriculaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Matricula nao encontrada: " + item.matriculaId()));
            matricula.validarAtivaNaTurma(avaliacao.getTurmaDisciplina().getTurma().getId());
            Nota nota = notaRepository.findByAvaliacaoIdAndMatriculaId(avaliacaoId, matricula.getId())
                    .orElseGet(() -> {
                        Nota nova = new Nota();
                        nova.setAvaliacao(avaliacao);
                        nova.setMatricula(matricula);
                        return nova;
                    });
            nota.setValor(item.valor());
            nota.setDataLancamento(LocalDateTime.now());
            resultado.add(NotaItemDto.fromEntity(notaRepository.save(nota)));
        }
        return resultado;
    }

    // RN-03: formula configurada no ano letivo
    public BigDecimal calcularMedia(List<Nota> notas, FormulaMedia formula) {
        if (formula != FormulaMedia.ARITMETICA) {
            return calcularMediaPonderada(notas);
        }
        if (notas == null || notas.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal soma = notas.stream().map(Nota::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
    }

    // RN-03: Calculo da media ponderada pelos pesos das avaliacoes da etapa
    public BigDecimal calcularMediaPonderada(List<Nota> notas) {
        if (notas == null || notas.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal somaNotaPeso = BigDecimal.ZERO;
        BigDecimal somaPesos = BigDecimal.ZERO;
        for (Nota n : notas) {
            BigDecimal peso = n.getAvaliacao().getPeso();
            somaNotaPeso = somaNotaPeso.add(n.getValor().multiply(peso));
            somaPesos = somaPesos.add(peso);
        }
        if (somaPesos.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return somaNotaPeso.divide(somaPesos, 2, RoundingMode.HALF_UP);
    }
}
