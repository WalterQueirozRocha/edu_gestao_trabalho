package br.ucsal.edugestao.ia.controller;

import br.ucsal.edugestao.ia.dto.AlertaRiscoDto;
import br.ucsal.edugestao.ia.dto.ConverterAlertaRequest;
import br.ucsal.edugestao.ia.dto.ModeloPreditivoDto;
import br.ucsal.edugestao.ia.service.PredicaoRiscoService;
import br.ucsal.edugestao.pedagogico.dto.PlanoPedagogicoDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
public class IaController {

    private final PredicaoRiscoService predicaoRiscoService;

    public IaController(PredicaoRiscoService predicaoRiscoService) {
        this.predicaoRiscoService = predicaoRiscoService;
    }

    @GetMapping("/alertas")
    public List<AlertaRiscoDto> listarAlertas() {
        return predicaoRiscoService.listarAlertas();
    }

    @PostMapping("/alertas/{id}/descartar")
    public AlertaRiscoDto descartarAlerta(@PathVariable Long id) {
        return predicaoRiscoService.descartarAlerta(id);
    }

    @PostMapping("/alertas/{id}/converter")
    @PreAuthorize("hasRole('COORDENADOR')")
    public PlanoPedagogicoDto converterEmPlano(
            @PathVariable Long id,
            @RequestBody(required = false) ConverterAlertaRequest req
    ) {
        return predicaoRiscoService.converterEmPlano(id, req);
    }

    @PostMapping("/ia/executar")
    public List<AlertaRiscoDto> executarPredicao() {
        return predicaoRiscoService.executarPredicaoParaAlunosAtivos();
    }

    @PostMapping("/ia/treinar")
    public ModeloPreditivoDto treinarModelo() {
        return predicaoRiscoService.treinarModelo();
    }
}
