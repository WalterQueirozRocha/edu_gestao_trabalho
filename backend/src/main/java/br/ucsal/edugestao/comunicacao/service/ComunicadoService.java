package br.ucsal.edugestao.comunicacao.service;

import br.ucsal.edugestao.academico.entity.Matricula;
import br.ucsal.edugestao.academico.entity.StatusMatricula;
import br.ucsal.edugestao.academico.entity.Turma;
import br.ucsal.edugestao.academico.entity.TurmaDisciplina;
import br.ucsal.edugestao.academico.repository.MatriculaRepository;
import br.ucsal.edugestao.academico.repository.TurmaDisciplinaRepository;
import br.ucsal.edugestao.academico.repository.TurmaRepository;
import br.ucsal.edugestao.comunicacao.dto.ComunicadoDto;
import br.ucsal.edugestao.comunicacao.entity.Comunicado;
import br.ucsal.edugestao.comunicacao.repository.ComunicadoRepository;
import br.ucsal.edugestao.seguranca.service.AcessoService;
import br.ucsal.edugestao.usuario.entity.Aluno;
import br.ucsal.edugestao.usuario.entity.Perfil;
import br.ucsal.edugestao.usuario.entity.Usuario;
import br.ucsal.edugestao.usuario.repository.AlunoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ComunicadoService {

    private final ComunicadoRepository comunicadoRepository;
    private final TurmaRepository turmaRepository;
    private final MatriculaRepository matriculaRepository;
    private final TurmaDisciplinaRepository turmaDisciplinaRepository;
    private final AlunoRepository alunoRepository;
    private final AcessoService acessoService;

    public ComunicadoService(
            ComunicadoRepository comunicadoRepository,
            TurmaRepository turmaRepository,
            MatriculaRepository matriculaRepository,
            TurmaDisciplinaRepository turmaDisciplinaRepository,
            AlunoRepository alunoRepository,
            AcessoService acessoService
    ) {
        this.comunicadoRepository = comunicadoRepository;
        this.turmaRepository = turmaRepository;
        this.matriculaRepository = matriculaRepository;
        this.turmaDisciplinaRepository = turmaDisciplinaRepository;
        this.alunoRepository = alunoRepository;
        this.acessoService = acessoService;
    }

    @Transactional
    public ComunicadoDto publicar(ComunicadoDto dto) {
        Usuario autor = acessoService.obterUsuarioLogado();
        Comunicado c = new Comunicado();
        c.setAutor(autor);
        c.setTitulo(dto.titulo().trim());
        c.setMensagem(dto.mensagem().trim());
        c.publicar();

        if (dto.turmasIds() != null && !dto.turmasIds().isEmpty()) {
            Set<Turma> turmas = new HashSet<>(turmaRepository.findAllById(dto.turmasIds()));
            c.setTurmas(turmas);
        }
        if (dto.publicoAlvo() != null && !dto.publicoAlvo().isEmpty()) {
            c.setPublicoAlvo(new HashSet<>(dto.publicoAlvo()));
        }

        return ComunicadoDto.fromEntity(comunicadoRepository.save(c));
    }

    @Transactional
    public void publicarAutomatico(Usuario autor, String titulo, String mensagem, Set<Perfil> publicoAlvo) {
        if (autor == null) {
            return;
        }
        Comunicado c = new Comunicado();
        c.setAutor(autor);
        c.setTitulo(titulo);
        c.setMensagem(mensagem);
        c.setDataPublicacao(LocalDateTime.now());
        if (publicoAlvo != null) {
            c.setPublicoAlvo(new HashSet<>(publicoAlvo));
        }
        comunicadoRepository.save(c);
    }

    @Transactional(readOnly = true)
    public List<ComunicadoDto> listarParaUsuarioLogado() {
        Usuario logado = acessoService.obterUsuarioLogado();
        List<Comunicado> todos = comunicadoRepository.findAllByOrderByDataPublicacaoDesc();

        if (logado.getPerfil() == Perfil.ADMINISTRADOR || logado.getPerfil() == Perfil.COORDENADOR) {
            return todos.stream().map(ComunicadoDto::fromEntity).toList();
        }

        Set<Long> turmasDoUsuario = obterTurmasVinculadasAoUsuario(logado);

        return todos.stream()
                .filter(c -> c.getPublicoAlvo().isEmpty() || c.getPublicoAlvo().contains(logado.getPerfil()))
                .filter(c -> c.getTurmas().isEmpty()
                        || c.getTurmas().stream().anyMatch(t -> turmasDoUsuario.contains(t.getId())))
                .map(ComunicadoDto::fromEntity)
                .toList();
    }

    private Set<Long> obterTurmasVinculadasAoUsuario(Usuario usuario) {
        Set<Long> turmasIds = new HashSet<>();
        if (usuario.getPerfil() == Perfil.ALUNO) {
            matriculaRepository.findByAlunoIdOrderByDataMatriculaDesc(usuario.getId()).stream()
                    .filter(m -> m.getStatus() == StatusMatricula.ATIVA)
                    .forEach(m -> turmasIds.add(m.getTurma().getId()));
        } else if (usuario.getPerfil() == Perfil.RESPONSAVEL) {
            for (Aluno filho : alunoRepository.findByResponsavelId(usuario.getId())) {
                matriculaRepository.findByAlunoIdOrderByDataMatriculaDesc(filho.getId()).stream()
                        .filter(m -> m.getStatus() == StatusMatricula.ATIVA)
                        .forEach(m -> turmasIds.add(m.getTurma().getId()));
            }
        } else if (usuario.getPerfil() == Perfil.PROFESSOR) {
            for (TurmaDisciplina td : turmaDisciplinaRepository.findByProfessorIdOrderByTurmaNomeAscDisciplinaNomeAsc(usuario.getId())) {
                turmasIds.add(td.getTurma().getId());
            }
        }
        return turmasIds;
    }
}
