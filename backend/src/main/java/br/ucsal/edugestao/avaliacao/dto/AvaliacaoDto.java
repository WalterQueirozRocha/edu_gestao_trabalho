package br.ucsal.edugestao.avaliacao.dto;

import br.ucsal.edugestao.avaliacao.entity.Avaliacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AvaliacaoDto(
        Long id,
        @NotNull(message = "Turma/disciplina e obrigatoria")
        Long turmaDisciplinaId,
        String turmaNome,
        String disciplinaNome,
        @NotNull(message = "Etapa e obrigatoria")
        Long etapaId,
        String etapaNome,
        @NotBlank(message = "Descricao e obrigatoria")
        String descricao,
        @NotNull(message = "Data da avaliacao e obrigatoria")
        LocalDate data,
        @NotNull(message = "Peso da avaliacao e obrigatorio")
        @DecimalMin(value = "0.01", message = "Peso deve ser maior que zero")
        BigDecimal peso
) {
    public static AvaliacaoDto fromEntity(Avaliacao a) {
        return new AvaliacaoDto(
                a.getId(),
                a.getTurmaDisciplina().getId(),
                a.getTurmaDisciplina().getTurma().getNome(),
                a.getTurmaDisciplina().getDisciplina().getNome(),
                a.getEtapa().getId(),
                a.getEtapa().getNome(),
                a.getDescricao(),
                a.getData(),
                a.getPeso()
        );
    }
}
