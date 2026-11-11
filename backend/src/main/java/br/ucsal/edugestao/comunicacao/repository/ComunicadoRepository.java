package br.ucsal.edugestao.comunicacao.repository;

import br.ucsal.edugestao.comunicacao.entity.Comunicado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComunicadoRepository extends JpaRepository<Comunicado, Long> {
    List<Comunicado> findAllByOrderByDataPublicacaoDesc();
}
