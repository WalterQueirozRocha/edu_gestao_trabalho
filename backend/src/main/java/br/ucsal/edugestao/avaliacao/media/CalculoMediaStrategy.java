package br.ucsal.edugestao.avaliacao.media;

import br.ucsal.edugestao.academico.entity.FormulaMedia;
import br.ucsal.edugestao.avaliacao.entity.Nota;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Padrao Strategy (RN-03): cada formula de media do regimento e uma estrategia
 * intercambiavel. Para incluir uma nova formula basta criar outra implementacao
 * e registra-la em {@link #para(FormulaMedia)}, sem alterar quem calcula a media.
 */
public interface CalculoMediaStrategy {

    BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    BigDecimal calcular(List<Nota> notas);

    // Sem formula configurada, vale a media ponderada (padrao do regimento)
    static CalculoMediaStrategy para(FormulaMedia formula) {
        if (formula == FormulaMedia.ARITMETICA) {
            return MediaAritmeticaStrategy.INSTANCIA;
        }
        return MediaPonderadaStrategy.INSTANCIA;
    }
}
