package br.ucsal.edugestao.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("RESPONSAVEL")
public class Responsavel extends Usuario {

    @Column(unique = true, length = 14)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    @ManyToMany(mappedBy = "responsaveis")
    private Set<Aluno> alunos = new HashSet<>();

    @Override
    public Perfil getPerfilTipo() {
        return Perfil.RESPONSAVEL;
    }

    public Set<Aluno> consultarAlunos() {
        return this.alunos;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Set<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(Set<Aluno> alunos) {
        this.alunos = alunos;
    }
}
