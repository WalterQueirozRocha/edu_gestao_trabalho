package br.ucsal.edugestao.academico.entity;

import br.ucsal.edugestao.avaliacao.entity.Nota;
import br.ucsal.edugestao.config.RegraNegocioException;
import br.ucsal.edugestao.diario.entity.Frequencia;
import br.ucsal.edugestao.usuario.entity.Aluno;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matricula")
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @Column(name = "data_matricula", nullable = false)
    private LocalDate dataMatricula = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusMatricula status = StatusMatricula.ATIVA;

    @OneToMany(mappedBy = "matricula", fetch = FetchType.LAZY)
    private List<Frequencia> frequencias = new ArrayList<>();

    @OneToMany(mappedBy = "matricula", fetch = FetchType.LAZY)
    private List<Nota> notas = new ArrayList<>();

    public void encerrar() {
        this.status = StatusMatricula.ENCERRADA;
    }

    // Notas e frequencias so podem ser lancadas para matriculas ativas da propria turma
    public void validarAtivaNaTurma(Long turmaId) {
        if (status != StatusMatricula.ATIVA || !turma.getId().equals(turmaId)) {
            throw new RegraNegocioException("Matricula " + id + " nao pertence a esta turma ou nao esta ativa.");
        }
    }

    public BigDecimal calcularFrequencia() {
        return calcularFrequencia(this.frequencias);
    }

    public BigDecimal calcularFrequencia(List<Frequencia> registros) {
        if (registros == null || registros.isEmpty()) {
            return new BigDecimal("100.00");
        }
        int totalHoras = registros.stream()
                .mapToInt(f -> f.getAula() != null ? f.getAula().getHoras() : 1)
                .sum();
        if (totalHoras == 0) {
            return new BigDecimal("100.00");
        }
        int horasPresentes = registros.stream()
                .filter(Frequencia::isPresente)
                .mapToInt(f -> f.getAula() != null ? f.getAula().getHoras() : 1)
                .sum();
        return BigDecimal.valueOf(horasPresentes)
                .multiply(new BigDecimal("100"))
                .divide(BigDecimal.valueOf(totalHoras), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularMedia() {
        return calcularMedia(this.notas);
    }

    public BigDecimal calcularMedia(List<Nota> listaNotas) {
        if (listaNotas == null || listaNotas.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal somaPonderada = BigDecimal.ZERO;
        BigDecimal somaPesos = BigDecimal.ZERO;
        for (Nota n : listaNotas) {
            BigDecimal peso = n.getAvaliacao() != null && n.getAvaliacao().getPeso() != null
                    ? n.getAvaliacao().getPeso()
                    : BigDecimal.ONE;
            somaPonderada = somaPonderada.add(n.getValor().multiply(peso));
            somaPesos = somaPesos.add(peso);
        }
        if (somaPesos.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return somaPonderada.divide(somaPesos, 2, RoundingMode.HALF_UP);
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

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public void setStatus(StatusMatricula status) {
        this.status = status;
    }
}
