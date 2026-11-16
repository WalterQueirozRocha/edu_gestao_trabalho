package br.ucsal.edugestao.avaliacao.media;

import br.ucsal.edugestao.avaliacao.entity.Nota;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

// RN-03: media aritmetica simples, ignorando os pesos das avaliacoes
public final class MediaAritmeticaStrategy implements CalculoMediaStrategy {

    static final MediaAritmeticaStrategy INSTANCIA = new MediaAritmeticaStrategy();

    private MediaAritmeticaStrategy() {
    }

    @Override
    public BigDecimal calcular(List<Nota> notas) {
        if (notas == null || notas.isEmpty()) {
            return ZERO;
        }
        BigDecimal soma = notas.stream().map(Nota::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
    }
}
