package br.ucsal.edugestao.ocorrencia.repository;

import br.ucsal.edugestao.ocorrencia.entity.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {
    Optional<Denuncia> findByProtocoloIgnoreCase(String protocolo);
    List<Denuncia> findAllByOrderByCriadaEmDesc();
}
