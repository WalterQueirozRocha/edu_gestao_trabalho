package br.ucsal.edugestao.avaliacao.dto;

import br.ucsal.edugestao.avaliacao.entity.Situacao;

import java.math.BigDecimal;
import java.util.List;

public record ResultadoAlunoTurmaDto(
        Long matriculaId,
        Long alunoId,
        String alunoNome,
        String alunoMatriculaCodigo,
        BigDecimal mediaGeral,
        BigDecimal percentualFrequenciaGeral,
        Situacao situacaoCalculada,
        List<BoletimDisciplinaDto> disciplinas
) {
}
