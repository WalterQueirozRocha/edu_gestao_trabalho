package br.ucsal.edugestao.usuario.entity;

import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "perfil", discriminatorType = DiscriminatorType.STRING, length = 30)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", insertable = false, updatable = false, length = 30)
    private Perfil perfil;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public abstract Perfil getPerfilTipo();

    public boolean autenticar(String senha) {
        return this.ativo && senha != null && !senha.isBlank() && this.senhaHash != null;
    }

    public boolean autenticar(String senha, PasswordEncoder encoder) {
        return this.ativo && senha != null && this.senhaHash != null && encoder.matches(senha, this.senhaHash);
    }

    public boolean possuiPermissao(String acao) {
        if (!this.ativo || acao == null) {
            return false;
        }
        Perfil p = getPerfil();
        if (p == Perfil.ADMINISTRADOR) {
            return true;
        }
        return acao.toUpperCase().contains(p.name());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public Perfil getPerfil() {
        return perfil != null ? perfil : getPerfilTipo();
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
