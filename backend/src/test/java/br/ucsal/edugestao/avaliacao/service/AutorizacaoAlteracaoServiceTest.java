package br.ucsal.edugestao.avaliacao.service;

import br.ucsal.edugestao.academico.entity.Etapa;
import br.ucsal.edugestao.academico.entity.StatusEtapa;
import br.ucsal.edugestao.avaliacao.entity.AutorizacaoAlteracao;
import br.ucsal.edugestao.avaliacao.repository.AutorizacaoAlteracaoRepository;
import br.ucsal.edugestao.config.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

// RN-04: notas e faltas nao podem ser alteradas apos o fechamento da etapa sem autorizacao da coordenacao
@ExtendWith(MockitoExtension.class)
class AutorizacaoAlteracaoServiceTest {

    private static final Long ETAPA_ID = 1L;
    private static final Long TURMA_DISCIPLINA_ID = 20L;

    @Mock
    private AutorizacaoAlteracaoRepository autorizacaoRepository;

    @InjectMocks
    private AutorizacaoAlteracaoService service;

    private Etapa etapa;

    @BeforeEach
    void setUp() {
        etapa = new Etapa();
        etapa.setId(ETAPA_ID);
        etapa.setNome("1a Unidade");
    }

    private void semAutorizacaoAtiva() {
        when(autorizacaoRepository.findFirstByEtapaIdAndTurmaDisciplinaIdAndUtilizadaFalseAndValidaAteAfterOrderByCriadaEmDesc(
                eq(ETAPA_ID), eq(TURMA_DISCIPLINA_ID), any(LocalDateTime.class)
        )).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("Etapa aberta permite alteracao sem autorizacao")
    void etapaAbertaLiberada() {
        etapa.setStatus(StatusEtapa.ABERTA);

        service.validarEutilizarAutorizacaoSeEtapaFechada(etapa, TURMA_DISCIPLINA_ID);

        verifyNoInteractions(autorizacaoRepository);
    }

    @ParameterizedTest(name = "Etapa {0} sem autorizacao bloqueia alteracao")
    @EnumSource(value = StatusEtapa.class, names = {"FECHADA", "PUBLICADA"})
    void etapaFechadaSemAutorizacaoBloqueia(StatusEtapa status) {
        etapa.setStatus(status);
        semAutorizacaoAtiva();

        assertThatThrownBy(() -> service.validarEutilizarAutorizacaoSeEtapaFechada(etapa, TURMA_DISCIPLINA_ID))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("RN-04");
        verify(autorizacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Etapa fechada com autorizacao ativa libera e consome a autorizacao")
    void autorizacaoAtivaEhConsumida() {
        etapa.setStatus(StatusEtapa.FECHADA);
        AutorizacaoAlteracao autorizacao = new AutorizacaoAlteracao();
        autorizacao.setUtilizada(false);
        when(autorizacaoRepository.findFirstByEtapaIdAndTurmaDisciplinaIdAndUtilizadaFalseAndValidaAteAfterOrderByCriadaEmDesc(
                eq(ETAPA_ID), eq(TURMA_DISCIPLINA_ID), any(LocalDateTime.class)
        )).thenReturn(Optional.of(autorizacao));

        service.validarEutilizarAutorizacaoSeEtapaFechada(etapa, TURMA_DISCIPLINA_ID);

        assertThat(autorizacao.isUtilizada()).isTrue();
        verify(autorizacaoRepository).save(autorizacao);
    }

    @Test
    @DisplayName("Fluxo da etapa: ABERTA -> FECHADA -> PUBLICADA")
    void fluxoDaEtapa() {
        Etapa nova = new Etapa();
        assertThat(nova.getStatus()).isEqualTo(StatusEtapa.ABERTA);

        assertThatThrownBy(nova::publicar).isInstanceOf(RegraNegocioException.class);

        nova.fechar();
        assertThat(nova.getStatus()).isEqualTo(StatusEtapa.FECHADA);
        assertThatThrownBy(nova::fechar).isInstanceOf(RegraNegocioException.class);

        nova.publicar();
        assertThat(nova.getStatus()).isEqualTo(StatusEtapa.PUBLICADA);
    }
}
