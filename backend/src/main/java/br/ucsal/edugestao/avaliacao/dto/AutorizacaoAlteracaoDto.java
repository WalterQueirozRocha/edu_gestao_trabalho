package br.ucsal.edugestao.avaliacao.dto;

import br.ucsal.edugestao.avaliacao.entity.AutorizacaoAlteracao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AutorizacaoAlteracaoDto(
        Long id,
        @NotNull(message = "Etapa e obrigatoria")
        Long etapaId,
        String etapaNome,
        @NotNull(message = "Turma/disciplina e obrigatoria")
        Long turmaDisciplinaId,
        String turmaNome,
        String disciplinaNome,
        Long coordenadorId,
        String coordenadorNome,
        @NotBlank(message = "Motivo da autorizacao e obrigatorio")
        String motivo,
        LocalDateTime criadaEm,
        LocalDateTime validaAte,
        boolean utilizada
) {
    public static AutorizacaoAlteracaoDto fromEntity(AutorizacaoAlteracao a) {
        return new AutorizacaoAlteracaoDto(
                a.getId(),
                a.getEtapa().getId(),
                a.getEtapa().getNome(),
                a.getTurmaDisciplina().getId(),
                a.getTurmaDisciplina().getTurma().getNome(),
                a.getTurmaDisciplina().getDisciplina().getNome(),
                a.getCoordenador().getId(),
                a.getCoordenador().getNome(),
                a.getMotivo(),
                a.getCriadaEm(),
                a.getValidaAte(),
                a.isUtilizada()
        );
    }
}
