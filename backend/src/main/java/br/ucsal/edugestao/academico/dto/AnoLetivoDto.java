package br.ucsal.edugestao.academico.dto;

import br.ucsal.edugestao.academico.entity.AnoLetivo;
import br.ucsal.edugestao.academico.entity.FormulaMedia;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnoLetivoDto(
        Long id,
        @Min(value = 2000, message = "Ano invalido")
        int ano,
        @NotNull(message = "Data de inicio e obrigatoria")
        LocalDate dataInicio,
        @NotNull(message = "Data de fim e obrigatoria")
        LocalDate dataFim,
        @NotNull(message = "Media minima e obrigatoria")
        @DecimalMin(value = "0.0") @DecimalMax(value = "10.0")
        BigDecimal mediaMinima,
        @NotNull(message = "Frequencia minima e obrigatoria")
        @DecimalMin(value = "0.0") @DecimalMax(value = "100.0")
        BigDecimal frequenciaMinima,
        FormulaMedia formulaMedia
) {
    public static AnoLetivoDto fromEntity(AnoLetivo a) {
        return new AnoLetivoDto(
                a.getId(),
                a.getAno(),
                a.getDataInicio(),
                a.getDataFim(),
                a.getMediaMinima(),
                a.getFrequenciaMinima(),
                a.getFormulaMedia()
        );
    }
}
