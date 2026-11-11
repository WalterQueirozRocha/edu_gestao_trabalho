package br.ucsal.edugestao.avaliacao.entity;

import br.ucsal.edugestao.academico.entity.Etapa;
import br.ucsal.edugestao.academico.entity.TurmaDisciplina;
import br.ucsal.edugestao.usuario.entity.Coordenador;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "autorizacao_alteracao")
public class AutorizacaoAlteracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "etapa_id", nullable = false)
    private Etapa etapa;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "turma_disciplina_id", nullable = false)
    private TurmaDisciplina turmaDisciplina;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "coordenador_id", nullable = false)
    private Coordenador coordenador;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm = LocalDateTime.now();

    @Column(name = "valida_ate", nullable = false)
    private LocalDateTime validaAte;

    @Column(nullable = false)
    private boolean utilizada = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Etapa getEtapa() {
        return etapa;
    }

    public void setEtapa(Etapa etapa) {
        this.etapa = etapa;
    }

    public TurmaDisciplina getTurmaDisciplina() {
        return turmaDisciplina;
    }

    public void setTurmaDisciplina(TurmaDisciplina turmaDisciplina) {
        this.turmaDisciplina = turmaDisciplina;
    }

    public Coordenador getCoordenador() {
        return coordenador;
    }

    public void setCoordenador(Coordenador coordenador) {
        this.coordenador = coordenador;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(LocalDateTime criadaEm) {
        this.criadaEm = criadaEm;
    }

    public LocalDateTime getValidaAte() {
        return validaAte;
    }

    public void setValidaAte(LocalDateTime validaAte) {
        this.validaAte = validaAte;
    }

    public boolean isUtilizada() {
        return utilizada;
    }

    public void setUtilizada(boolean utilizada) {
        this.utilizada = utilizada;
    }
}
