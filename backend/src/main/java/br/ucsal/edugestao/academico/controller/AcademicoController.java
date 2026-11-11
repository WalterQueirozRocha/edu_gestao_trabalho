package br.ucsal.edugestao.academico.controller;

import br.ucsal.edugestao.academico.dto.*;
import br.ucsal.edugestao.academico.service.AcademicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AcademicoController {

    private final AcademicoService academicoService;

    public AcademicoController(AcademicoService academicoService) {
        this.academicoService = academicoService;
    }

    @GetMapping("/anos-letivos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR', 'PROFESSOR')")
    public List<AnoLetivoDto> listarAnosLetivos() {
        return academicoService.listarAnosLetivos();
    }

    @PostMapping("/anos-letivos")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public AnoLetivoDto criarAnoLetivo(@Valid @RequestBody AnoLetivoDto dto) {
        return academicoService.criarAnoLetivo(dto);
    }

    @PutMapping("/anos-letivos/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public AnoLetivoDto atualizarAnoLetivo(@PathVariable Long id, @Valid @RequestBody AnoLetivoDto dto) {
        return academicoService.atualizarAnoLetivo(id, dto);
    }

    @GetMapping("/etapas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR', 'PROFESSOR')")
    public List<EtapaDto> listarEtapas(@RequestParam(required = false) Long anoLetivoId) {
        return academicoService.listarEtapas(anoLetivoId);
    }

    @PostMapping("/etapas")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public EtapaDto criarEtapa(@Valid @RequestBody EtapaDto dto) {
        return academicoService.criarEtapa(dto);
    }

    @PutMapping("/etapas/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public EtapaDto atualizarEtapa(@PathVariable Long id, @Valid @RequestBody EtapaDto dto) {
        return academicoService.atualizarEtapa(id, dto);
    }

    @GetMapping("/turmas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR', 'PROFESSOR')")
    public List<TurmaDto> listarTurmas(@RequestParam(required = false) Long anoLetivoId) {
        return academicoService.listarTurmas(anoLetivoId);
    }

    @PostMapping("/turmas")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public TurmaDto criarTurma(@Valid @RequestBody TurmaDto dto) {
        return academicoService.criarTurma(dto);
    }

    @PutMapping("/turmas/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public TurmaDto atualizarTurma(@PathVariable Long id, @Valid @RequestBody TurmaDto dto) {
        return academicoService.atualizarTurma(id, dto);
    }

    @GetMapping("/disciplinas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR', 'PROFESSOR')")
    public List<DisciplinaDto> listarDisciplinas() {
        return academicoService.listarDisciplinas();
    }

    @PostMapping("/disciplinas")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public DisciplinaDto criarDisciplina(@Valid @RequestBody DisciplinaDto dto) {
        return academicoService.criarDisciplina(dto);
    }

    @PutMapping("/disciplinas/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public DisciplinaDto atualizarDisciplina(@PathVariable Long id, @Valid @RequestBody DisciplinaDto dto) {
        return academicoService.atualizarDisciplina(id, dto);
    }

    @GetMapping("/turmas/{id}/disciplinas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR', 'PROFESSOR')")
    public List<TurmaDisciplinaDto> listarDisciplinasDaTurma(@PathVariable Long id) {
        return academicoService.listarDisciplinasDaTurma(id);
    }

    @PostMapping("/turmas/{id}/disciplinas")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public TurmaDisciplinaDto vincularDisciplina(
            @PathVariable Long id,
            @Valid @RequestBody TurmaDisciplinaDto dto
    ) {
        return academicoService.vincularDisciplinaATurma(id, dto);
    }
}
