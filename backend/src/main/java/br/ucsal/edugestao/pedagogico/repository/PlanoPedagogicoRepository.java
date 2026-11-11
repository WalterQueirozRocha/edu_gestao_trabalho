package br.ucsal.edugestao.pedagogico.repository;

import br.ucsal.edugestao.pedagogico.entity.PlanoPedagogico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanoPedagogicoRepository extends JpaRepository<PlanoPedagogico, Long> {
    List<PlanoPedagogico> findAllByOrderByDataRevisaoAsc();

    @Query("SELECT p FROM PlanoPedagogico p JOIN p.profissionaisAutorizados pa WHERE pa.id = :profissionalId ORDER BY p.dataRevisao ASC")
    List<PlanoPedagogico> findByProfissionalAutorizadoId(@Param("profissionalId") Long profissionalId);
}
