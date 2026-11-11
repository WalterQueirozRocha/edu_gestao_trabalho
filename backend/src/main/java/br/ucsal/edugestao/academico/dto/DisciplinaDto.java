package br.ucsal.edugestao.academico.dto;

import br.ucsal.edugestao.academico.entity.Disciplina;
import jakarta.validation.constraints.NotBlank;

public record DisciplinaDto(
        Long id,
        @NotBlank(message = "Nome da disciplina e obrigatorio")
        String nome,
        @NotBlank(message = "Codigo da disciplina e obrigatorio")
        String codigo
) {
    public static DisciplinaDto fromEntity(Disciplina d) {
        return new DisciplinaDto(d.getId(), d.getNome(), d.getCodigo());
    }
}
