package br.ucsal.edugestao.avaliacao.entity;

import br.ucsal.edugestao.academico.entity.Etapa;
import br.ucsal.edugestao.academico.entity.Matricula;
import br.ucsal.edugestao.academico.entity.TurmaDisciplina;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "turma_disciplina_id", nullable = false)
    private TurmaDisciplina turmaDisciplina;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "etapa_id", nullable = false)
    private Etapa etapa;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal peso;

    public Nota lancarNota(Matricula m, BigDecimal valor) {
        Nota nota = new Nota();
        nota.setAvaliacao(this);
        nota.setMatricula(m);
        nota.setValor(valor);
        nota.setDataLancamento(LocalDateTime.now());
        return nota;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TurmaDisciplina getTurmaDisciplina() {
        return turmaDisciplina;
    }

    public void setTurmaDisciplina(TurmaDisciplina turmaDisciplina) {
        this.turmaDisciplina = turmaDisciplina;
    }

    public Etapa getEtapa() {
        return etapa;
    }

    public void setEtapa(Etapa etapa) {
        this.etapa = etapa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }
}
