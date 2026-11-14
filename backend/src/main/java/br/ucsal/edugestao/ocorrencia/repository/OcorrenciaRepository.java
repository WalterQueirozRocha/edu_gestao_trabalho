package br.ucsal.edugestao.ocorrencia.repository;

import br.ucsal.edugestao.ocorrencia.entity.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
    List<Ocorrencia> findAllByOrderByDataDescIdDesc();

    List<Ocorrencia> findByRegistradaPorIdOrderByDataDescIdDesc(Long registradaPorId);

    @Query("SELECT COUNT(o) FROM Ocorrencia o JOIN o.alunos a WHERE a.id = :alunoId")
    long countByAlunoId(@Param("alunoId") Long alunoId);
}
