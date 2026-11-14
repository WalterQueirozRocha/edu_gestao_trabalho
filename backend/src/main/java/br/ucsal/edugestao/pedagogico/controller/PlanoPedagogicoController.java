package br.ucsal.edugestao.pedagogico.controller;

import br.ucsal.edugestao.pedagogico.dto.AutorizarProfissionaisRequest;
import br.ucsal.edugestao.pedagogico.dto.PlanoPedagogicoDto;
import br.ucsal.edugestao.pedagogico.service.PlanoPedagogicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlanoPedagogicoController {

    private final PlanoPedagogicoService planoService;

    public PlanoPedagogicoController(PlanoPedagogicoService planoService) {
        this.planoService = planoService;
    }

    @GetMapping("/planos")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public List<PlanoPedagogicoDto> listarPlanos() {
        return planoService.listarTodos();
    }

    @PostMapping("/planos")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('COORDENADOR')")
    public PlanoPedagogicoDto criarPlano(@Valid @RequestBody PlanoPedagogicoDto dto) {
        return planoService.criar(dto);
    }

    @PutMapping("/planos/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public PlanoPedagogicoDto atualizarPlano(
            @PathVariable Long id,
            @Valid @RequestBody PlanoPedagogicoDto dto
    ) {
        return planoService.atualizar(id, dto);
    }

    @PutMapping("/planos/{id}/profissionais")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public PlanoPedagogicoDto autorizarProfissionais(
            @PathVariable Long id,
            @Valid @RequestBody AutorizarProfissionaisRequest req
    ) {
        return planoService.autorizarProfissionais(id, req.profissionaisIds());
    }

    @GetMapping("/apoio/planos")
    @PreAuthorize("hasAnyRole('PROFISSIONAL_APOIO', 'COORDENADOR', 'ADMINISTRADOR')")
    public List<PlanoPedagogicoDto> listarPlanosDoApoio() {
        return planoService.listarPlanosAutorizadosParaProfissionalLogado();
    }
}
