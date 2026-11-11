package br.ucsal.edugestao.avaliacao.entity;

import br.ucsal.edugestao.academico.entity.Etapa;
import br.ucsal.edugestao.academico.entity.Matricula;
import jakarta.persistence.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Entity
@Table(name = "boletim")
public class Boletim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "etapa_id", nullable = false)
    private Etapa etapa;

    @Column(name = "data_publicacao")
    private LocalDate dataPublicacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Situacao situacao = Situacao.EM_ANDAMENTO;

    public byte[] gerarPdf() {
        String alunoNome = matricula != null && matricula.getAluno() != null ? matricula.getAluno().getNome() : "N/A";
        String turmaNome = matricula != null && matricula.getTurma() != null ? matricula.getTurma().getNome() : "N/A";
        String etapaNome = etapa != null ? etapa.getNome() : "N/A";
        String resumo = String.format(
                "BOLETIM ESCOLAR - EDUGESTAO%nEstudante: %s%nTurma: %s%nEtapa: %s%nSituacao: %s%nData Publicacao: %s%n",
                alunoNome,
                turmaNome,
                etapaNome,
                situacao,
                dataPublicacao != null ? dataPublicacao : "Pendente"
        );
        return resumo.getBytes(StandardCharsets.UTF_8);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public Etapa getEtapa() {
        return etapa;
    }

    public void setEtapa(Etapa etapa) {
        this.etapa = etapa;
    }

    public LocalDate getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDate dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }
}
