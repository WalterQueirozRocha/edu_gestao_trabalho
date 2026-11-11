package br.ucsal.edugestao.usuario.entity;

import br.ucsal.edugestao.avaliacao.entity.Boletim;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("ALUNO")
public class Aluno extends Usuario {

    @Column(unique = true, length = 30)
    private String matricula;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "aluno_responsavel",
            joinColumns = @JoinColumn(name = "aluno_id"),
            inverseJoinColumns = @JoinColumn(name = "responsavel_id")
    )
    private Set<Responsavel> responsaveis = new HashSet<>();

    @Override
    public Perfil getPerfilTipo() {
        return Perfil.ALUNO;
    }

    public boolean consultarBoletim(Boletim boletim) {
        return boletim != null
                && boletim.getMatricula() != null
                && boletim.getMatricula().getAluno() != null
                && this.getId() != null
                && this.getId().equals(boletim.getMatricula().getAluno().getId());
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Set<Responsavel> getResponsaveis() {
        return responsaveis;
    }

    public void setResponsaveis(Set<Responsavel> responsaveis) {
        this.responsaveis = responsaveis;
    }
}
