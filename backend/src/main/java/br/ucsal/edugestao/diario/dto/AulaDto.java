package br.ucsal.edugestao.diario.dto;

import br.ucsal.edugestao.diario.entity.Aula;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AulaDto(
        Long id,
        @NotNull(message = "Turma/disciplina e obrigatoria")
        Long turmaDisciplinaId,
        String turmaNome,
        String disciplinaNome,
        @NotNull(message = "Data da aula e obrigatoria")
        LocalDate data,
        @NotBlank(message = "Conteudo da aula e obrigatorio")
        String conteudo,
        @Min(value = 1, message = "Quantidade de horas deve ser positiva")
        int horas
) {
    public static AulaDto fromEntity(Aula a) {
        return new AulaDto(
                a.getId(),
                a.getTurmaDisciplina().getId(),
                a.getTurmaDisciplina().getTurma().getNome(),
                a.getTurmaDisciplina().getDisciplina().getNome(),
                a.getData(),
                a.getConteudo(),
                a.getHoras()
        );
    }
}
