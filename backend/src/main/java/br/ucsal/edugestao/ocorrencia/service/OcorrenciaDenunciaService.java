package br.ucsal.edugestao.ocorrencia.service;

import br.ucsal.edugestao.config.RecursoNaoEncontradoException;
import br.ucsal.edugestao.config.RegraNegocioException;
import br.ucsal.edugestao.ocorrencia.dto.DenunciaDto;
import br.ucsal.edugestao.ocorrencia.dto.OcorrenciaDto;
import br.ucsal.edugestao.ocorrencia.dto.TratarDenunciaRequest;
import br.ucsal.edugestao.ocorrencia.entity.Denuncia;
import br.ucsal.edugestao.ocorrencia.entity.Ocorrencia;
import br.ucsal.edugestao.ocorrencia.entity.StatusAtendimento;
import br.ucsal.edugestao.ocorrencia.repository.DenunciaRepository;
import br.ucsal.edugestao.ocorrencia.repository.OcorrenciaRepository;
import br.ucsal.edugestao.pedagogico.dto.PlanoPedagogicoDto;
import br.ucsal.edugestao.pedagogico.service.PlanoPedagogicoService;
import br.ucsal.edugestao.seguranca.service.AcessoService;
import br.ucsal.edugestao.usuario.entity.Aluno;
import br.ucsal.edugestao.usuario.entity.Perfil;
import br.ucsal.edugestao.usuario.entity.Usuario;
import br.ucsal.edugestao.usuario.repository.AlunoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class OcorrenciaDenunciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final DenunciaRepository denunciaRepository;
    private final AlunoRepository alunoRepository;
    private final PlanoPedagogicoService planoPedagogicoService;
    private final AcessoService acessoService;

    public OcorrenciaDenunciaService(
            OcorrenciaRepository ocorrenciaRepository,
            DenunciaRepository denunciaRepository,
            AlunoRepository alunoRepository,
            PlanoPedagogicoService planoPedagogicoService,
            AcessoService acessoService
    ) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.denunciaRepository = denunciaRepository;
        this.alunoRepository = alunoRepository;
        this.planoPedagogicoService = planoPedagogicoService;
        this.acessoService = acessoService;
    }

    @Transactional
    public OcorrenciaDto registrarOcorrencia(OcorrenciaDto dto) {
        Usuario logado = acessoService.obterUsuarioLogado();
        Set<Aluno> alunos = new HashSet<>(alunoRepository.findAllById(dto.alunosIds()));
        if (alunos.isEmpty()) {
            throw new RegraNegocioException("Ao menos um estudante valido deve ser associado a ocorrencia.");
        }

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setRegistradaPor(logado);
        ocorrencia.setData(dto.data() != null ? dto.data() : LocalDate.now());
        ocorrencia.setTipo(dto.tipo());
        ocorrencia.setDescricao(dto.descricao().trim());
        ocorrencia.setAlunos(alunos);
        ocorrencia.registrar();

        return OcorrenciaDto.fromEntity(ocorrenciaRepository.save(ocorrencia));
    }

    @Transactional(readOnly = true)
    public List<OcorrenciaDto> listarOcorrencias() {
        Usuario logado = acessoService.obterUsuarioLogado();
        // LGPD: professor ve apenas as ocorrencias que registrou
        List<Ocorrencia> lista = logado.getPerfil() == Perfil.PROFESSOR
                ? ocorrenciaRepository.findByRegistradaPorIdOrderByDataDescIdDesc(logado.getId())
                : ocorrenciaRepository.findAllByOrderByDataDescIdDesc();
        return lista.stream()
                .map(OcorrenciaDto::fromEntity)
                .toList();
    }

    // UC12: tratamento de ocorrencias com os mesmos status das denuncias
    @Transactional
    public OcorrenciaDto tratarOcorrencia(Long id, TratarDenunciaRequest req) {
        Ocorrencia o = ocorrenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ocorrencia nao encontrada."));
        String medidas = req.medidas();
        if (req.status() == StatusAtendimento.ARQUIVADA) {
            if (req.justificativaArquivamento() == null || req.justificativaArquivamento().isBlank()) {
                throw new RegraNegocioException("A justificativa e obrigatoria para arquivar uma ocorrencia.");
            }
            medidas = "Arquivada: " + req.justificativaArquivamento().trim();
        }
        o.tratar(req.status(), medidas);
        criarPlanoSeSolicitado(req, "Acompanhamento pedagogico decorrente da ocorrencia #" + o.getId());
        return OcorrenciaDto.fromEntity(ocorrenciaRepository.save(o));
    }

    @Transactional
    public DenunciaDto registrarDenuncia(DenunciaDto dto) {
        Usuario logado = acessoService.obterUsuarioLogado();
        Denuncia d = new Denuncia();
        String sufixo = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        d.setProtocolo("DEN-" + LocalDate.now().getYear() + "-" + sufixo);
        d.setDescricao(dto.descricao().trim());
        d.setAnonima(dto.anonima());
        d.setDenunciante(dto.anonima() ? null : logado);
        d.setStatus(StatusAtendimento.RECEBIDA);
        d.setCriadaEm(LocalDateTime.now());
        d.setAtualizadaEm(LocalDateTime.now());

        return DenunciaDto.fromEntity(denunciaRepository.save(d), false);
    }

    @Transactional(readOnly = true)
    public DenunciaDto consultarPorProtocolo(String protocolo) {
        Denuncia d = denunciaRepository.findByProtocoloIgnoreCase(protocolo.trim())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhuma denuncia encontrada para o protocolo informado."));
        Usuario logado = acessoService.obterUsuarioLogado();
        boolean coordenacao = logado.getPerfil() == Perfil.COORDENADOR || logado.getPerfil() == Perfil.ADMINISTRADOR;
        boolean autor = d.getDenunciante() != null && d.getDenunciante().getId().equals(logado.getId());
        // LGPD: terceiros acompanham apenas o andamento, sem o conteudo da denuncia
        return (coordenacao || autor) ? DenunciaDto.fromEntity(d, true) : DenunciaDto.andamento(d);
    }

    @Transactional(readOnly = true)
    public List<DenunciaDto> listarDenunciasRestrito() {
        return denunciaRepository.findAllByOrderByCriadaEmDesc().stream()
                .map(d -> DenunciaDto.fromEntity(d, false))
                .toList();
    }

    @Transactional
    public DenunciaDto tratarDenuncia(Long id, TratarDenunciaRequest req) {
        Denuncia d = denunciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Denuncia nao encontrada."));

        if (req.status() == StatusAtendimento.ARQUIVADA) {
            if (req.justificativaArquivamento() == null || req.justificativaArquivamento().isBlank()) {
                throw new RegraNegocioException("A justificativa e obrigatoria para arquivar uma denuncia.");
            }
            d.arquivar(req.justificativaArquivamento().trim());
        } else if (req.status() == StatusAtendimento.ENCAMINHADA) {
            d.encaminhar(req.medidas());
        } else {
            d.setStatus(req.status());
            if (req.medidas() != null) {
                d.setMedidas(req.medidas());
            }
            d.setAtualizadaEm(LocalDateTime.now());
        }

        criarPlanoSeSolicitado(req, "Acompanhamento pedagogico decorrente do protocolo " + d.getProtocolo());
        return DenunciaDto.fromEntity(denunciaRepository.save(d), false);
    }

    private void criarPlanoSeSolicitado(TratarDenunciaRequest req, String metasPadrao) {
        if (Boolean.TRUE.equals(req.requerAcompanhamentoPedagogico()) && req.alunoIdPlano() != null && planoPedagogicoService != null) {
            String metas = (req.metasPlano() != null && !req.metasPlano().isBlank())
                    ? req.metasPlano()
                    : metasPadrao;
            String adaptacoes = (req.adaptacoesPlano() != null && !req.adaptacoesPlano().isBlank())
                    ? req.adaptacoesPlano()
                    : "Escuta ativa e acompanhamento socioemocional/pedagogico.";
            String estrategias = (req.estrategiasPlano() != null && !req.estrategiasPlano().isBlank())
                    ? req.estrategiasPlano()
                    : (req.medidas() != null && !req.medidas().isBlank() ? req.medidas() : "Monitoramento continuo pela coordenacao.");
            LocalDate dataRev = req.dataRevisaoPlano() != null ? req.dataRevisaoPlano() : LocalDate.now().plusDays(30);

            planoPedagogicoService.criar(new PlanoPedagogicoDto(
                    null,
                    req.alunoIdPlano(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    metas,
                    adaptacoes,
                    estrategias,
                    dataRev,
                    null,
                    List.of(),
                    List.of()
            ));
        }
    }
}
