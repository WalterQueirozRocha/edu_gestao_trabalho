package br.ucsal.edugestao.usuario.entity;

import br.ucsal.edugestao.academico.entity.Etapa;
import br.ucsal.edugestao.academico.entity.TurmaDisciplina;
import br.ucsal.edugestao.avaliacao.entity.AutorizacaoAlteracao;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("COORDENADOR")
public class Coordenador extends Usuario {

    @Column(length = 100)
    private String cargo;

    @Override
    public Perfil getPerfilTipo() {
        return Perfil.COORDENADOR;
    }

    public void publicarEtapa(Etapa etapa) {
        if (etapa != null) {
            etapa.publicar();
        }
    }

    public void autorizarAlteracao() {
        // Metodo comportamental do diagrama UML (sobrecarga completa abaixo)
    }

    public AutorizacaoAlteracao autorizarAlteracao(
            Etapa etapa,
            TurmaDisciplina turmaDisciplina,
            String motivo,
            LocalDateTime validaAte
    ) {
        AutorizacaoAlteracao aut = new AutorizacaoAlteracao();
        aut.setCoordenador(this);
        aut.setEtapa(etapa);
        aut.setTurmaDisciplina(turmaDisciplina);
        aut.setMotivo(motivo);
        aut.setCriadaEm(LocalDateTime.now());
        aut.setValidaAte(validaAte != null ? validaAte : LocalDateTime.now().plusHours(48));
        aut.setUtilizada(false);
        return aut;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
