package br.ucsal.edugestao.ocorrencia.dto;

import br.ucsal.edugestao.ocorrencia.entity.Denuncia;
import br.ucsal.edugestao.ocorrencia.entity.StatusAtendimento;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record DenunciaDto(
        Long id,
        String protocolo,
        @NotBlank(message = "Descricao da denuncia e obrigatoria")
        String descricao,
        boolean anonima,
        Long denuncianteId,
        String denuncianteNome,
        StatusAtendimento status,
        String medidas,
        String justificativaArquivamento,
        LocalDateTime criadaEm,
        LocalDateTime atualizadaEm
) {
    public static DenunciaDto fromEntity(Denuncia d, boolean ocultarDenunciante) {
        return new DenunciaDto(
                d.getId(),
                d.getProtocolo(),
                d.getDescricao(),
                d.isAnonima(),
                (d.isAnonima() || ocultarDenunciante || d.getDenunciante() == null) ? null : d.getDenunciante().getId(),
                (d.isAnonima() || ocultarDenunciante || d.getDenunciante() == null) ? "Anonimo" : d.getDenunciante().getNome(),
                d.getStatus(),
                d.getMedidas(),
                d.getJustificativaArquivamento(),
                d.getCriadaEm(),
                d.getAtualizadaEm()
        );
    }
}
