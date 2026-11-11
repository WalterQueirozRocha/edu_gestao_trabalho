package br.ucsal.edugestao.ocorrencia.dto;

import br.ucsal.edugestao.ocorrencia.entity.StatusAtendimento;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TratarDenunciaRequest(
        @NotNull(message = "Status de atendimento e obrigatorio")
        StatusAtendimento status,
        String medidas,
        String justificativaArquivamento,
        Boolean requerAcompanhamentoPedagogico,
        Long alunoIdPlano,
        String metasPlano,
        String adaptacoesPlano,
        String estrategiasPlano,
        LocalDate dataRevisaoPlano
) {
}
