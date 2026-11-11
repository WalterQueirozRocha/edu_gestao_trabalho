package br.ucsal.edugestao.academico.dto;

import br.ucsal.edugestao.academico.entity.Matricula;
import br.ucsal.edugestao.academico.entity.StatusMatricula;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MatriculaDto(
        Long id,
        @NotNull(message = "Aluno e obrigatorio")
        Long alunoId,
        String alunoNome,
        String alunoMatriculaCodigo,
        @NotNull(message = "Turma e obrigatoria")
        Long turmaId,
        String turmaNome,
        String turmaSerie,
        Integer anoLetivo,
        LocalDate dataMatricula,
        StatusMatricula status
) {
    public static MatriculaDto fromEntity(Matricula m) {
        return new MatriculaDto(
                m.getId(),
                m.getAluno().getId(),
                m.getAluno().getNome(),
                m.getAluno().getMatricula(),
                m.getTurma().getId(),
                m.getTurma().getNome(),
                m.getTurma().getSerie(),
                m.getTurma().getAnoLetivo().getAno(),
                m.getDataMatricula(),
                m.getStatus()
        );
    }
}
