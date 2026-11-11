package br.ucsal.edugestao.diario.controller;

import br.ucsal.edugestao.academico.dto.TurmaDisciplinaDto;
import br.ucsal.edugestao.diario.dto.AulaDto;
import br.ucsal.edugestao.diario.dto.FrequenciaItemDto;
import br.ucsal.edugestao.diario.dto.LancarFrequenciasRequest;
import br.ucsal.edugestao.diario.dto.ResumoFrequenciaDto;
import br.ucsal.edugestao.diario.service.FrequenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DiarioController {

    private final FrequenciaService frequenciaService;

    public DiarioController(FrequenciaService frequenciaService) {
        this.frequenciaService = frequenciaService;
    }

    @GetMapping("/professor/turmas")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR', 'ADMINISTRADOR')")
    public List<TurmaDisciplinaDto> listarTurmasDoProfessor() {
        return frequenciaService.listarTurmasDoProfessorLogado();
    }

    @GetMapping("/aulas")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR', 'ADMINISTRADOR')")
    public List<AulaDto> listarAulas(@RequestParam Long turmaDisciplinaId) {
        return frequenciaService.listarAulas(turmaDisciplinaId);
    }

    @PostMapping("/aulas")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR')")
    public AulaDto criarAula(@Valid @RequestBody AulaDto dto) {
        return frequenciaService.criarAula(dto);
    }

    @GetMapping("/aulas/{id}/frequencias")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR', 'ADMINISTRADOR')")
    public List<FrequenciaItemDto> listarFrequencias(@PathVariable Long id) {
        return frequenciaService.listarFrequenciasDaAula(id);
    }

    @PutMapping("/aulas/{id}/frequencias")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR')")
    public List<FrequenciaItemDto> lancarFrequencias(
            @PathVariable Long id,
            @Valid @RequestBody LancarFrequenciasRequest req
    ) {
        return frequenciaService.registrarFrequencias(id, req.frequencias());
    }

    @GetMapping("/matriculas/{id}/frequencia")
    public ResumoFrequenciaDto consultarFrequencia(@PathVariable Long id) {
        return frequenciaService.calcularResumoFrequencia(id);
    }
}
