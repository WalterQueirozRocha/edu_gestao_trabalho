package br.ucsal.edugestao.ia.service;

import br.ucsal.edugestao.academico.evento.EtapaFechadaEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

// Observer do fechamento de etapa: RF-11 roda a predicao a cada fechamento
@Component
public class PredicaoAoFecharEtapaListener {

    private final PredicaoRiscoService predicaoRiscoService;

    public PredicaoAoFecharEtapaListener(PredicaoRiscoService predicaoRiscoService) {
        this.predicaoRiscoService = predicaoRiscoService;
    }

    // Sincrono e na mesma transacao do fechamento, como antes da refatoracao
    @EventListener
    public void aoFecharEtapa(EtapaFechadaEvent evento) {
        if (evento.ultimaEtapaDoAno()) {
            // IA §10: retreino ao final de cada ano letivo
            predicaoRiscoService.treinarModelo();
        }
        predicaoRiscoService.executarPredicaoParaAlunosAtivos();
    }
}
