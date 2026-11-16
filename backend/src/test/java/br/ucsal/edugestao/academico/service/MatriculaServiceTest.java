package br.ucsal.edugestao.academico.service;

import br.ucsal.edugestao.academico.dto.MatriculaDto;
import br.ucsal.edugestao.academico.entity.AnoLetivo;
import br.ucsal.edugestao.academico.entity.Matricula;
import br.ucsal.edugestao.academico.entity.StatusMatricula;
import br.ucsal.edugestao.academico.entity.Turma;
import br.ucsal.edugestao.academico.repository.MatriculaRepository;
import br.ucsal.edugestao.academico.repository.TurmaRepository;
import br.ucsal.edugestao.config.RegraNegocioException;
import br.ucsal.edugestao.seguranca.service.AcessoService;
import br.ucsal.edugestao.usuario.entity.Aluno;
import br.ucsal.edugestao.usuario.repository.AlunoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    private static final Long ALUNO_ID = 1L;
    private static final Long TURMA_ID = 10L;
    private static final Long ANO_LETIVO_ID = 100L;

    @Mock
    private MatriculaRepository matriculaRepository;
    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private TurmaRepository turmaRepository;
    @Mock
    private AcessoService acessoService;

    @InjectMocks
    private MatriculaService service;

    private Aluno aluno;
    private Turma turma;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(ALUNO_ID);
        aluno.setNome("Aluno Teste");

        AnoLetivo anoLetivo = new AnoLetivo();
        anoLetivo.setId(ANO_LETIVO_ID);
        anoLetivo.setAno(2026);

        turma = new Turma();
        turma.setId(TURMA_ID);
        turma.setNome("6A");
        turma.setAnoLetivo(anoLetivo);
    }

    private MatriculaDto novaMatriculaDto() {
        return new MatriculaDto(null, ALUNO_ID, null, null, TURMA_ID, null, null, null, LocalDate.of(2026, 2, 1), null);
    }

    @Test
    @DisplayName("Matricula aluno sem matricula ativa no ano letivo")
    void matriculaAlunoSemMatriculaAtiva() {
        when(alunoRepository.findById(ALUNO_ID)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(TURMA_ID)).thenReturn(Optional.of(turma));
        when(matriculaRepository.existsByAlunoIdAndTurmaIdAndStatus(ALUNO_ID, TURMA_ID, StatusMatricula.ATIVA)).thenReturn(false);
        when(matriculaRepository.existsByAlunoIdAndAnoLetivoIdAndStatus(ALUNO_ID, ANO_LETIVO_ID, StatusMatricula.ATIVA)).thenReturn(false);
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));

        MatriculaDto resultado = service.matricular(novaMatriculaDto());

        assertThat(resultado.status()).isEqualTo(StatusMatricula.ATIVA);
        assertThat(resultado.alunoId()).isEqualTo(ALUNO_ID);
        assertThat(resultado.turmaId()).isEqualTo(TURMA_ID);
    }

    @Test
    @DisplayName("RN-01: bloqueia segunda matricula ativa na mesma turma")
    void bloqueiaMatriculaDuplicadaNaTurma() {
        when(alunoRepository.findById(ALUNO_ID)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(TURMA_ID)).thenReturn(Optional.of(turma));
        when(matriculaRepository.existsByAlunoIdAndTurmaIdAndStatus(ALUNO_ID, TURMA_ID, StatusMatricula.ATIVA)).thenReturn(true);

        assertThatThrownBy(() -> service.matricular(novaMatriculaDto()))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("RN-01");
        verify(matriculaRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN-01: bloqueia segunda matricula ativa no mesmo ano letivo")
    void bloqueiaMatriculaDuplicadaNoAnoLetivo() {
        when(alunoRepository.findById(ALUNO_ID)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(TURMA_ID)).thenReturn(Optional.of(turma));
        when(matriculaRepository.existsByAlunoIdAndTurmaIdAndStatus(ALUNO_ID, TURMA_ID, StatusMatricula.ATIVA)).thenReturn(false);
        when(matriculaRepository.existsByAlunoIdAndAnoLetivoIdAndStatus(ALUNO_ID, ANO_LETIVO_ID, StatusMatricula.ATIVA)).thenReturn(true);

        assertThatThrownBy(() -> service.matricular(novaMatriculaDto()))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("RN-01")
                .hasMessageContaining("2026");
        verify(matriculaRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN-01: reativar matricula valida duplicidade antes de salvar")
    void reativarMatriculaValidaDuplicidade() {
        Matricula encerrada = new Matricula();
        encerrada.setId(5L);
        encerrada.setAluno(aluno);
        encerrada.setTurma(turma);
        encerrada.setStatus(StatusMatricula.ENCERRADA);

        when(matriculaRepository.findById(5L)).thenReturn(Optional.of(encerrada));
        when(matriculaRepository.existsByAlunoIdAndTurmaIdAndStatus(ALUNO_ID, TURMA_ID, StatusMatricula.ATIVA)).thenReturn(true);

        assertThatThrownBy(() -> service.alterarStatus(5L, StatusMatricula.ATIVA))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("RN-01");
        verify(matriculaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Encerrar matricula nao aplica a validacao da RN-01")
    void encerrarMatriculaNaoValidaDuplicidade() {
        Matricula ativa = new Matricula();
        ativa.setId(5L);
        ativa.setAluno(aluno);
        ativa.setTurma(turma);
        ativa.setStatus(StatusMatricula.ATIVA);

        when(matriculaRepository.findById(5L)).thenReturn(Optional.of(ativa));
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));

        MatriculaDto resultado = service.alterarStatus(5L, StatusMatricula.ENCERRADA);

        assertThat(resultado.status()).isEqualTo(StatusMatricula.ENCERRADA);
        verify(matriculaRepository, never()).existsByAlunoIdAndTurmaIdAndStatus(any(), any(), any());
    }
}
