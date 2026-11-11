package br.ucsal.edugestao.avaliacao.repository;

import br.ucsal.edugestao.avaliacao.entity.Boletim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoletimRepository extends JpaRepository<Boletim, Long> {
    Optional<Boletim> findByMatriculaIdAndEtapaId(Long matriculaId, Long etapaId);
    List<Boletim> findByMatriculaIdOrderByEtapaDataInicioAsc(Long matriculaId);
}
