package br.ucsal.edugestao.usuario.repository;

import br.ucsal.edugestao.usuario.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    boolean existsByRegistroFuncional(String registroFuncional);
}
