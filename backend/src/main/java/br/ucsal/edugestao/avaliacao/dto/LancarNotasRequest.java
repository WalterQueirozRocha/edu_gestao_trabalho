package br.ucsal.edugestao.avaliacao.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record LancarNotasRequest(
        @NotEmpty(message = "A lista de notas nao pode estar vazia")
        @Valid
        List<NotaItemDto> notas
) {
}
