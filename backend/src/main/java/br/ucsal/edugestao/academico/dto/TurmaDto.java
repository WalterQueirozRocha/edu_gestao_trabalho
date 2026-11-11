package br.ucsal.edugestao.academico.dto;

import br.ucsal.edugestao.academico.entity.Turma;
import br.ucsal.edugestao.academico.entity.Turno;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TurmaDto(
        Long id,
        @NotNull(message = "Ano letivo e obrigatorio")
        Long anoLetivoId,
        Integer anoLetivoAno,
        @NotBlank(message = "Nome da turma e obrigatorio")
        String nome,
        @NotBlank(message = "Serie e obrigatoria")
        String serie,
        @NotNull(message = "Turno e obrigatorio")
        Turno turno
) {
    public static TurmaDto fromEntity(Turma t) {
        return new TurmaDto(
                t.getId(),
                t.getAnoLetivo().getId(),
                t.getAnoLetivo().getAno(),
                t.getNome(),
                t.getSerie(),
                t.getTurno()
        );
    }
}
