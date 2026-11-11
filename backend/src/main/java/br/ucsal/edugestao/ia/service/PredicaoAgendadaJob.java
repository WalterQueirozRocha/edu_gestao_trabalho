package br.ucsal.edugestao.ia.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PredicaoAgendadaJob {

    private final PredicaoRiscoService predicaoRiscoService;

    public PredicaoAgendadaJob(PredicaoRiscoService predicaoRiscoService) {
        this.predicaoRiscoService = predicaoRiscoService;
    }

    // RN-02 e RF-11: Execucao semanal automatica (cobre alerta de frequencia < 80% em ate 7 dias)
    @Scheduled(cron = "0 0 6 * * MON")
    public void executarAnaliseSemanal() {
        predicaoRiscoService.executarPredicaoParaAlunosAtivos();
    }
}
