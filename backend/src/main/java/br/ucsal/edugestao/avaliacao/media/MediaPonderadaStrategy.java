package br.ucsal.edugestao.avaliacao.media;

import br.ucsal.edugestao.avaliacao.entity.Nota;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

// RN-03: media ponderada pelos pesos das avaliacoes da etapa
public final class MediaPonderadaStrategy implements CalculoMediaStrategy {

    static final MediaPonderadaStrategy INSTANCIA = new MediaPonderadaStrategy();

    private MediaPonderadaStrategy() {
    }

    @Override
    public BigDecimal calcular(List<Nota> notas) {
        if (notas == null || notas.isEmpty()) {
            return ZERO;
        }
        BigDecimal somaNotaPeso = BigDecimal.ZERO;
        BigDecimal somaPesos = BigDecimal.ZERO;
        for (Nota n : notas) {
            BigDecimal peso = n.getAvaliacao().getPeso();
            somaNotaPeso = somaNotaPeso.add(n.getValor().multiply(peso));
            somaPesos = somaPesos.add(peso);
        }
        if (somaPesos.compareTo(BigDecimal.ZERO) == 0) {
            return ZERO;
        }
        return somaNotaPeso.divide(somaPesos, 2, RoundingMode.HALF_UP);
    }
}
