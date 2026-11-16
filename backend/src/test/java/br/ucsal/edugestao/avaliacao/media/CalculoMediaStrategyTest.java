package br.ucsal.edugestao.avaliacao.media;

import br.ucsal.edugestao.academico.entity.FormulaMedia;
import br.ucsal.edugestao.avaliacao.entity.Avaliacao;
import br.ucsal.edugestao.avaliacao.entity.Nota;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Padrao Strategy (RN-03): a formula do ano letivo escolhe a estrategia de calculo
class CalculoMediaStrategyTest {

    private Nota nota(String valor, String peso) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setPeso(new BigDecimal(peso));
        Nota nota = new Nota();
        nota.setAvaliacao(avaliacao);
        nota.setValor(new BigDecimal(valor));
        return nota;
    }

    @Test
    @DisplayName("Cada formula resolve para a sua estrategia")
    void resolveEstrategiaPorFormula() {
        assertThat(CalculoMediaStrategy.para(FormulaMedia.PONDERADA)).isInstanceOf(MediaPonderadaStrategy.class);
        assertThat(CalculoMediaStrategy.para(FormulaMedia.ARITMETICA)).isInstanceOf(MediaAritmeticaStrategy.class);
        assertThat(CalculoMediaStrategy.para(null)).isInstanceOf(MediaPonderadaStrategy.class);
    }

    @Test
    @DisplayName("Estrategias diferentes dao resultados diferentes para as mesmas notas")
    void estrategiasIntercambiaveis() {
        List<Nota> notas = List.of(nota("8.00", "2"), nota("5.00", "1"));

        assertThat(CalculoMediaStrategy.para(FormulaMedia.PONDERADA).calcular(notas)).isEqualByComparingTo("7.00");
        assertThat(CalculoMediaStrategy.para(FormulaMedia.ARITMETICA).calcular(notas)).isEqualByComparingTo("6.50");
    }
}
