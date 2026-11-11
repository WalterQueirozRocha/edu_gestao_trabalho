package br.ucsal.edugestao.usuario.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record VinculoResponsaveisRequest(
        @NotNull(message = "Lista de responsaveis e obrigatoria")
        List<Long> responsaveisIds
) {
}
