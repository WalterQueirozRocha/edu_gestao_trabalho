package br.ucsal.edugestao.diario.repository;

import br.ucsal.edugestao.diario.entity.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {
    List<Frequencia> findByAulaIdOrderByMatriculaAlunoNomeAsc(Long aulaId);
    List<Frequencia> findByMatriculaId(Long matriculaId);
    Optional<Frequencia> findByAulaIdAndMatriculaId(Long aulaId, Long matriculaId);
}
