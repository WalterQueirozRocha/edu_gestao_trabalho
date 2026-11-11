package br.ucsal.edugestao.seguranca.controller;

import br.ucsal.edugestao.seguranca.dto.LoginRequest;
import br.ucsal.edugestao.seguranca.dto.LoginResponse;
import br.ucsal.edugestao.seguranca.service.AcessoService;
import br.ucsal.edugestao.seguranca.service.TokenService;
import br.ucsal.edugestao.usuario.dto.UsuarioResponse;
import br.ucsal.edugestao.usuario.entity.Usuario;
import br.ucsal.edugestao.usuario.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AcessoService acessoService;

    public AuthController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService,
            AcessoService acessoService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.acessoService = acessoService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(req.email().trim())
                .orElseThrow(() -> new BadCredentialsException("Credenciais invalidas."));

        if (!usuario.isAtivo() || !passwordEncoder.matches(req.senha(), usuario.getSenhaHash())) {
            throw new BadCredentialsException("Credenciais invalidas ou usuario inativo.");
        }

        String token = tokenService.gerarToken(usuario);
        return new LoginResponse(token, "Bearer", UsuarioResponse.fromEntity(usuario));
    }

    @GetMapping("/me")
    public UsuarioResponse me() {
        return UsuarioResponse.fromEntity(acessoService.obterUsuarioLogado());
    }
}
