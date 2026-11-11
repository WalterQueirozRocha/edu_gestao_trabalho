package br.ucsal.edugestao.ia.entity;

import br.ucsal.edugestao.usuario.entity.Aluno;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "modelo_preditivo")
public class ModeloPreditivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String versao;

    @Column(name = "data_treinamento", nullable = false)
    private LocalDate dataTreinamento = LocalDate.now();

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal acuracia;

    @Column(nullable = false)
    private boolean ativo = true;

    public void treinar() {
        this.dataTreinamento = LocalDate.now();
        this.ativo = true;
    }

    public void treinar(BigDecimal novaAcuracia) {
        this.dataTreinamento = LocalDate.now();
        this.acuracia = novaAcuracia;
        this.ativo = true;
    }

    public AlertaRisco preverRisco(Aluno a) {
        return preverRisco(a, BigDecimal.ZERO, NivelRisco.BAIXO, "Sem fatores de risco identificados", "Manter acompanhamento regular.");
    }

    public AlertaRisco preverRisco(
            Aluno a,
            BigDecimal probabilidade,
            NivelRisco nivel,
            String fatores,
            String sugestoes
    ) {
        AlertaRisco alerta = new AlertaRisco();
        alerta.setAluno(a);
        alerta.setModelo(this);
        alerta.setProbabilidade(probabilidade);
        alerta.setNivel(nivel);
        alerta.setFatores(fatores);
        alerta.setSugestoes(sugestoes);
        alerta.setDataGeracao(LocalDateTime.now());
        alerta.setStatus(StatusAlerta.NOVO);
        return alerta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public LocalDate getDataTreinamento() {
        return dataTreinamento;
    }

    public void setDataTreinamento(LocalDate dataTreinamento) {
        this.dataTreinamento = dataTreinamento;
    }

    public BigDecimal getAcuracia() {
        return acuracia;
    }

    public void setAcuracia(BigDecimal acuracia) {
        this.acuracia = acuracia;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
