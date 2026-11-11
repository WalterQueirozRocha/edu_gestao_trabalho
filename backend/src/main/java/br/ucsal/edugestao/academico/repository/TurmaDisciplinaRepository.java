package br.ucsal.edugestao.academico.repository;

import br.ucsal.edugestao.academico.entity.TurmaDisciplina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurmaDisciplinaRepository extends JpaRepository<TurmaDisciplina, Long> {
    boolean existsByTurmaIdAndDisciplinaId(Long turmaId, Long disciplinaId);
    List<TurmaDisciplina> findByTurmaIdOrderByDisciplinaNomeAsc(Long turmaId);
    List<TurmaDisciplina> findByProfessorIdOrderByTurmaNomeAscDisciplinaNomeAsc(Long professorId);
}
