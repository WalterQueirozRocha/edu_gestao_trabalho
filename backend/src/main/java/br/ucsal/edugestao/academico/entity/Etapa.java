package br.ucsal.edugestao.academico.entity;

import br.ucsal.edugestao.config.RegraNegocioException;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "etapa")
public class Etapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ano_letivo_id", nullable = false)
    private AnoLetivo anoLetivo;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEtapa status = StatusEtapa.ABERTA;

    // Fluxo da etapa: ABERTA -> FECHADA -> PUBLICADA
    public void fechar() {
        if (status != StatusEtapa.ABERTA) {
            throw new RegraNegocioException("Somente etapas abertas podem ser fechadas.");
        }
        this.status = StatusEtapa.FECHADA;
    }

    public void publicar() {
        if (status != StatusEtapa.FECHADA) {
            throw new RegraNegocioException("A etapa precisa estar fechada para ser publicada.");
        }
        this.status = StatusEtapa.PUBLICADA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnoLetivo getAnoLetivo() {
        return anoLetivo;
    }

    public void setAnoLetivo(AnoLetivo anoLetivo) {
        this.anoLetivo = anoLetivo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public StatusEtapa getStatus() {
        return status;
    }

    public void setStatus(StatusEtapa status) {
        this.status = status;
    }
}
