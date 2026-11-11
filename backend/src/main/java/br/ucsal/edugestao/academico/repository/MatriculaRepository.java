package br.ucsal.edugestao.academico.repository;

import br.ucsal.edugestao.academico.entity.Matricula;
import br.ucsal.edugestao.academico.entity.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    boolean existsByAlunoIdAndTurmaIdAndStatus(Long alunoId, Long turmaId, StatusMatricula status);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Matricula m WHERE m.aluno.id = :alunoId AND m.turma.anoLetivo.id = :anoLetivoId AND m.status = :status")
    boolean existsByAlunoIdAndAnoLetivoIdAndStatus(
            @Param("alunoId") Long alunoId,
            @Param("anoLetivoId") Long anoLetivoId,
            @Param("status") StatusMatricula status
    );

    List<Matricula> findByAlunoIdOrderByDataMatriculaDesc(Long alunoId);
    List<Matricula> findByTurmaIdAndStatusOrderByAlunoNomeAsc(Long turmaId, StatusMatricula status);
    List<Matricula> findByTurmaIdOrderByAlunoNomeAsc(Long turmaId);
    List<Matricula> findByStatusOrderByAlunoNomeAsc(StatusMatricula status);
    List<Matricula> findAllByOrderByDataMatriculaDesc();
}
