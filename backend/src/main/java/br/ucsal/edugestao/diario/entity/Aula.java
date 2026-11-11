package br.ucsal.edugestao.diario.entity;

import br.ucsal.edugestao.academico.entity.Matricula;
import br.ucsal.edugestao.academico.entity.TurmaDisciplina;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "aula")
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "turma_disciplina_id", nullable = false)
    private TurmaDisciplina turmaDisciplina;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(nullable = false)
    private int horas;

    public Frequencia registrarPresenca(Matricula m, boolean presente) {
        Frequencia freq = new Frequencia();
        freq.setAula(this);
        freq.setMatricula(m);
        freq.setPresente(presente);
        return freq;
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }
}
