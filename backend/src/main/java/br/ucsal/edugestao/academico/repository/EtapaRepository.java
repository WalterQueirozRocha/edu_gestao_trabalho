package br.ucsal.edugestao.academico.repository;

import br.ucsal.edugestao.academico.entity.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EtapaRepository extends JpaRepository<Etapa, Long> {
    List<Etapa> findByAnoLetivoIdOrderByDataInicioAsc(Long anoLetivoId);
    List<Etapa> findAllByOrderByDataInicioAsc();
}
