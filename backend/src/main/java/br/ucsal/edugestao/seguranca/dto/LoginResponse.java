package br.ucsal.edugestao.seguranca.dto;

import br.ucsal.edugestao.usuario.dto.UsuarioResponse;

public record LoginResponse(
        String token,
        String tipo,
        UsuarioResponse usuario
) {
}
