package br.ucsal.edugestao.academico.service;

import br.ucsal.edugestao.academico.dto.MatriculaDto;
import br.ucsal.edugestao.academico.entity.Matricula;
import br.ucsal.edugestao.academico.entity.StatusMatricula;
import br.ucsal.edugestao.academico.entity.Turma;
import br.ucsal.edugestao.academico.repository.MatriculaRepository;
import br.ucsal.edugestao.academico.repository.TurmaRepository;
import br.ucsal.edugestao.config.RecursoNaoEncontradoException;
import br.ucsal.edugestao.config.RegraNegocioException;
import br.ucsal.edugestao.seguranca.service.AcessoService;
import br.ucsal.edugestao.usuario.entity.Aluno;
import br.ucsal.edugestao.usuario.repository.AlunoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final AcessoService acessoService;

    public MatriculaService(
            MatriculaRepository matriculaRepository,
            AlunoRepository alunoRepository,
            TurmaRepository turmaRepository,
            AcessoService acessoService
    ) {
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
        this.acessoService = acessoService;
    }

    @Transactional
    public MatriculaDto matricular(MatriculaDto dto) {
        Aluno aluno = alunoRepository.findById(dto.alunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno nao encontrado."));
        Turma turma = turmaRepository.findById(dto.turmaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma nao encontrada."));

        validarRn01MatriculaAtiva(aluno.getId(), turma);

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setDataMatricula(dto.dataMatricula() != null ? dto.dataMatricula() : LocalDate.now());
        matricula.setStatus(StatusMatricula.ATIVA);
        return MatriculaDto.fromEntity(matriculaRepository.save(matricula));
    }

    @Transactional
    public MatriculaDto alterarStatus(Long matriculaId, StatusMatricula novoStatus) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Matricula nao encontrada."));

        if (novoStatus == StatusMatricula.ATIVA && matricula.getStatus() != StatusMatricula.ATIVA) {
            validarRn01MatriculaAtiva(matricula.getAluno().getId(), matricula.getTurma());
        }

        matricula.setStatus(novoStatus);
        return MatriculaDto.fromEntity(matriculaRepository.save(matricula));
    }

    @Transactional(readOnly = true)
    public List<MatriculaDto> listarPorAluno(Long alunoId) {
        acessoService.validarAcessoAoAluno(alunoId);
        return matriculaRepository.findByAlunoIdOrderByDataMatriculaDesc(alunoId).stream()
                .map(MatriculaDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatriculaDto> listarTodas(Long turmaId) {
        List<Matricula> lista = turmaId != null
                ? matriculaRepository.findByTurmaIdOrderByAlunoNomeAsc(turmaId)
                : matriculaRepository.findAllByOrderByDataMatriculaDesc();
        return lista.stream().map(MatriculaDto::fromEntity).toList();
    }

    // RN-01: O aluno nao podera possuir duas matriculas ativas na mesma turma e no mesmo ano letivo
    private void validarRn01MatriculaAtiva(Long alunoId, Turma turma) {
        if (matriculaRepository.existsByAlunoIdAndTurmaIdAndStatus(alunoId, turma.getId(), StatusMatricula.ATIVA)) {
            throw new RegraNegocioException("RN-01: O aluno ja possui uma matricula ativa nesta turma.");
        }
        if (matriculaRepository.existsByAlunoIdAndAnoLetivoIdAndStatus(
                alunoId,
                turma.getAnoLetivo().getId(),
                StatusMatricula.ATIVA
        )) {
            throw new RegraNegocioException("RN-01: O aluno ja possui uma matricula ativa no ano letivo " + turma.getAnoLetivo().getAno() + ".");
        }
    }
}
