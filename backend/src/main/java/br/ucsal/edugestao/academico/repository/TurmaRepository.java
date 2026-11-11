package br.ucsal.edugestao.academico.repository;

import br.ucsal.edugestao.academico.entity.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    boolean existsByAnoLetivoIdAndNomeIgnoreCase(Long anoLetivoId, String nome);
    List<Turma> findByAnoLetivoIdOrderByNomeAsc(Long anoLetivoId);
    List<Turma> findAllByOrderByNomeAsc();
}
