package br.ucsal.edugestao.seguranca.service;

import br.ucsal.edugestao.academico.entity.TurmaDisciplina;
import br.ucsal.edugestao.config.AcessoNegadoNegocioException;
import br.ucsal.edugestao.config.RecursoNaoEncontradoException;
import br.ucsal.edugestao.usuario.entity.Perfil;
import br.ucsal.edugestao.usuario.entity.Usuario;
import br.ucsal.edugestao.usuario.repository.AlunoRepository;
import br.ucsal.edugestao.usuario.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AcessoService {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;

    public AcessoService(UsuarioRepository usuarioRepository, AlunoRepository alunoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
    }

    public Usuario obterUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new BadCredentialsException("Usuario nao autenticado.");
        }
        Long id;
        try {
            id = Long.parseLong(auth.getName());
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("Identificador de sessao invalido.");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario autenticado nao encontrado."));
        if (!usuario.isAtivo()) {
            throw new AcessoNegadoNegocioException("Conta de usuario desativada.");
        }
        return usuario;
    }

    // RN-06: Responsavel somente visualiza estudantes vinculados a sua conta
    public void validarAcessoAoAluno(Long alunoId) {
        Usuario logado = obterUsuarioLogado();
        if (logado.getPerfil() == Perfil.ADMINISTRADOR || logado.getPerfil() == Perfil.COORDENADOR) {
            return;
        }
        if (logado.getPerfil() == Perfil.ALUNO) {
            if (!logado.getId().equals(alunoId)) {
                throw new AcessoNegadoNegocioException("Estudante so pode consultar seus proprios dados.");
            }
            return;
        }
        if (logado.getPerfil() == Perfil.RESPONSAVEL) {
            boolean vinculado = alunoRepository.existsVinculoResponsavel(alunoId, logado.getId());
            if (!vinculado) {
                throw new AcessoNegadoNegocioException("RN-06: Responsavel nao possui vinculo autorizado com este estudante.");
            }
            return;
        }
        throw new AcessoNegadoNegocioException("Perfil sem permissao para acessar os dados deste estudante.");
    }

    public void validarProfessorDaTurmaDisciplina(TurmaDisciplina td) {
        Usuario logado = obterUsuarioLogado();
        if (logado.getPerfil() == Perfil.COORDENADOR || logado.getPerfil() == Perfil.ADMINISTRADOR) {
            return;
        }
        if (logado.getPerfil() == Perfil.PROFESSOR && td.getProfessor().getId().equals(logado.getId())) {
            return;
        }
        throw new AcessoNegadoNegocioException("Professor nao vinculado a esta turma/disciplina.");
    }
}
