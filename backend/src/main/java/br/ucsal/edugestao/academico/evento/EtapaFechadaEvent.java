package br.ucsal.edugestao.academico.evento;

import br.ucsal.edugestao.academico.entity.Etapa;

/**
 * Padrao Observer: publicado quando o coordenador fecha uma etapa.
 * Quem reage (ex.: modulo de IA) se inscreve com @EventListener, sem que o
 * fechamento precise conhecer os interessados.
 */
public record EtapaFechadaEvent(Etapa etapa, boolean ultimaEtapaDoAno) {
}
