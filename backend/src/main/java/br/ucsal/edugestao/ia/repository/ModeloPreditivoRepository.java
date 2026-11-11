package br.ucsal.edugestao.ia.repository;

import br.ucsal.edugestao.ia.entity.ModeloPreditivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModeloPreditivoRepository extends JpaRepository<ModeloPreditivo, Long> {
    Optional<ModeloPreditivo> findFirstByAtivoTrueOrderByDataTreinamentoDescIdDesc();
}
