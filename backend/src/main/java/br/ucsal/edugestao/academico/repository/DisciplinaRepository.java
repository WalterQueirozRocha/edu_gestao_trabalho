package br.ucsal.edugestao.academico.repository;

import br.ucsal.edugestao.academico.entity.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    boolean existsByCodigoIgnoreCase(String codigo);
    List<Disciplina> findAllByOrderByNomeAsc();
}
