package br.ucsal.edugestao.diario.dto;

import br.ucsal.edugestao.diario.entity.Frequencia;
import jakarta.validation.constraints.NotNull;

public record FrequenciaItemDto(
        Long id,
        @NotNull(message = "Matricula e obrigatoria")
        Long matriculaId,
        Long alunoId,
        String alunoNome,
        @NotNull(message = "Indicador de presenca e obrigatorio")
        Boolean presente,
        String justificativa
) {
    public static FrequenciaItemDto fromEntity(Frequencia f) {
        return new FrequenciaItemDto(
                f.getId(),
                f.getMatricula().getId(),
                f.getMatricula().getAluno().getId(),
                f.getMatricula().getAluno().getNome(),
                f.isPresente(),
                f.getJustificativa()
        );
    }
}
