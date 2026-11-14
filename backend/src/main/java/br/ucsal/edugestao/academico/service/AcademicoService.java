package br.ucsal.edugestao.academico.service;

import br.ucsal.edugestao.academico.dto.*;
import br.ucsal.edugestao.academico.entity.*;
import br.ucsal.edugestao.academico.repository.*;
import br.ucsal.edugestao.config.RecursoNaoEncontradoException;
import br.ucsal.edugestao.config.RegraNegocioException;
import br.ucsal.edugestao.usuario.entity.Professor;
import br.ucsal.edugestao.usuario.repository.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AcademicoService {

    private final AnoLetivoRepository anoLetivoRepository;
    private final EtapaRepository etapaRepository;
    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final TurmaDisciplinaRepository turmaDisciplinaRepository;
    private final ProfessorRepository professorRepository;

    public AcademicoService(
            AnoLetivoRepository anoLetivoRepository,
            EtapaRepository etapaRepository,
            TurmaRepository turmaRepository,
            DisciplinaRepository disciplinaRepository,
            TurmaDisciplinaRepository turmaDisciplinaRepository,
            ProfessorRepository professorRepository
    ) {
        this.anoLetivoRepository = anoLetivoRepository;
        this.etapaRepository = etapaRepository;
        this.turmaRepository = turmaRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.turmaDisciplinaRepository = turmaDisciplinaRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional(readOnly = true)
    public List<AnoLetivoDto> listarAnosLetivos() {
        return anoLetivoRepository.findAllByOrderByAnoDesc().stream()
                .map(AnoLetivoDto::fromEntity)
                .toList();
    }

    @Transactional
    public AnoLetivoDto criarAnoLetivo(AnoLetivoDto dto) {
        if (anoLetivoRepository.existsByAno(dto.ano())) {
            throw new RegraNegocioException("Ja existe um ano letivo cadastrado para " + dto.ano() + ".");
        }
        AnoLetivo entidade = new AnoLetivo();
        entidade.setAno(dto.ano());
        entidade.setDataInicio(dto.dataInicio());
        entidade.setDataFim(dto.dataFim());
        entidade.setMediaMinima(dto.mediaMinima());
        entidade.setFrequenciaMinima(dto.frequenciaMinima());
        entidade.setFormulaMedia(dto.formulaMedia() != null ? dto.formulaMedia() : FormulaMedia.PONDERADA);
        return AnoLetivoDto.fromEntity(anoLetivoRepository.save(entidade));
    }

    @Transactional
    public AnoLetivoDto atualizarAnoLetivo(Long id, AnoLetivoDto dto) {
        AnoLetivo entidade = anoLetivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ano letivo nao encontrado."));
        entidade.setAno(dto.ano());
        entidade.setDataInicio(dto.dataInicio());
        entidade.setDataFim(dto.dataFim());
        entidade.setMediaMinima(dto.mediaMinima());
        entidade.setFrequenciaMinima(dto.frequenciaMinima());
        entidade.setFormulaMedia(dto.formulaMedia() != null ? dto.formulaMedia() : FormulaMedia.PONDERADA);
        return AnoLetivoDto.fromEntity(anoLetivoRepository.save(entidade));
    }

    @Transactional(readOnly = true)
    public List<EtapaDto> listarEtapas(Long anoLetivoId) {
        List<Etapa> lista = anoLetivoId != null
                ? etapaRepository.findByAnoLetivoIdOrderByDataInicioAsc(anoLetivoId)
                : etapaRepository.findAllByOrderByDataInicioAsc();
        return lista.stream().map(EtapaDto::fromEntity).toList();
    }

    @Transactional
    public EtapaDto criarEtapa(EtapaDto dto) {
        AnoLetivo anoLetivo = anoLetivoRepository.findById(dto.anoLetivoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ano letivo nao encontrado."));
        Etapa etapa = new Etapa();
        etapa.setAnoLetivo(anoLetivo);
        etapa.setNome(dto.nome().trim());
        etapa.setDataInicio(dto.dataInicio());
        etapa.setDataFim(dto.dataFim());
        etapa.setStatus(StatusEtapa.ABERTA);
        return EtapaDto.fromEntity(etapaRepository.save(etapa));
    }

    @Transactional
    public EtapaDto atualizarEtapa(Long id, EtapaDto dto) {
        Etapa etapa = etapaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Etapa nao encontrada."));
        AnoLetivo anoLetivo = anoLetivoRepository.findById(dto.anoLetivoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ano letivo nao encontrado."));
        etapa.setAnoLetivo(anoLetivo);
        etapa.setNome(dto.nome().trim());
        etapa.setDataInicio(dto.dataInicio());
        etapa.setDataFim(dto.dataFim());
        return EtapaDto.fromEntity(etapaRepository.save(etapa));
    }

    @Transactional(readOnly = true)
    public List<TurmaDto> listarTurmas(Long anoLetivoId) {
        List<Turma> lista = anoLetivoId != null
                ? turmaRepository.findByAnoLetivoIdOrderByNomeAsc(anoLetivoId)
                : turmaRepository.findAllByOrderByNomeAsc();
        return lista.stream().map(TurmaDto::fromEntity).toList();
    }

    @Transactional
    public TurmaDto criarTurma(TurmaDto dto) {
        if (turmaRepository.existsByAnoLetivoIdAndNomeIgnoreCase(dto.anoLetivoId(), dto.nome().trim())) {
            throw new RegraNegocioException("Ja existe uma turma com este nome no ano letivo informado.");
        }
        AnoLetivo anoLetivo = anoLetivoRepository.findById(dto.anoLetivoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ano letivo nao encontrado."));
        Turma turma = new Turma();
        turma.setAnoLetivo(anoLetivo);
        turma.setNome(dto.nome().trim());
        turma.setSerie(dto.serie().trim());
        turma.setTurno(dto.turno());
        return TurmaDto.fromEntity(turmaRepository.save(turma));
    }

    @Transactional
    public TurmaDto atualizarTurma(Long id, TurmaDto dto) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma nao encontrada."));
        AnoLetivo anoLetivo = anoLetivoRepository.findById(dto.anoLetivoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ano letivo nao encontrado."));
        turma.setAnoLetivo(anoLetivo);
        turma.setNome(dto.nome().trim());
        turma.setSerie(dto.serie().trim());
        turma.setTurno(dto.turno());
        return TurmaDto.fromEntity(turmaRepository.save(turma));
    }

    @Transactional(readOnly = true)
    public List<DisciplinaDto> listarDisciplinas() {
        return disciplinaRepository.findAllByOrderByNomeAsc().stream()
                .map(DisciplinaDto::fromEntity)
                .toList();
    }

    @Transactional
    public DisciplinaDto criarDisciplina(DisciplinaDto dto) {
        if (disciplinaRepository.existsByCodigoIgnoreCase(dto.codigo().trim())) {
            throw new RegraNegocioException("Ja existe uma disciplina com o codigo informado.");
        }
        Disciplina d = new Disciplina();
        d.setNome(dto.nome().trim());
        d.setCodigo(dto.codigo().trim().toUpperCase());
        return DisciplinaDto.fromEntity(disciplinaRepository.save(d));
    }

    @Transactional
    public DisciplinaDto atualizarDisciplina(Long id, DisciplinaDto dto) {
        Disciplina d = disciplinaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina nao encontrada."));
        d.setNome(dto.nome().trim());
        d.setCodigo(dto.codigo().trim().toUpperCase());
        return DisciplinaDto.fromEntity(disciplinaRepository.save(d));
    }

    @Transactional(readOnly = true)
    public List<TurmaDisciplinaDto> listarDisciplinasDaTurma(Long turmaId) {
        return turmaDisciplinaRepository.findByTurmaIdOrderByDisciplinaNomeAsc(turmaId).stream()
                .map(TurmaDisciplinaDto::fromEntity)
                .toList();
    }

    @Transactional
    public TurmaDisciplinaDto vincularDisciplinaATurma(Long turmaId, TurmaDisciplinaDto dto) {
        if (turmaDisciplinaRepository.existsByTurmaIdAndDisciplinaId(turmaId, dto.disciplinaId())) {
            throw new RegraNegocioException("Esta disciplina ja esta vinculada a turma selecionada.");
        }
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma nao encontrada."));
        Disciplina disciplina = disciplinaRepository.findById(dto.disciplinaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina nao encontrada."));
        Professor professor = professorRepository.findById(dto.professorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Professor nao encontrado."));

        TurmaDisciplina td = new TurmaDisciplina();
        td.setTurma(turma);
        td.setDisciplina(disciplina);
        td.setProfessor(professor);
        td.setCargaHoraria(dto.cargaHoraria());
        return TurmaDisciplinaDto.fromEntity(turmaDisciplinaRepository.save(td));
    }
}
