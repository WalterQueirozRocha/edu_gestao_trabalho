package br.ucsal.edugestao.usuario.controller;

import br.ucsal.edugestao.usuario.dto.AtivoRequest;
import br.ucsal.edugestao.usuario.dto.UsuarioRequest;
import br.ucsal.edugestao.usuario.dto.UsuarioResponse;
import br.ucsal.edugestao.usuario.dto.VinculoResponsaveisRequest;
import br.ucsal.edugestao.usuario.entity.Perfil;
import br.ucsal.edugestao.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR', 'PROFESSOR')")
    public List<UsuarioResponse> listar(@RequestParam(required = false) Perfil perfil) {
        return usuarioService.listar(perfil);
    }

    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public UsuarioResponse criar(@Valid @RequestBody UsuarioRequest req) {
        return usuarioService.criar(req);
    }

    @PutMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req) {
        return usuarioService.atualizar(id, req);
    }

    @PatchMapping("/usuarios/{id}/ativo")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public UsuarioResponse alterarAtivo(@PathVariable Long id, @Valid @RequestBody AtivoRequest req) {
        return usuarioService.alterarAtivo(id, req.ativo());
    }

    @PutMapping("/alunos/{id}/responsaveis")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public UsuarioResponse atualizarResponsaveis(
            @PathVariable Long id,
            @Valid @RequestBody VinculoResponsaveisRequest req
    ) {
        return usuarioService.atualizarResponsaveisDoAluno(id, req.responsaveisIds());
    }
}
