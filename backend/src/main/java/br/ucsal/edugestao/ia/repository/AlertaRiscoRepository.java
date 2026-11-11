package br.ucsal.edugestao.ia.repository;

import br.ucsal.edugestao.ia.entity.AlertaRisco;
import br.ucsal.edugestao.ia.entity.StatusAlerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaRiscoRepository extends JpaRepository<AlertaRisco, Long> {
    List<AlertaRisco> findAllByOrderByProbabilidadeDescDataGeracaoDesc();
    List<AlertaRisco> findByAlunoIdAndStatus(Long alunoId, StatusAlerta status);
}
