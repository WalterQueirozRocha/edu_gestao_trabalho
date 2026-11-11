package br.ucsal.edugestao.diario.repository;

import br.ucsal.edugestao.diario.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AulaRepository extends JpaRepository<Aula, Long> {
    List<Aula> findByTurmaDisciplinaIdOrderByDataDesc(Long turmaDisciplinaId);
}
