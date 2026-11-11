package br.ucsal.edugestao.avaliacao.dto;

import br.ucsal.edugestao.academico.entity.StatusEtapa;
import br.ucsal.edugestao.avaliacao.entity.Situacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BoletimEtapaDto(
        Long boletimId,
        Long matriculaId,
        Long alunoId,
        String alunoNome,
        String alunoMatriculaCodigo,
        String turmaNome,
        String turmaSerie,
        int anoLetivo,
        Long etapaId,
        String etapaNome,
        StatusEtapa etapaStatus,
        LocalDate dataPublicacao,
        Situacao situacao,
        BigDecimal mediaMinimaExigida,
        BigDecimal frequenciaMinimaExigida,
        BigDecimal mediaGeralEtapa,
        BigDecimal percentualFrequenciaGeral,
        List<BoletimDisciplinaDto> disciplinas
) {
}
