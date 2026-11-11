package br.ucsal.edugestao.ocorrencia.dto;

import br.ucsal.edugestao.ocorrencia.entity.Ocorrencia;
import br.ucsal.edugestao.ocorrencia.entity.TipoOcorrencia;
import br.ucsal.edugestao.usuario.entity.Aluno;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record OcorrenciaDto(
        Long id,
        Long registradaPorId,
        String registradaPorNome,
        LocalDate data,
        @NotNull(message = "Tipo da ocorrencia e obrigatorio")
        TipoOcorrencia tipo,
        @NotBlank(message = "Descricao e obrigatoria")
        String descricao,
        @NotEmpty(message = "Informe ao menos um estudante envolvido")
        List<Long> alunosIds,
        List<String> alunosNomes
) {
    public static OcorrenciaDto fromEntity(Ocorrencia o) {
        return new OcorrenciaDto(
                o.getId(),
                o.getRegistradaPor().getId(),
                o.getRegistradaPor().getNome(),
                o.getData(),
                o.getTipo(),
                o.getDescricao(),
                o.getAlunos().stream().map(Aluno::getId).sorted().toList(),
                o.getAlunos().stream().map(Aluno::getNome).sorted().toList()
        );
    }
}
