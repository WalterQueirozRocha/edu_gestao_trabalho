package br.ucsal.edugestao.usuario.entity;

import br.ucsal.edugestao.academico.entity.Matricula;
import br.ucsal.edugestao.avaliacao.entity.Avaliacao;
import br.ucsal.edugestao.avaliacao.entity.Nota;
import br.ucsal.edugestao.diario.entity.Aula;
import br.ucsal.edugestao.diario.entity.Frequencia;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("PROFESSOR")
public class Professor extends Usuario {

    @Column(name = "registro_funcional", unique = true, length = 30)
    private String registroFuncional;

    @Column(length = 150)
    private String formacao;

    @Override
    public Perfil getPerfilTipo() {
        return Perfil.PROFESSOR;
    }

    public Nota lancarNota(Avaliacao avaliacao, Matricula matricula, BigDecimal valor) {
        Nota nota = new Nota();
        nota.setAvaliacao(avaliacao);
        nota.setMatricula(matricula);
        nota.setValor(valor);
        nota.setDataLancamento(LocalDateTime.now());
        return nota;
    }

    public Frequencia registrarFrequencia(Aula aula, Matricula matricula, boolean presente, String justificativa) {
        Frequencia freq = new Frequencia();
        freq.setAula(aula);
        freq.setMatricula(matricula);
        freq.setPresente(presente);
        freq.setJustificativa(justificativa);
        return freq;
    }

    public String getRegistroFuncional() {
        return registroFuncional;
    }

    public void setRegistroFuncional(String registroFuncional) {
        this.registroFuncional = registroFuncional;
    }

    public String getFormacao() {
        return formacao;
    }

    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }
}
