package br.ucsal.edugestao.academico.repository;

import br.ucsal.edugestao.academico.entity.AnoLetivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnoLetivoRepository extends JpaRepository<AnoLetivo, Long> {
    boolean existsByAno(int ano);
    List<AnoLetivo> findAllByOrderByAnoDesc();
    Optional<AnoLetivo> findFirstByOrderByAnoDesc();
}
