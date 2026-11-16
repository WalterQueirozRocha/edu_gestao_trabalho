package br.ucsal.edugestao.ia.service;

import br.ucsal.edugestao.ia.dto.FeaturesAlunoDto;
import br.ucsal.edugestao.ia.entity.NivelRisco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

// RF-11, RN-09: classificacao de risco e regras de referencia (modelo nao treinado neste teste)
@ExtendWith(MockitoExtension.class)
class PredicaoRiscoServiceTest {

    private static final BigDecimal MEDIA_MINIMA = new BigDecimal("6.00");

    @InjectMocks
    private PredicaoRiscoService service;

    private FeaturesAlunoDto aluno(String media, int disciplinasAbaixo, String frequencia, String queda, long ocorrencias) {
        return new FeaturesAlunoDto(
                1L,
                "Aluno Teste",
                new BigDecimal(media),
                new BigDecimal(media),
                disciplinasAbaixo,
                new BigDecimal(frequencia),
                new BigDecimal(queda),
                ocorrencias,
                MEDIA_MINIMA
        );
    }

    @ParameterizedTest(name = "Probabilidade {0} -> {1}")
    @CsvSource({
            "0.0000, BAIXO",
            "0.2999, BAIXO",
            "0.3000, MEDIO",
            "0.6000, MEDIO",
            "0.6001, ALTO",
            "0.9800, ALTO"
    })
    @DisplayName("Faixas de risco: baixo < 30%, medio 30% a 60%, alto > 60%")
    void classificaNivelDeRisco(String probabilidade, NivelRisco esperado) {
        assertThat(service.classificarNivelRisco(new BigDecimal(probabilidade))).isEqualTo(esperado);
    }

    @Test
    @DisplayName("Aluno sem fatores de risco fica com risco baixo")
    void alunoSemRisco() {
        FeaturesAlunoDto f = aluno("8.00", 0, "95.00", "0.00", 0);

        BigDecimal prob = service.calcularProbabilidadeRisco(f);

        assertThat(prob).isEqualByComparingTo("0.0500");
        assertThat(service.classificarNivelRisco(prob)).isEqualTo(NivelRisco.BAIXO);
    }

    @Test
    @DisplayName("Frequencia abaixo de 80% gera pelo menos risco medio (alerta preventivo)")
    void frequenciaAbaixoDe80GeraRiscoMedio() {
        FeaturesAlunoDto f = aluno("8.00", 0, "78.00", "0.00", 0);

        BigDecimal prob = service.calcularProbabilidadeRisco(f);

        assertThat(service.classificarNivelRisco(prob)).isEqualTo(NivelRisco.MEDIO);
    }

    @Test
    @DisplayName("Varios fatores combinados geram risco alto, limitado a 98%")
    void variosFatoresRiscoAltoComTeto() {
        FeaturesAlunoDto f = aluno("5.00", 3, "70.00", "15.00", 3);

        BigDecimal prob = service.calcularProbabilidadeRisco(f);

        assertThat(prob).isEqualByComparingTo("0.9800");
        assertThat(service.classificarNivelRisco(prob)).isEqualTo(NivelRisco.ALTO);
    }

    @Test
    @DisplayName("Sem modelo treinado, frequencia abaixo de 75% indica tendencia de evasao")
    void tendenciaEvasao() {
        assertThat(service.identificarTendencia(aluno("7.00", 0, "70.00", "0.00", 0))).isEqualTo("evasao");
        assertThat(service.identificarTendencia(aluno("7.00", 0, "90.00", "12.00", 0))).isEqualTo("evasao");
    }

    @Test
    @DisplayName("Sem modelo treinado, notas baixas com boa frequencia indicam tendencia de retencao")
    void tendenciaRetencao() {
        assertThat(service.identificarTendencia(aluno("4.00", 2, "92.00", "0.00", 0))).isEqualTo("retencao");
    }

    @Test
    @DisplayName("Fatores contribuintes vem ordenados pelo maior impacto")
    void fatoresOrdenados() {
        String fatores = service.identificarFatoresContribuintes(aluno("7.00", 0, "70.00", "0.00", 1));

        assertThat(fatores).startsWith("Frequencia abaixo do minimo legal de 75%");
        assertThat(fatores).contains("ocorrencia");
    }

    @Test
    @DisplayName("RN-08/RN-09: sugestoes sao pedagogicas, sem termos clinicos")
    void sugestoesPedagogicas() {
        String sugestoes = service.gerarSugestoesPedagogicas(aluno("5.00", 2, "78.00", "12.00", 1));

        assertThat(sugestoes)
                .contains("reforco")
                .contains("responsavel")
                .doesNotContainIgnoringCase("diagnostico")
                .doesNotContainIgnoringCase("transtorno")
                .doesNotContainIgnoringCase("medicacao");
    }

    @Test
    @DisplayName("Aluno sem fatores recebe sugestao de acompanhamento regular")
    void sugestaoPadrao() {
        String sugestoes = service.gerarSugestoesPedagogicas(aluno("8.00", 0, "95.00", "0.00", 0));

        assertThat(sugestoes).startsWith("Manter acompanhamento pedagogico regular");
    }
}
