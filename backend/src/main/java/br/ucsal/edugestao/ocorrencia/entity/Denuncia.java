package br.ucsal.edugestao.ocorrencia.entity;

import br.ucsal.edugestao.usuario.entity.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "denuncia")
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String protocolo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private boolean anonima = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "denunciante_id")
    private Usuario denunciante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAtendimento status = StatusAtendimento.RECEBIDA;

    @Column(columnDefinition = "TEXT")
    private String medidas;

    @Column(name = "justificativa_arquivamento", columnDefinition = "TEXT")
    private String justificativaArquivamento;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm = LocalDateTime.now();

    @Column(name = "atualizada_em", nullable = false)
    private LocalDateTime atualizadaEm = LocalDateTime.now();

    public void encaminhar() {
        encaminhar(this.medidas);
    }

    public void encaminhar(String medidasTomadas) {
        this.status = StatusAtendimento.ENCAMINHADA;
        this.medidas = medidasTomadas;
        this.atualizadaEm = LocalDateTime.now();
    }

    public void arquivar(String justificativa) {
        this.status = StatusAtendimento.ARQUIVADA;
        this.justificativaArquivamento = justificativa;
        this.atualizadaEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAnonima() {
        return anonima;
    }

    public void setAnonima(boolean anonima) {
        this.anonima = anonima;
    }

    public Usuario getDenunciante() {
        return denunciante;
    }

    public void setDenunciante(Usuario denunciante) {
        this.denunciante = denunciante;
    }

    public StatusAtendimento getStatus() {
        return status;
    }

    public void setStatus(StatusAtendimento status) {
        this.status = status;
    }

    public String getMedidas() {
        return medidas;
    }

    public void setMedidas(String medidas) {
        this.medidas = medidas;
    }

    public String getJustificativaArquivamento() {
        return justificativaArquivamento;
    }

    public void setJustificativaArquivamento(String justificativaArquivamento) {
        this.justificativaArquivamento = justificativaArquivamento;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(LocalDateTime criadaEm) {
        this.criadaEm = criadaEm;
    }

    public LocalDateTime getAtualizadaEm() {
        return atualizadaEm;
    }

    public void setAtualizadaEm(LocalDateTime atualizadaEm) {
        this.atualizadaEm = atualizadaEm;
    }
}
