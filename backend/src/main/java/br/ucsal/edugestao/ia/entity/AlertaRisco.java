package br.ucsal.edugestao.ia.entity;

import br.ucsal.edugestao.pedagogico.entity.PlanoPedagogico;
import br.ucsal.edugestao.usuario.entity.Aluno;
import br.ucsal.edugestao.usuario.entity.Coordenador;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta_risco")
public class AlertaRisco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "modelo_id")
    private ModeloPreditivo modelo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelRisco nivel;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal probabilidade;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String fatores;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String sugestoes;

    @Column(name = "data_geracao", nullable = false)
    private LocalDateTime dataGeracao = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAlerta status = StatusAlerta.NOVO;

    public void descartar() {
        this.status = StatusAlerta.DESCARTADO;
    }

    public void marcarConvertido() {
        this.status = StatusAlerta.CONVERTIDO;
    }

    public PlanoPedagogico converterEmPlano() {
        return converterEmPlano(null, "Acompanhamento de " + this.fatores, "Apoio individualizado", this.sugestoes, LocalDate.now().plusDays(30));
    }

    public PlanoPedagogico converterEmPlano(
            Coordenador coordenador,
            String metas,
            String adaptacoes,
            String estrategias,
            LocalDate dataRevisao
    ) {
        this.marcarConvertido();
        PlanoPedagogico plano = new PlanoPedagogico();
        plano.setAluno(this.aluno);
        plano.setCoordenador(coordenador);
        plano.setAlertaOrigem(this);
        plano.setMetas(metas);
        plano.setAdaptacoes(adaptacoes);
        plano.setEstrategias(estrategias);
        plano.setDataRevisao(dataRevisao != null ? dataRevisao : LocalDate.now().plusDays(30));
        plano.setCriadoEm(LocalDateTime.now());
        return plano;
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

    public ModeloPreditivo getModelo() {
        return modelo;
    }

    public void setModelo(ModeloPreditivo modelo) {
        this.modelo = modelo;
    }

    public NivelRisco getNivel() {
        return nivel;
    }

    public void setNivel(NivelRisco nivel) {
        this.nivel = nivel;
    }

    public BigDecimal getProbabilidade() {
        return probabilidade;
    }

    public void setProbabilidade(BigDecimal probabilidade) {
        this.probabilidade = probabilidade;
    }

    public String getFatores() {
        return fatores;
    }

    public void setFatores(String fatores) {
        this.fatores = fatores;
    }

    public String getSugestoes() {
        return sugestoes;
    }

    public void setSugestoes(String sugestoes) {
        this.sugestoes = sugestoes;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public StatusAlerta getStatus() {
        return status;
    }

    public void setStatus(StatusAlerta status) {
        this.status = status;
    }
}
