package br.ucsal.edugestao.avaliacao.repository;

import br.ucsal.edugestao.avaliacao.entity.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByAvaliacaoIdOrderByMatriculaAlunoNomeAsc(Long avaliacaoId);
    List<Nota> findByMatriculaId(Long matriculaId);
    List<Nota> findByMatriculaIdAndAvaliacaoEtapaId(Long matriculaId, Long etapaId);
    Optional<Nota> findByAvaliacaoIdAndMatriculaId(Long avaliacaoId, Long matriculaId);
}
