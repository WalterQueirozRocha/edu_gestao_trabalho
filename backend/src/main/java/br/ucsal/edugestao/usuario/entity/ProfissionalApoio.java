package br.ucsal.edugestao.usuario.entity;

import br.ucsal.edugestao.pedagogico.entity.PlanoPedagogico;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PROFISSIONAL_APOIO")
public class ProfissionalApoio extends Usuario {

    @Column(length = 100)
    private String especialidade;

    @Override
    public Perfil getPerfilTipo() {
        return Perfil.PROFISSIONAL_APOIO;
    }

    public boolean consultarPlano(PlanoPedagogico plano) {
        if (plano == null || plano.getProfissionaisAutorizados() == null) {
            return false;
        }
        return plano.getProfissionaisAutorizados().stream()
                .anyMatch(p -> p.getId() != null && p.getId().equals(this.getId()));
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
