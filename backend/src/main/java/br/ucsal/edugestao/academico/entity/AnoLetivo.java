package br.ucsal.edugestao.academico.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ano_letivo")
public class AnoLetivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private int ano;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "media_minima", nullable = false, precision = 4, scale = 2)
    private BigDecimal mediaMinima = new BigDecimal("6.00");

    @Column(name = "frequencia_minima", nullable = false, precision = 5, scale = 2)
    private BigDecimal frequenciaMinima = new BigDecimal("75.00");

    @OneToMany(mappedBy = "anoLetivo", fetch = FetchType.LAZY)
    private List<Etapa> etapas = new ArrayList<>();

    public Etapa etapaAtual() {
        LocalDate hoje = LocalDate.now();
        return etapas.stream()
                .filter(e -> !hoje.isBefore(e.getDataInicio()) && !hoje.isAfter(e.getDataFim()))
                .findFirst()
                .orElseGet(() -> etapas.stream()
                        .filter(e -> e.getStatus() == StatusEtapa.ABERTA)
                        .findFirst()
                        .orElse(etapas.isEmpty() ? null : etapas.get(0)));
    }

    public List<Etapa> getEtapas() {
        return etapas;
    }

    public void setEtapas(List<Etapa> etapas) {
        this.etapas = etapas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
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

    public BigDecimal getMediaMinima() {
        return mediaMinima;
    }

    public void setMediaMinima(BigDecimal mediaMinima) {
        this.mediaMinima = mediaMinima;
    }

    public BigDecimal getFrequenciaMinima() {
        return frequenciaMinima;
    }

    public void setFrequenciaMinima(BigDecimal frequenciaMinima) {
        this.frequenciaMinima = frequenciaMinima;
    }
}
