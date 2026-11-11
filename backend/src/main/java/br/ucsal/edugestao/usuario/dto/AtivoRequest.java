package br.ucsal.edugestao.usuario.dto;

import jakarta.validation.constraints.NotNull;

public record AtivoRequest(
        @NotNull(message = "Indicador ativo e obrigatorio")
        Boolean ativo
) {
}
