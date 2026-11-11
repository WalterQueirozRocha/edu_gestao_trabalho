package br.ucsal.edugestao.ia.dto;

import br.ucsal.edugestao.ia.entity.ModeloPreditivo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ModeloPreditivoDto(
        Long id,
        String versao,
        LocalDate dataTreinamento,
        BigDecimal acuracia,
        boolean ativo
) {
    public static ModeloPreditivoDto fromEntity(ModeloPreditivo m) {
        return new ModeloPreditivoDto(
                m.getId(),
                m.getVersao(),
                m.getDataTreinamento(),
                m.getAcuracia(),
                m.isAtivo()
        );
    }
}
