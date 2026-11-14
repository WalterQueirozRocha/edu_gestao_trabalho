package br.ucsal.edugestao.ocorrencia.entity;

import br.ucsal.edugestao.usuario.entity.Aluno;
import br.ucsal.edugestao.usuario.entity.Usuario;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ocorrencia")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "registrada_por_id", nullable = false)
    private Usuario registradaPor;

    @Column(nullable = false)
    private LocalDate data = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoOcorrencia tipo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ocorrencia_aluno",
            joinColumns = @JoinColumn(name = "ocorrencia_id"),
            inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    private Set<Aluno> alunos = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAtendimento status = StatusAtendimento.RECEBIDA;

    @Column(columnDefinition = "TEXT")
    private String medidas;

    @Column(name = "atualizada_em")
    private LocalDateTime atualizadaEm;

    public void registrar() {
        if (this.data == null) {
            this.data = LocalDate.now();
        }
    }

    // UC12: tratamento da ocorrencia pela coordenacao
    public void tratar(StatusAtendimento novoStatus, String medidasAdotadas) {
        this.status = novoStatus;
        if (medidasAdotadas != null && !medidasAdotadas.isBlank()) {
            this.medidas = medidasAdotadas.trim();
        }
        this.atualizadaEm = LocalDateTime.now();
    }

    public StatusAtendimento getStatus() {
        return status;
    }

    public String getMedidas() {
        return medidas;
    }

    public LocalDateTime getAtualizadaEm() {
        return atualizadaEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getRegistradaPor() {
        return registradaPor;
    }

    public void setRegistradaPor(Usuario registradaPor) {
        this.registradaPor = registradaPor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public TipoOcorrencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoOcorrencia tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Set<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(Set<Aluno> alunos) {
        this.alunos = alunos;
    }
}
