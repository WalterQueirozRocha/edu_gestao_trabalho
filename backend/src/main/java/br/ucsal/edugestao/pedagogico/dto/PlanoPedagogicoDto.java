package br.ucsal.edugestao.pedagogico.dto;

import br.ucsal.edugestao.pedagogico.entity.PlanoPedagogico;
import br.ucsal.edugestao.usuario.entity.ProfissionalApoio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PlanoPedagogicoDto(
        Long id,
        @NotNull(message = "Estudante e obrigatorio")
        Long alunoId,
        String alunoNome,
        String alunoMatriculaCodigo,
        Long coordenadorId,
        String coordenadorNome,
        Long alertaOrigemId,
        @NotBlank(message = "Metas pedagogicas sao obrigatorias")
        String metas,
        @NotBlank(message = "Adaptacoes pedagogicas sao obrigatorias")
        String adaptacoes,
        @NotBlank(message = "Estrategias pedagogicas sao obrigatorias")
        String estrategias,
        @NotNull(message = "Data de revisao e obrigatoria")
        LocalDate dataRevisao,
        LocalDateTime criadoEm,
        List<Long> profissionaisIds,
        List<String> profissionaisNomes
) {
    public static PlanoPedagogicoDto fromEntity(PlanoPedagogico p) {
        return new PlanoPedagogicoDto(
                p.getId(),
                p.getAluno().getId(),
                p.getAluno().getNome(),
                p.getAluno().getMatricula(),
                p.getCoordenador().getId(),
                p.getCoordenador().getNome(),
                p.getAlertaOrigemId(),
                p.getMetas(),
                p.getAdaptacoes(),
                p.getEstrategias(),
                p.getDataRevisao(),
                p.getCriadoEm(),
                p.getProfissionaisAutorizados().stream().map(ProfissionalApoio::getId).sorted().toList(),
                p.getProfissionaisAutorizados().stream().map(ProfissionalApoio::getNome).sorted().toList()
        );
    }
}
