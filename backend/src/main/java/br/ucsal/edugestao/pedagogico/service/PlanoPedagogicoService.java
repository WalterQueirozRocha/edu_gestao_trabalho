package br.ucsal.edugestao.pedagogico.service;

import br.ucsal.edugestao.config.AcessoNegadoNegocioException;
import br.ucsal.edugestao.config.RecursoNaoEncontradoException;
import br.ucsal.edugestao.ia.entity.AlertaRisco;
import br.ucsal.edugestao.ia.repository.AlertaRiscoRepository;
import br.ucsal.edugestao.pedagogico.dto.PlanoPedagogicoDto;
import br.ucsal.edugestao.pedagogico.entity.PlanoPedagogico;
import br.ucsal.edugestao.pedagogico.repository.PlanoPedagogicoRepository;
import br.ucsal.edugestao.seguranca.service.AcessoService;
import br.ucsal.edugestao.usuario.entity.Aluno;
import br.ucsal.edugestao.usuario.entity.Coordenador;
import br.ucsal.edugestao.usuario.entity.ProfissionalApoio;
import br.ucsal.edugestao.usuario.entity.Usuario;
import br.ucsal.edugestao.usuario.repository.AlunoRepository;
import br.ucsal.edugestao.usuario.repository.CoordenadorRepository;
import br.ucsal.edugestao.usuario.repository.ProfissionalApoioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
public class PlanoPedagogicoService {

    private final PlanoPedagogicoRepository planoRepository;
    private final AlunoRepository alunoRepository;
    private final CoordenadorRepository coordenadorRepository;
    private final ProfissionalApoioRepository profissionalApoioRepository;
    private final AlertaRiscoRepository alertaRiscoRepository;
    private final AcessoService acessoService;

    public PlanoPedagogicoService(
            PlanoPedagogicoRepository planoRepository,
            AlunoRepository alunoRepository,
            CoordenadorRepository coordenadorRepository,
            ProfissionalApoioRepository profissionalApoioRepository,
            AlertaRiscoRepository alertaRiscoRepository,
            AcessoService acessoService
    ) {
        this.planoRepository = planoRepository;
        this.alunoRepository = alunoRepository;
        this.coordenadorRepository = coordenadorRepository;
        this.profissionalApoioRepository = profissionalApoioRepository;
        this.alertaRiscoRepository = alertaRiscoRepository;
        this.acessoService = acessoService;
    }

    @Transactional(readOnly = true)
    public List<PlanoPedagogicoDto> listarTodos() {
        return planoRepository.findAllByOrderByDataRevisaoAsc().stream()
                .map(PlanoPedagogicoDto::fromEntity)
                .toList();
    }

    @Transactional
    public PlanoPedagogicoDto criar(PlanoPedagogicoDto dto) {
        Aluno aluno = alunoRepository.findById(dto.alunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estudante nao encontrado."));
        Coordenador coord = obterCoordenadorAtual();

        PlanoPedagogico plano = new PlanoPedagogico();
        plano.setAluno(aluno);
        plano.setCoordenador(coord);
        if (dto.alertaOrigemId() != null) {
            AlertaRisco alerta = alertaRiscoRepository.findById(dto.alertaOrigemId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta de risco de origem nao encontrado."));
            plano.setAlertaOrigem(alerta);
        }
        plano.setMetas(dto.metas().trim());
        plano.setAdaptacoes(dto.adaptacoes().trim());
        plano.setEstrategias(dto.estrategias().trim());
        plano.revisar(dto.dataRevisao());
        plano.setCriadoEm(LocalDateTime.now());

        if (dto.profissionaisIds() != null && !dto.profissionaisIds().isEmpty()) {
            for (ProfissionalApoio prof : profissionalApoioRepository.findAllById(dto.profissionaisIds())) {
                plano.autorizar(prof);
            }
        }

        return PlanoPedagogicoDto.fromEntity(planoRepository.save(plano));
    }

    @Transactional
    public PlanoPedagogicoDto atualizar(Long id, PlanoPedagogicoDto dto) {
        PlanoPedagogico plano = planoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano pedagogico nao encontrado."));
        plano.setMetas(dto.metas().trim());
        plano.setAdaptacoes(dto.adaptacoes().trim());
        plano.setEstrategias(dto.estrategias().trim());
        plano.revisar(dto.dataRevisao());
        return PlanoPedagogicoDto.fromEntity(planoRepository.save(plano));
    }

    @Transactional
    public PlanoPedagogicoDto autorizarProfissionais(Long planoId, List<Long> profissionaisIds) {
        PlanoPedagogico plano = planoRepository.findById(planoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano pedagogico nao encontrado."));
        plano.setProfissionaisAutorizados(new HashSet<>());
        if (profissionaisIds != null) {
            for (ProfissionalApoio prof : profissionalApoioRepository.findAllById(profissionaisIds)) {
                plano.autorizar(prof);
            }
        }
        return PlanoPedagogicoDto.fromEntity(planoRepository.save(plano));
    }

    // UC08: Profissional de apoio so acompanha planos em que foi autorizado
    @Transactional(readOnly = true)
    public List<PlanoPedagogicoDto> listarPlanosAutorizadosParaProfissionalLogado() {
        Usuario logado = acessoService.obterUsuarioLogado();
        return planoRepository.findByProfissionalAutorizadoId(logado.getId()).stream()
                .map(PlanoPedagogicoDto::fromEntity)
                .toList();
    }

    public Coordenador obterCoordenadorAtual() {
        Usuario logado = acessoService.obterUsuarioLogado();
        return coordenadorRepository.findById(logado.getId())
                .orElseThrow(() -> new AcessoNegadoNegocioException("Somente a coordenacao pode criar planos pedagogicos."));
    }
}
