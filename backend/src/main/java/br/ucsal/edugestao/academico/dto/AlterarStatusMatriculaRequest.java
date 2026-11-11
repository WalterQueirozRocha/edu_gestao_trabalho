package br.ucsal.edugestao.academico.dto;

import br.ucsal.edugestao.academico.entity.StatusMatricula;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusMatriculaRequest(
        @NotNull(message = "Status da matricula e obrigatorio")
        StatusMatricula status
) {
}
