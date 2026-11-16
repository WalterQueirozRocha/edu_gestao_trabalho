package br.ucsal.edugestao.academico.entity;

import br.ucsal.edugestao.config.RegraNegocioException;

/**
 * Padrao State: cada status da etapa decide quais transicoes aceita.
 * Fluxo: ABERTA -> FECHADA -> PUBLICADA. A entidade {@link Etapa} apenas delega.
 */
public enum StatusEtapa {
    ABERTA {
        @Override
        public StatusEtapa fechar() {
            return FECHADA;
        }
    },
    FECHADA {
        @Override
        public StatusEtapa publicar() {
            return PUBLICADA;
        }
    },
    PUBLICADA;

    public StatusEtapa fechar() {
        throw new RegraNegocioException("Somente etapas abertas podem ser fechadas.");
    }

    public StatusEtapa publicar() {
        throw new RegraNegocioException("A etapa precisa estar fechada para ser publicada.");
    }

    // RN-04: notas e frequencia so mudam livremente enquanto a etapa esta aberta
    public boolean permiteLancamentoLivre() {
        return this == ABERTA;
    }
}
