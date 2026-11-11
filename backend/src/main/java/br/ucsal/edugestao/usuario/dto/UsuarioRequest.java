package br.ucsal.edugestao.usuario.dto;

import br.ucsal.edugestao.usuario.entity.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UsuarioRequest(
        @NotBlank(message = "Nome e obrigatorio")
        String nome,

        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail invalido")
        String email,

        String senha,

        @NotNull(message = "Perfil e obrigatorio")
        Perfil perfil,

        String cargo,
        String registroFuncional,
        String formacao,
        String especialidade,
        String matricula,
        LocalDate dataNascimento,
        String cpf,
        String telefone
) {
}
