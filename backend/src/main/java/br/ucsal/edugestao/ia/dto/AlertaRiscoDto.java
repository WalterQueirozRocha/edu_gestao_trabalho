package br.ucsal.edugestao.ia.dto;

import br.ucsal.edugestao.ia.entity.AlertaRisco;
import br.ucsal.edugestao.ia.entity.NivelRisco;
import br.ucsal.edugestao.ia.entity.StatusAlerta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AlertaRiscoDto(
        Long id,
        Long alunoId,
        String alunoNome,
        String alunoMatriculaCodigo,
        Long modeloId,
        String modeloVersao,
        NivelRisco nivel,
        BigDecimal probabilidade,
        String fatores,
        String sugestoes,
        LocalDateTime dataGeracao,
        StatusAlerta status
) {
    public static AlertaRiscoDto fromEntity(AlertaRisco a) {
        return new AlertaRiscoDto(
                a.getId(),
                a.getAluno().getId(),
                a.getAluno().getNome(),
                a.getAluno().getMatricula(),
                a.getModelo() != null ? a.getModelo().getId() : null,
                a.getModelo() != null ? a.getModelo().getVersao() : "Regras de Referencia (RN-02/RN-03)",
                a.getNivel(),
                a.getProbabilidade(),
                a.getFatores(),
                a.getSugestoes(),
                a.getDataGeracao(),
                a.getStatus()
        );
    }
}
