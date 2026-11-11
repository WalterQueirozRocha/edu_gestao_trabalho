package br.ucsal.edugestao.comunicacao.dto;

import br.ucsal.edugestao.academico.entity.Turma;
import br.ucsal.edugestao.comunicacao.entity.Comunicado;
import br.ucsal.edugestao.usuario.entity.Perfil;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public record ComunicadoDto(
        Long id,
        Long autorId,
        String autorNome,
        @NotBlank(message = "Titulo e obrigatorio")
        String titulo,
        @NotBlank(message = "Mensagem e obrigatoria")
        String mensagem,
        LocalDateTime dataPublicacao,
        List<Long> turmasIds,
        List<String> turmasNomes,
        List<Perfil> publicoAlvo
) {
    public static ComunicadoDto fromEntity(Comunicado c) {
        return new ComunicadoDto(
                c.getId(),
                c.getAutor().getId(),
                c.getAutor().getNome(),
                c.getTitulo(),
                c.getMensagem(),
                c.getDataPublicacao(),
                c.getTurmas().stream().map(Turma::getId).sorted().toList(),
                c.getTurmas().stream().map(Turma::getNome).sorted().toList(),
                c.getPublicoAlvo().stream().sorted().toList()
        );
    }
}
