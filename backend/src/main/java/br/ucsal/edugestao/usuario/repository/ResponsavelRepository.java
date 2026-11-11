package br.ucsal.edugestao.usuario.repository;

import br.ucsal.edugestao.usuario.entity.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {
    boolean existsByCpf(String cpf);
}
