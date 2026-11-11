package br.ucsal.edugestao.usuario.repository;

import br.ucsal.edugestao.usuario.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    boolean existsByMatricula(String matricula);

    @Query("SELECT a FROM Aluno a JOIN a.responsaveis r WHERE r.id = :responsavelId ORDER BY a.nome ASC")
    List<Aluno> findByResponsavelId(@Param("responsavelId") Long responsavelId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Aluno a JOIN a.responsaveis r WHERE a.id = :alunoId AND r.id = :responsavelId")
    boolean existsVinculoResponsavel(@Param("alunoId") Long alunoId, @Param("responsavelId") Long responsavelId);
}
