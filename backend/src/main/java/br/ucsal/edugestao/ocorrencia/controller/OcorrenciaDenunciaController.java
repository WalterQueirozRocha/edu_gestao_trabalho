package br.ucsal.edugestao.ocorrencia.controller;

import br.ucsal.edugestao.ocorrencia.dto.DenunciaDto;
import br.ucsal.edugestao.ocorrencia.dto.OcorrenciaDto;
import br.ucsal.edugestao.ocorrencia.dto.TratarDenunciaRequest;
import br.ucsal.edugestao.ocorrencia.service.OcorrenciaDenunciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OcorrenciaDenunciaController {

    private final OcorrenciaDenunciaService service;

    public OcorrenciaDenunciaController(OcorrenciaDenunciaService service) {
        this.service = service;
    }

    @PostMapping("/ocorrencias")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR', 'ADMINISTRADOR')")
    public OcorrenciaDto registrarOcorrencia(@Valid @RequestBody OcorrenciaDto dto) {
        return service.registrarOcorrencia(dto);
    }

    @GetMapping("/ocorrencias")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR', 'ADMINISTRADOR')")
    public List<OcorrenciaDto> listarOcorrencias() {
        return service.listarOcorrencias();
    }

    @PatchMapping("/ocorrencias/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public OcorrenciaDto tratarOcorrencia(
            @PathVariable Long id,
            @Valid @RequestBody TratarDenunciaRequest req
    ) {
        return service.tratarOcorrencia(id, req);
    }

    @PostMapping("/denuncias")
    @ResponseStatus(HttpStatus.CREATED)
    public DenunciaDto registrarDenuncia(@Valid @RequestBody DenunciaDto dto) {
        return service.registrarDenuncia(dto);
    }

    @GetMapping("/denuncias/protocolo/{protocolo}")
    public DenunciaDto consultarPorProtocolo(@PathVariable String protocolo) {
        return service.consultarPorProtocolo(protocolo);
    }

    @GetMapping("/denuncias")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public List<DenunciaDto> listarDenuncias() {
        return service.listarDenunciasRestrito();
    }

    @PatchMapping("/denuncias/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public DenunciaDto tratarDenuncia(
            @PathVariable Long id,
            @Valid @RequestBody TratarDenunciaRequest req
    ) {
        return service.tratarDenuncia(id, req);
    }
}
