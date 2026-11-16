package br.ucsal.edugestao.avaliacao.service;

import br.ucsal.edugestao.academico.entity.Etapa;
import br.ucsal.edugestao.academico.entity.TurmaDisciplina;
import br.ucsal.edugestao.academico.repository.EtapaRepository;
import br.ucsal.edugestao.academico.repository.TurmaDisciplinaRepository;
import br.ucsal.edugestao.avaliacao.dto.AutorizacaoAlteracaoDto;
import br.ucsal.edugestao.avaliacao.entity.AutorizacaoAlteracao;
import br.ucsal.edugestao.avaliacao.repository.AutorizacaoAlteracaoRepository;
import br.ucsal.edugestao.config.AcessoNegadoNegocioException;
import br.ucsal.edugestao.config.RecursoNaoEncontradoException;
import br.ucsal.edugestao.config.RegraNegocioException;
import br.ucsal.edugestao.seguranca.service.AcessoService;
import br.ucsal.edugestao.usuario.entity.Coordenador;
import br.ucsal.edugestao.usuario.entity.Usuario;
import br.ucsal.edugestao.usuario.repository.CoordenadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AutorizacaoAlteracaoService {

    private final AutorizacaoAlteracaoRepository autorizacaoRepository;
    private final EtapaRepository etapaRepository;
    private final TurmaDisciplinaRepository turmaDisciplinaRepository;
    private final CoordenadorRepository coordenadorRepository;
    private final AcessoService acessoService;

    public AutorizacaoAlteracaoService(
            AutorizacaoAlteracaoRepository autorizacaoRepository,
            EtapaRepository etapaRepository,
            TurmaDisciplinaRepository turmaDisciplinaRepository,
            CoordenadorRepository coordenadorRepository,
            AcessoService acessoService
    ) {
        this.autorizacaoRepository = autorizacaoRepository;
        this.etapaRepository = etapaRepository;
        this.turmaDisciplinaRepository = turmaDisciplinaRepository;
        this.coordenadorRepository = coordenadorRepository;
        this.acessoService = acessoService;
    }

    @Transactional
    public AutorizacaoAlteracaoDto criarAutorizacao(AutorizacaoAlteracaoDto dto) {
        Etapa etapa = etapaRepository.findById(dto.etapaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Etapa nao encontrada."));
        TurmaDisciplina td = turmaDisciplinaRepository.findById(dto.turmaDisciplinaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma/disciplina nao encontrada."));
        Usuario logado = acessoService.obterUsuarioLogado();
        // RN-04: a autorizacao e sempre registrada em nome do coordenador que a concedeu
        Coordenador coord = coordenadorRepository.findById(logado.getId())
                .orElseThrow(() -> new AcessoNegadoNegocioException("RN-04: somente a coordenacao pode autorizar alteracoes."));

        AutorizacaoAlteracao auth = new AutorizacaoAlteracao();
        auth.setEtapa(etapa);
        auth.setTurmaDisciplina(td);
        auth.setCoordenador(coord);
        auth.setMotivo(dto.motivo().trim());
        auth.setCriadaEm(LocalDateTime.now());
        auth.setValidaAte(dto.validaAte() != null ? dto.validaAte() : LocalDateTime.now().plusHours(48));
        auth.setUtilizada(false);

        return AutorizacaoAlteracaoDto.fromEntity(autorizacaoRepository.save(auth));
    }

    @Transactional(readOnly = true)
    public List<AutorizacaoAlteracaoDto> listarTodas() {
        return autorizacaoRepository.findAllByOrderByCriadaEmDesc().stream()
                .map(AutorizacaoAlteracaoDto::fromEntity)
                .toList();
    }

    // RN-04: Notas e faltas nao poderao ser alteradas apos o fechamento da etapa sem autorizacao da coordenacao
    @Transactional
    public void validarEutilizarAutorizacaoSeEtapaFechada(Etapa etapa, Long turmaDisciplinaId) {
        if (etapa == null || etapa.getStatus().permiteLancamentoLivre()) {
            return;
        }
        Optional<AutorizacaoAlteracao> autorizacaoOpt = autorizacaoRepository
                .findFirstByEtapaIdAndTurmaDisciplinaIdAndUtilizadaFalseAndValidaAteAfterOrderByCriadaEmDesc(
                        etapa.getId(),
                        turmaDisciplinaId,
                        LocalDateTime.now()
                );
        if (autorizacaoOpt.isEmpty()) {
            throw new RegraNegocioException(
                    "RN-04: Etapa " + etapa.getNome() + " esta " + etapa.getStatus()
                            + ". Alteracao bloqueada sem autorizacao ativa da coordenacao."
            );
        }
        AutorizacaoAlteracao autorizacao = autorizacaoOpt.get();
        autorizacao.setUtilizada(true);
        autorizacaoRepository.save(autorizacao);
    }
}
