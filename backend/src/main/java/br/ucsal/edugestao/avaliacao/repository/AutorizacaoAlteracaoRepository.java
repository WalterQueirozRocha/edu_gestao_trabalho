package br.ucsal.edugestao.avaliacao.repository;

import br.ucsal.edugestao.avaliacao.entity.AutorizacaoAlteracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AutorizacaoAlteracaoRepository extends JpaRepository<AutorizacaoAlteracao, Long> {
    Optional<AutorizacaoAlteracao> findFirstByEtapaIdAndTurmaDisciplinaIdAndUtilizadaFalseAndValidaAteAfterOrderByCriadaEmDesc(
            Long etapaId,
            Long turmaDisciplinaId,
            LocalDateTime agora
    );

    List<AutorizacaoAlteracao> findAllByOrderByCriadaEmDesc();
}
