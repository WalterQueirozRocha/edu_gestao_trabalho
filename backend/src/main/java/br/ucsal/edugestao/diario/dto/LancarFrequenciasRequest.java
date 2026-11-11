package br.ucsal.edugestao.diario.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record LancarFrequenciasRequest(
        @NotEmpty(message = "A lista de frequencias nao pode estar vazia")
        @Valid
        List<FrequenciaItemDto> frequencias
) {
}
