package br.ucsal.edugestao.diario.service;

import br.ucsal.edugestao.academico.entity.AnoLetivo;
import br.ucsal.edugestao.academico.entity.Matricula;
import br.ucsal.edugestao.academico.entity.Turma;
import br.ucsal.edugestao.diario.dto.ResumoFrequenciaDto;
import br.ucsal.edugestao.diario.entity.Aula;
import br.ucsal.edugestao.diario.entity.Frequencia;
import br.ucsal.edugestao.usuario.entity.Aluno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// RN-02: frequencia calculada por horas de aula, minimo de 75% e alerta preventivo abaixo de 80%
@ExtendWith(MockitoExtension.class)
class FrequenciaServiceTest {

    @InjectMocks
    private FrequenciaService service;

    private Matricula matricula;

    @BeforeEach
    void setUp() {
        AnoLetivo anoLetivo = new AnoLetivo();
        anoLetivo.setAno(2026);
        anoLetivo.setFrequenciaMinima(new BigDecimal("75.00"));

        Turma turma = new Turma();
        turma.setId(10L);
        turma.setAnoLetivo(anoLetivo);

        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Aluno Teste");

        matricula = new Matricula();
        matricula.setId(5L);
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
    }

    private Frequencia registro(int horas, boolean presente) {
        Aula aula = new Aula();
        aula.setHoras(horas);
        Frequencia f = new Frequencia();
        f.setAula(aula);
        f.setMatricula(matricula);
        f.setPresente(presente);
        return f;
    }

    private List<Frequencia> registros(int horasPresentes, int horasFaltas) {
        List<Frequencia> lista = new ArrayList<>();
        for (int i = 0; i < horasPresentes; i++) {
            lista.add(registro(1, true));
        }
        for (int i = 0; i < horasFaltas; i++) {
            lista.add(registro(1, false));
        }
        return lista;
    }

    @Test
    @DisplayName("Sem aulas registradas a frequencia e 100% e nao gera alerta")
    void semRegistros() {
        ResumoFrequenciaDto resumo = service.montarResumo(matricula, List.of());

        assertThat(resumo.percentualPresenca()).isEqualByComparingTo("100.00");
        assertThat(resumo.alertaPreventivoAbaixo80()).isFalse();
    }

    @Test
    @DisplayName("Frequencia considera horas de aula, nao quantidade de aulas")
    void calculaPorHoras() {
        // Uma aula de 3h presente e uma de 1h com falta: 75% por horas (seria 50% por aulas)
        List<Frequencia> lista = List.of(registro(3, true), registro(1, false));

        ResumoFrequenciaDto resumo = service.montarResumo(matricula, lista);

        assertThat(resumo.horasRegistradas()).isEqualTo(4);
        assertThat(resumo.horasPresentes()).isEqualTo(3);
        assertThat(resumo.horasFaltas()).isEqualTo(1);
        assertThat(resumo.percentualPresenca()).isEqualByComparingTo("75.00");
    }

    @Test
    @DisplayName("85% de presenca: aprovado e sem alerta")
    void acimaDe80() {
        ResumoFrequenciaDto resumo = service.montarResumo(matricula, registros(17, 3));

        assertThat(resumo.percentualPresenca()).isEqualByComparingTo("85.00");
        assertThat(resumo.aprovadoPorFrequencia()).isTrue();
        assertThat(resumo.alertaPreventivoAbaixo80()).isFalse();
    }

    @Test
    @DisplayName("Exatamente 80%: sem alerta preventivo")
    void exatamente80() {
        ResumoFrequenciaDto resumo = service.montarResumo(matricula, registros(16, 4));

        assertThat(resumo.percentualPresenca()).isEqualByComparingTo("80.00");
        assertThat(resumo.alertaPreventivoAbaixo80()).isFalse();
    }

    @Test
    @DisplayName("Entre 75% e 80%: atinge o minimo, mas gera alerta preventivo")
    void entre75e80() {
        ResumoFrequenciaDto resumo = service.montarResumo(matricula, registros(39, 11));

        assertThat(resumo.percentualPresenca()).isEqualByComparingTo("78.00");
        assertThat(resumo.aprovadoPorFrequencia()).isTrue();
        assertThat(resumo.alertaPreventivoAbaixo80()).isTrue();
    }

    @Test
    @DisplayName("Exatamente 75%: atinge o minimo legal")
    void exatamente75() {
        ResumoFrequenciaDto resumo = service.montarResumo(matricula, registros(3, 1));

        assertThat(resumo.aprovadoPorFrequencia()).isTrue();
    }

    @Test
    @DisplayName("Abaixo de 75%: nao atinge o minimo legal")
    void abaixoDe75() {
        ResumoFrequenciaDto resumo = service.montarResumo(matricula, registros(7, 3));

        assertThat(resumo.percentualPresenca()).isEqualByComparingTo("70.00");
        assertThat(resumo.aprovadoPorFrequencia()).isFalse();
        assertThat(resumo.alertaPreventivoAbaixo80()).isTrue();
    }
}
