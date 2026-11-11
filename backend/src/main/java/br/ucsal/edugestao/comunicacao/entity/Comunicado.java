package br.ucsal.edugestao.comunicacao.entity;

import br.ucsal.edugestao.academico.entity.Turma;
import br.ucsal.edugestao.usuario.entity.Perfil;
import br.ucsal.edugestao.usuario.entity.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comunicado")
public class Comunicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "data_publicacao", nullable = false)
    private LocalDateTime dataPublicacao = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "comunicado_turma",
            joinColumns = @JoinColumn(name = "comunicado_id"),
            inverseJoinColumns = @JoinColumn(name = "turma_id")
    )
    private Set<Turma> turmas = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comunicado_perfil", joinColumns = @JoinColumn(name = "comunicado_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", length = 30)
    private Set<Perfil> publicoAlvo = new HashSet<>();

    public void publicar() {
        this.dataPublicacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Set<Turma> getTurmas() {
        return turmas;
    }

    public void setTurmas(Set<Turma> turmas) {
        this.turmas = turmas;
    }

    public Set<Perfil> getPublicoAlvo() {
        return publicoAlvo;
    }

    public void setPublicoAlvo(Set<Perfil> publicoAlvo) {
        this.publicoAlvo = publicoAlvo;
    }
}
