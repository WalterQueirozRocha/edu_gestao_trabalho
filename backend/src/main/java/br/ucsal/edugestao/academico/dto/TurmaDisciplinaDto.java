package br.ucsal.edugestao.academico.dto;

import br.ucsal.edugestao.academico.entity.TurmaDisciplina;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TurmaDisciplinaDto(
        Long id,
        Long turmaId,
        String turmaNome,
        @NotNull(message = "Disciplina e obrigatoria")
        Long disciplinaId,
        String disciplinaNome,
        String disciplinaCodigo,
        @NotNull(message = "Professor e obrigatorio")
        Long professorId,
        String professorNome,
        @Min(value = 1, message = "Carga horaria deve ser positiva")
        int cargaHoraria
) {
    public static TurmaDisciplinaDto fromEntity(TurmaDisciplina td) {
        return new TurmaDisciplinaDto(
                td.getId(),
                td.getTurma().getId(),
                td.getTurma().getNome(),
                td.getDisciplina().getId(),
                td.getDisciplina().getNome(),
                td.getDisciplina().getCodigo(),
                td.getProfessor().getId(),
                td.getProfessor().getNome(),
                td.getCargaHoraria()
        );
    }
}
