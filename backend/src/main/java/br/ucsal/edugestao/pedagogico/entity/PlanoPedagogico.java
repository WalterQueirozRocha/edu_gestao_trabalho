package br.ucsal.edugestao.pedagogico.entity;

import br.ucsal.edugestao.ia.entity.AlertaRisco;
import br.ucsal.edugestao.usuario.entity.Aluno;
import br.ucsal.edugestao.usuario.entity.Coordenador;
import br.ucsal.edugestao.usuario.entity.ProfissionalApoio;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plano_pedagogico")
public class PlanoPedagogico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "coordenador_id", nullable = false)
    private Coordenador coordenador;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "alerta_origem_id")
    private AlertaRisco alertaOrigem;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String metas;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String adaptacoes;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String estrategias;

    @Column(name = "data_revisao", nullable = false)
    private LocalDate dataRevisao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "plano_profissional_apoio",
            joinColumns = @JoinColumn(name = "plano_id"),
            inverseJoinColumns = @JoinColumn(name = "profissional_id")
    )
    private Set<ProfissionalApoio> profissionaisAutorizados = new HashSet<>();

    public void revisar() {
        this.dataRevisao = LocalDate.now().plusDays(30);
    }

    public void revisar(LocalDate novaDataRevisao) {
        this.dataRevisao = novaDataRevisao != null ? novaDataRevisao : LocalDate.now().plusDays(30);
    }

    public void autorizar(ProfissionalApoio p) {
        if (p != null) {
            this.profissionaisAutorizados.add(p);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Coordenador getCoordenador() {
        return coordenador;
    }

    public void setCoordenador(Coordenador coordenador) {
        this.coordenador = coordenador;
    }

    public AlertaRisco getAlertaOrigem() {
        return alertaOrigem;
    }

    public void setAlertaOrigem(AlertaRisco alertaOrigem) {
        this.alertaOrigem = alertaOrigem;
    }

    public Long getAlertaOrigemId() {
        return alertaOrigem != null ? alertaOrigem.getId() : null;
    }

    public String getMetas() {
        return metas;
    }

    public void setMetas(String metas) {
        this.metas = metas;
    }

    public String getAdaptacoes() {
        return adaptacoes;
    }

    public void setAdaptacoes(String adaptacoes) {
        this.adaptacoes = adaptacoes;
    }

    public String getEstrategias() {
        return estrategias;
    }

    public void setEstrategias(String estrategias) {
        this.estrategias = estrategias;
    }

    public LocalDate getDataRevisao() {
        return dataRevisao;
    }

    public void setDataRevisao(LocalDate dataRevisao) {
        this.dataRevisao = dataRevisao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Set<ProfissionalApoio> getProfissionaisAutorizados() {
        return profissionaisAutorizados;
    }

    public void setProfissionaisAutorizados(Set<ProfissionalApoio> profissionaisAutorizados) {
        this.profissionaisAutorizados = profissionaisAutorizados;
    }
}
