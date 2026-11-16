package br.ucsal.edugestao.comunicacao.service;

import br.ucsal.edugestao.academico.entity.Etapa;
import br.ucsal.edugestao.academico.evento.EtapaPublicadaEvent;
import br.ucsal.edugestao.seguranca.service.AcessoService;
import br.ucsal.edugestao.usuario.entity.Perfil;
import br.ucsal.edugestao.usuario.entity.Usuario;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

// Observer da publicacao de etapa: avisa alunos e responsaveis por comunicado automatico
@Component
public class ComunicadoBoletimPublicadoListener {

    private final ComunicadoService comunicadoService;
    private final AcessoService acessoService;

    public ComunicadoBoletimPublicadoListener(ComunicadoService comunicadoService, AcessoService acessoService) {
        this.comunicadoService = comunicadoService;
        this.acessoService = acessoService;
    }

    @EventListener
    public void aoPublicarEtapa(EtapaPublicadaEvent evento) {
        Usuario autor;
        try {
            autor = acessoService.obterUsuarioLogado();
        } catch (RuntimeException semUsuarioLogado) {
            // Publicacao fora de uma requisicao autenticada: nao ha autor para o comunicado
            return;
        }
        Etapa etapa = evento.etapa();
        comunicadoService.publicarAutomatico(
                autor,
                "Boletins Publicados - " + etapa.getNome() + " (" + etapa.getAnoLetivo().getAno() + ")",
                "Os boletins e resultados consolidados da etapa " + etapa.getNome()
                        + " foram publicados pela coordenacao pedagogica e ja estao disponiveis para consulta.",
                Set.of(Perfil.ALUNO, Perfil.RESPONSAVEL)
        );
    }
}
