package br.ucsal.edugestao.pedagogico.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AutorizarProfissionaisRequest(
        @NotNull(message = "Lista de profissionais e obrigatoria")
        List<Long> profissionaisIds
) {
}
