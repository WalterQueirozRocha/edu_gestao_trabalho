package br.ucsal.edugestao.avaliacao.repository;

import br.ucsal.edugestao.avaliacao.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByTurmaDisciplinaIdAndEtapaIdOrderByDataAsc(Long turmaDisciplinaId, Long etapaId);
    List<Avaliacao> findByTurmaDisciplinaIdOrderByDataAsc(Long turmaDisciplinaId);
    List<Avaliacao> findByEtapaIdOrderByDataAsc(Long etapaId);
}
