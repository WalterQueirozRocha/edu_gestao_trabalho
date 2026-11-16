package br.ucsal.edugestao.ia.service;

import br.ucsal.edugestao.academico.entity.Etapa;
import br.ucsal.edugestao.academico.evento.EtapaFechadaEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

// Padrao Observer: a IA reage ao evento de fechamento de etapa (RF-11)
@ExtendWith(MockitoExtension.class)
class PredicaoAoFecharEtapaListenerTest {

    @Mock
    private PredicaoRiscoService predicaoRiscoService;

    @InjectMocks
    private PredicaoAoFecharEtapaListener listener;

    @Test
    @DisplayName("Fechamento de etapa intermediaria apenas executa a predicao")
    void etapaIntermediaria() {
        listener.aoFecharEtapa(new EtapaFechadaEvent(new Etapa(), false));

        verify(predicaoRiscoService, never()).treinarModelo();
        verify(predicaoRiscoService).executarPredicaoParaAlunosAtivos();
    }

    @Test
    @DisplayName("Fechamento da ultima etapa do ano retreina o modelo antes de prever")
    void ultimaEtapaRetreina() {
        listener.aoFecharEtapa(new EtapaFechadaEvent(new Etapa(), true));

        InOrder ordem = inOrder(predicaoRiscoService);
        ordem.verify(predicaoRiscoService).treinarModelo();
        ordem.verify(predicaoRiscoService).executarPredicaoParaAlunosAtivos();
    }
}
