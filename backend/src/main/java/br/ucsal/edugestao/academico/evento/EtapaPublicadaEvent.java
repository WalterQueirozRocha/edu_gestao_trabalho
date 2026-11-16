package br.ucsal.edugestao.academico.evento;

import br.ucsal.edugestao.academico.entity.Etapa;

// Padrao Observer: publicado quando os boletins de uma etapa sao publicados
public record EtapaPublicadaEvent(Etapa etapa) {
}
