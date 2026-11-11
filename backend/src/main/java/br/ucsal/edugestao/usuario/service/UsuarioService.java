package br.ucsal.edugestao.usuario.service;

import br.ucsal.edugestao.config.RecursoNaoEncontradoException;
import br.ucsal.edugestao.config.RegraNegocioException;
import br.ucsal.edugestao.usuario.dto.UsuarioRequest;
import br.ucsal.edugestao.usuario.dto.UsuarioResponse;
import br.ucsal.edugestao.usuario.entity.*;
import br.ucsal.edugestao.usuario.repository.AlunoRepository;
import br.ucsal.edugestao.usuario.repository.ResponsavelRepository;
import br.ucsal.edugestao.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            AlunoRepository alunoRepository,
            ResponsavelRepository responsavelRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.responsavelRepository = responsavelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(Perfil perfil) {
        List<Usuario> lista = perfil != null
                ? usuarioRepository.findByPerfilOrderByNomeAsc(perfil)
                : usuarioRepository.findAllByOrderByNomeAsc();
        return lista.stream().map(UsuarioResponse::fromEntity).toList();
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest req) {
        if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
            throw new RegraNegocioException("Ja existe um usuario cadastrado com o e-mail informado.");
        }
        String senhaBruta = (req.senha() != null && !req.senha().isBlank()) ? req.senha() : "123456";
        Usuario usuario = instanciarPorPerfil(req.perfil());
        preencherCampos(usuario, req);
        usuario.setSenhaHash(passwordEncoder.encode(senhaBruta));
        usuario.setAtivo(true);
        return UsuarioResponse.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest req) {
        Usuario usuario = buscarEntidade(id);
        if (!usuario.getEmail().equalsIgnoreCase(req.email()) && usuarioRepository.existsByEmailIgnoreCase(req.email())) {
            throw new RegraNegocioException("Ja existe outro usuario com o e-mail informado.");
        }
        preencherCampos(usuario, req);
        if (req.senha() != null && !req.senha().isBlank()) {
            usuario.setSenhaHash(passwordEncoder.encode(req.senha()));
        }
        return UsuarioResponse.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse alterarAtivo(Long id, boolean ativo) {
        Usuario usuario = buscarEntidade(id);
        usuario.setAtivo(ativo);
        return UsuarioResponse.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse atualizarResponsaveisDoAluno(Long alunoId, List<Long> responsaveisIds) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno nao encontrado."));
        Set<Responsavel> responsaveis = new HashSet<>(responsavelRepository.findAllById(responsaveisIds));
        if (responsaveis.size() != new HashSet<>(responsaveisIds).size()) {
            throw new RegraNegocioException("Um ou mais responsaveis informados nao foram encontrados.");
        }
        aluno.setResponsaveis(responsaveis);
        return UsuarioResponse.fromEntity(alunoRepository.save(aluno));
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado."));
    }

    private Usuario instanciarPorPerfil(Perfil perfil) {
        return switch (perfil) {
            case ADMINISTRADOR -> new Administrador();
            case COORDENADOR -> new Coordenador();
            case PROFESSOR -> new Professor();
            case PROFISSIONAL_APOIO -> new ProfissionalApoio();
            case ALUNO -> new Aluno();
            case RESPONSAVEL -> new Responsavel();
        };
    }

    private void preencherCampos(Usuario usuario, UsuarioRequest req) {
        usuario.setNome(req.nome().trim());
        usuario.setEmail(req.email().trim().toLowerCase());

        if (usuario instanceof Coordenador c) {
            c.setCargo(req.cargo());
        } else if (usuario instanceof Professor p) {
            p.setRegistroFuncional(req.registroFuncional());
            p.setFormacao(req.formacao());
        } else if (usuario instanceof ProfissionalApoio pa) {
            pa.setEspecialidade(req.especialidade());
        } else if (usuario instanceof Aluno a) {
            a.setMatricula(req.matricula());
            a.setDataNascimento(req.dataNascimento());
        } else if (usuario instanceof Responsavel r) {
            r.setCpf(req.cpf());
            r.setTelefone(req.telefone());
        }
    }
}
