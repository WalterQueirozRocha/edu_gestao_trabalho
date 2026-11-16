package br.ucsal.edugestao.academico.entity;

import br.ucsal.edugestao.config.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Padrao State: transicoes validas da etapa (ABERTA -> FECHADA -> PUBLICADA)
class StatusEtapaTest {

    @Test
    @DisplayName("Fluxo normal: abrir, fechar e publicar")
    void fluxoNormal() {
        Etapa etapa = new Etapa();

        etapa.fechar();
        assertThat(etapa.getStatus()).isEqualTo(StatusEtapa.FECHADA);

        etapa.publicar();
        assertThat(etapa.getStatus()).isEqualTo(StatusEtapa.PUBLICADA);
    }

    @Test
    @DisplayName("Etapa aberta nao pode ser publicada direto")
    void naoPublicaEtapaAberta() {
        assertThatThrownBy(StatusEtapa.ABERTA::publicar)
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("precisa estar fechada");
    }

    @Test
    @DisplayName("Etapa fechada ou publicada nao pode ser fechada de novo")
    void naoFechaDuasVezes() {
        assertThatThrownBy(StatusEtapa.FECHADA::fechar).isInstanceOf(RegraNegocioException.class);
        assertThatThrownBy(StatusEtapa.PUBLICADA::fechar).isInstanceOf(RegraNegocioException.class);
        assertThatThrownBy(StatusEtapa.PUBLICADA::publicar).isInstanceOf(RegraNegocioException.class);
    }

    @Test
    @DisplayName("RN-04: apenas etapa aberta permite lancamento sem autorizacao")
    void lancamentoLivre() {
        assertThat(StatusEtapa.ABERTA.permiteLancamentoLivre()).isTrue();
        assertThat(StatusEtapa.FECHADA.permiteLancamentoLivre()).isFalse();
        assertThat(StatusEtapa.PUBLICADA.permiteLancamentoLivre()).isFalse();
    }
}
