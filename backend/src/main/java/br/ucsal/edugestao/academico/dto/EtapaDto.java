package br.ucsal.edugestao.academico.dto;

import br.ucsal.edugestao.academico.entity.Etapa;
import br.ucsal.edugestao.academico.entity.StatusEtapa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EtapaDto(
        Long id,
        @NotNull(message = "Ano letivo e obrigatorio")
        Long anoLetivoId,
        Integer anoLetivoAno,
        @NotBlank(message = "Nome da etapa e obrigatorio")
        String nome,
        @NotNull(message = "Data de inicio e obrigatoria")
        LocalDate dataInicio,
        @NotNull(message = "Data de fim e obrigatoria")
        LocalDate dataFim,
        StatusEtapa status
) {
    public static EtapaDto fromEntity(Etapa e) {
        return new EtapaDto(
                e.getId(),
                e.getAnoLetivo().getId(),
                e.getAnoLetivo().getAno(),
                e.getNome(),
                e.getDataInicio(),
                e.getDataFim(),
                e.getStatus()
        );
    }
}
