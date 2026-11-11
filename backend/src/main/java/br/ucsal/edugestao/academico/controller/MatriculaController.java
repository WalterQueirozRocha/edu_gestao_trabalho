package br.ucsal.edugestao.academico.controller;

import br.ucsal.edugestao.academico.dto.AlterarStatusMatriculaRequest;
import br.ucsal.edugestao.academico.dto.MatriculaDto;
import br.ucsal.edugestao.academico.service.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @GetMapping("/matriculas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR', 'PROFESSOR')")
    public List<MatriculaDto> listar(@RequestParam(required = false) Long turmaId) {
        return matriculaService.listarTodas(turmaId);
    }

    @PostMapping("/matriculas")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public MatriculaDto matricular(@Valid @RequestBody MatriculaDto dto) {
        return matriculaService.matricular(dto);
    }

    @PatchMapping("/matriculas/{id}/status")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public MatriculaDto alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AlterarStatusMatriculaRequest req
    ) {
        return matriculaService.alterarStatus(id, req.status());
    }

    @GetMapping("/alunos/{id}/matriculas")
    public List<MatriculaDto> listarPorAluno(@PathVariable Long id) {
        return matriculaService.listarPorAluno(id);
    }
}
