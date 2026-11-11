package br.ucsal.edugestao.avaliacao.dto;

import br.ucsal.edugestao.avaliacao.entity.Nota;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record NotaItemDto(
        Long id,
        @NotNull(message = "Matricula e obrigatoria")
        Long matriculaId,
        Long alunoId,
        String alunoNome,
        @NotNull(message = "Valor da nota e obrigatorio")
        @DecimalMin(value = "0.0", message = "Nota minima e 0")
        @DecimalMax(value = "10.0", message = "Nota maxima e 10")
        BigDecimal valor,
        LocalDateTime dataLancamento
) {
    public static NotaItemDto fromEntity(Nota n) {
        return new NotaItemDto(
                n.getId(),
                n.getMatricula().getId(),
                n.getMatricula().getAluno().getId(),
                n.getMatricula().getAluno().getNome(),
                n.getValor(),
                n.getDataLancamento()
        );
    }
}
