package br.ucsal.edugestao.comunicacao.controller;

import br.ucsal.edugestao.comunicacao.dto.ComunicadoDto;
import br.ucsal.edugestao.comunicacao.service.ComunicadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunicados")
public class ComunicadoController {

    private final ComunicadoService comunicadoService;

    public ComunicadoController(ComunicadoService comunicadoService) {
        this.comunicadoService = comunicadoService;
    }

    @GetMapping
    public List<ComunicadoDto> listar() {
        return comunicadoService.listarParaUsuarioLogado();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ComunicadoDto publicar(@Valid @RequestBody ComunicadoDto dto) {
        return comunicadoService.publicar(dto);
    }
}
