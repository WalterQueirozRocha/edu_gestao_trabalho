package br.ucsal.edugestao.avaliacao.dto;

import java.math.BigDecimal;

public record BoletimDisciplinaDto(
        Long turmaDisciplinaId,
        String disciplinaNome,
        String disciplinaCodigo,
        BigDecimal mediaPonderada,
        BigDecimal percentualFrequencia,
        boolean aprovadoMedia,
        boolean aprovadoFrequencia
) {
}
