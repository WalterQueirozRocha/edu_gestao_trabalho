package br.ucsal.edugestao.ia.dto;

import java.time.LocalDate;

public record ConverterAlertaRequest(
        String metas,
        String adaptacoes,
        String estrategias,
        LocalDate dataRevisao
) {
}
