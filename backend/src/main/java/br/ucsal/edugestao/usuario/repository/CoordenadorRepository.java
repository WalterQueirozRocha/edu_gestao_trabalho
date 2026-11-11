package br.ucsal.edugestao.usuario.repository;

import br.ucsal.edugestao.usuario.entity.Coordenador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoordenadorRepository extends JpaRepository<Coordenador, Long> {
}
