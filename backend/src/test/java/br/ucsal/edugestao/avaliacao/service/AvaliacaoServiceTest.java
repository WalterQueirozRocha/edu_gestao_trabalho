package br.ucsal.edugestao.avaliacao.service;

import br.ucsal.edugestao.academico.entity.FormulaMedia;
import br.ucsal.edugestao.avaliacao.entity.Avaliacao;
import br.ucsal.edugestao.avaliacao.entity.Nota;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// RN-03: formula da media e pesos configurados conforme o regimento da escola
@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @InjectMocks
    private AvaliacaoService service;

    private Nota nota(String valor, String peso) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setPeso(new BigDecimal(peso));
        Nota nota = new Nota();
        nota.setAvaliacao(avaliacao);
        nota.setValor(new BigDecimal(valor));
        return nota;
    }

    @Test
    @DisplayName("Media ponderada respeita os pesos das avaliacoes")
    void mediaPonderada() {
        // (8 x 2 + 5 x 1) / 3 = 7,00
        List<Nota> notas = List.of(nota("8.00", "2"), nota("5.00", "1"));

        assertThat(service.calcularMediaPonderada(notas)).isEqualByComparingTo("7.00");
    }

    @Test
    @DisplayName("Media ponderada arredonda para duas casas (HALF_UP)")
    void mediaPonderadaArredonda() {
        // (7 + 8 + 8) / 3 = 7,666... -> 7,67
        List<Nota> notas = List.of(nota("7.00", "1"), nota("8.00", "1"), nota("8.00", "1"));

        assertThat(service.calcularMediaPonderada(notas)).isEqualByComparingTo("7.67");
    }

    @Test
    @DisplayName("Media aritmetica ignora os pesos")
    void mediaAritmetica() {
        List<Nota> notas = List.of(nota("8.00", "2"), nota("5.00", "1"));

        assertThat(service.calcularMedia(notas, FormulaMedia.ARITMETICA)).isEqualByComparingTo("6.50");
    }

    @Test
    @DisplayName("Formula PONDERADA usa os pesos")
    void formulaPonderada() {
        List<Nota> notas = List.of(nota("8.00", "2"), nota("5.00", "1"));

        assertThat(service.calcularMedia(notas, FormulaMedia.PONDERADA)).isEqualByComparingTo("7.00");
    }

    @Test
    @DisplayName("Sem formula configurada, usa media ponderada")
    void formulaNulaUsaPonderada() {
        List<Nota> notas = List.of(nota("8.00", "2"), nota("5.00", "1"));

        assertThat(service.calcularMedia(notas, null)).isEqualByComparingTo("7.00");
    }

    @Test
    @DisplayName("Sem notas, a media e zero")
    void semNotas() {
        assertThat(service.calcularMediaPonderada(List.of())).isEqualByComparingTo("0.00");
        assertThat(service.calcularMediaPonderada(null)).isEqualByComparingTo("0.00");
        assertThat(service.calcularMedia(List.of(), FormulaMedia.ARITMETICA)).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Pesos somando zero nao causam divisao por zero")
    void pesosZerados() {
        List<Nota> notas = List.of(nota("8.00", "0"), nota("5.00", "0"));

        assertThat(service.calcularMediaPonderada(notas)).isEqualByComparingTo("0.00");
    }
}
