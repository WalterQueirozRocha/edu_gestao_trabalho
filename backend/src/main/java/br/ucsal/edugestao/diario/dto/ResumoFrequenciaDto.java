package br.ucsal.edugestao.diario.dto;

import java.math.BigDecimal;

public record ResumoFrequenciaDto(
        Long matriculaId,
        Long alunoId,
        String alunoNome,
        int horasRegistradas,
        int horasPresentes,
        int horasFaltas,
        BigDecimal percentualPresenca,
        BigDecimal frequenciaMinimaExigida,
        boolean aprovadoPorFrequencia,
        boolean alertaPreventivoAbaixo80
) {
}
