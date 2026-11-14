package br.ucsal.edugestao.usuario.dto;

import br.ucsal.edugestao.usuario.entity.*;

import java.time.LocalDate;
import java.util.List;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        boolean ativo,
        String cargo,
        String registroFuncional,
        String formacao,
        String especialidade,
        String matricula,
        LocalDate dataNascimento,
        String cpf,
        String telefone,
        List<Long> responsaveisIds
) {
    // LGPD: apenas identificacao do estudante, sem dados pessoais
    public static UsuarioResponse resumo(Usuario u) {
        String matricula = u instanceof Aluno a ? a.getMatricula() : null;
        return new UsuarioResponse(u.getId(), u.getNome(), null, u.getPerfil(), u.isAtivo(),
                null, null, null, null, matricula, null, null, null, List.of());
    }

    public static UsuarioResponse fromEntity(Usuario u) {
        String cargo = null;
        String registroFuncional = null;
        String formacao = null;
        String especialidade = null;
        String matricula = null;
        LocalDate dataNascimento = null;
        String cpf = null;
        String telefone = null;
        List<Long> responsaveisIds = List.of();

        if (u instanceof Coordenador c) {
            cargo = c.getCargo();
        } else if (u instanceof Professor p) {
            registroFuncional = p.getRegistroFuncional();
            formacao = p.getFormacao();
        } else if (u instanceof ProfissionalApoio pa) {
            especialidade = pa.getEspecialidade();
        } else if (u instanceof Aluno a) {
            matricula = a.getMatricula();
            dataNascimento = a.getDataNascimento();
            if (a.getResponsaveis() != null) {
                responsaveisIds = a.getResponsaveis().stream().map(Responsavel::getId).sorted().toList();
            }
        } else if (u instanceof Responsavel r) {
            cpf = r.getCpf();
            telefone = r.getTelefone();
        }

        return new UsuarioResponse(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getPerfil(),
                u.isAtivo(),
                cargo,
                registroFuncional,
                formacao,
                especialidade,
                matricula,
                dataNascimento,
                cpf,
                telefone,
                responsaveisIds
        );
    }
}
