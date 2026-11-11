package br.ucsal.edugestao.usuario.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {

    @Override
    public Perfil getPerfilTipo() {
        return Perfil.ADMINISTRADOR;
    }

    public void gerenciarPermissoes() {
        this.setAtivo(true);
    }

    public boolean gerenciarPermissoes(Usuario alvo, boolean ativo) {
        if (alvo == null) {
            return false;
        }
        alvo.setAtivo(ativo);
        return alvo.isAtivo();
    }
}
