package br.ucsal.edugestao.ia.dto;

import java.math.BigDecimal;

public record FeaturesAlunoDto(
        Long alunoId,
        String alunoNome,
        BigDecimal mediaGeral,
        BigDecimal menorMediaDisciplina,
        int disciplinasAbaixoMedia,
        BigDecimal frequenciaGeral,
        BigDecimal quedaFrequencia,
        long numOcorrencias,
        BigDecimal mediaMinimaRegimento
) {
}
