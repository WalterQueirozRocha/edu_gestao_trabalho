package br.ucsal.edugestao.ia.service;

import br.ucsal.edugestao.academico.entity.Matricula;
import br.ucsal.edugestao.academico.entity.StatusMatricula;
import br.ucsal.edugestao.academico.entity.TurmaDisciplina;
import br.ucsal.edugestao.academico.repository.MatriculaRepository;
import br.ucsal.edugestao.academico.repository.TurmaDisciplinaRepository;
import br.ucsal.edugestao.avaliacao.entity.Nota;
import br.ucsal.edugestao.avaliacao.repository.NotaRepository;
import br.ucsal.edugestao.avaliacao.service.AvaliacaoService;
import br.ucsal.edugestao.config.RecursoNaoEncontradoException;
import br.ucsal.edugestao.diario.dto.ResumoFrequenciaDto;
import br.ucsal.edugestao.diario.entity.Frequencia;
import br.ucsal.edugestao.diario.repository.FrequenciaRepository;
import br.ucsal.edugestao.diario.service.FrequenciaService;
import br.ucsal.edugestao.ia.dto.AlertaRiscoDto;
import br.ucsal.edugestao.ia.dto.ConverterAlertaRequest;
import br.ucsal.edugestao.ia.dto.FeaturesAlunoDto;
import br.ucsal.edugestao.ia.dto.ModeloPreditivoDto;
import br.ucsal.edugestao.ia.entity.AlertaRisco;
import br.ucsal.edugestao.ia.entity.ModeloPreditivo;
import br.ucsal.edugestao.ia.entity.NivelRisco;
import br.ucsal.edugestao.ia.entity.StatusAlerta;
import br.ucsal.edugestao.ia.repository.AlertaRiscoRepository;
import br.ucsal.edugestao.ia.repository.ModeloPreditivoRepository;
import br.ucsal.edugestao.ocorrencia.repository.OcorrenciaRepository;
import br.ucsal.edugestao.pedagogico.dto.PlanoPedagogicoDto;
import br.ucsal.edugestao.pedagogico.entity.PlanoPedagogico;
import br.ucsal.edugestao.pedagogico.repository.PlanoPedagogicoRepository;
import br.ucsal.edugestao.pedagogico.service.PlanoPedagogicoService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tribuo.Model;
import org.tribuo.MutableDataset;
import org.tribuo.Prediction;
import org.tribuo.classification.Label;
import org.tribuo.classification.LabelFactory;
import org.tribuo.classification.evaluation.LabelEvaluation;
import org.tribuo.classification.evaluation.LabelEvaluator;
import org.tribuo.classification.sgd.linear.LogisticRegressionTrainer;
import org.tribuo.impl.ArrayExample;
import org.tribuo.provenance.SimpleDataSourceProvenance;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PredicaoRiscoService {

    private static final String[] FEATURE_NAMES = {
            "media_geral",
            "menor_media_disciplina",
            "disciplinas_abaixo_media",
            "frequencia_geral",
            "queda_frequencia",
            "num_ocorrencias"
    };

    private final ModeloPreditivoRepository modeloRepository;
    private final AlertaRiscoRepository alertaRepository;
    private final MatriculaRepository matriculaRepository;
    private final TurmaDisciplinaRepository turmaDisciplinaRepository;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final OcorrenciaRepository ocorrenciaRepository;
    private final PlanoPedagogicoRepository planoRepository;
    private final PlanoPedagogicoService planoService;
    private final AvaliacaoService avaliacaoService;
    private final FrequenciaService frequenciaService;

    private static final int MINIMO_AMOSTRAS_TREINO = 30;

    private volatile Model<Label> modeloTribuoEmMemoria;
    private volatile int amostrasTreino = 0;

    public PredicaoRiscoService(
            ModeloPreditivoRepository modeloRepository,
            AlertaRiscoRepository alertaRepository,
            MatriculaRepository matriculaRepository,
            TurmaDisciplinaRepository turmaDisciplinaRepository,
            NotaRepository notaRepository,
            FrequenciaRepository frequenciaRepository,
            OcorrenciaRepository ocorrenciaRepository,
            PlanoPedagogicoRepository planoRepository,
            PlanoPedagogicoService planoService,
            AvaliacaoService avaliacaoService,
            FrequenciaService frequenciaService
    ) {
        this.modeloRepository = modeloRepository;
        this.alertaRepository = alertaRepository;
        this.matriculaRepository = matriculaRepository;
        this.turmaDisciplinaRepository = turmaDisciplinaRepository;
        this.notaRepository = notaRepository;
        this.frequenciaRepository = frequenciaRepository;
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.planoRepository = planoRepository;
        this.planoService = planoService;
        this.avaliacaoService = avaliacaoService;
        this.frequenciaService = frequenciaService;
    }

    @Transactional
    public ModeloPreditivoDto treinarModelo() {
        LabelFactory labelFactory = new LabelFactory();
        SimpleDataSourceProvenance provenance = new SimpleDataSourceProvenance("historico_treino.csv", labelFactory);
        MutableDataset<Label> trainDataset = new MutableDataset<>(provenance, labelFactory);
        MutableDataset<Label> testDataset = new MutableDataset<>(provenance, labelFactory);

        List<ArrayExample<Label>> exemplos = carregarExemplosCsv();
        this.amostrasTreino = exemplos.size();
        for (int i = 0; i < exemplos.size(); i++) {
            if (i % 5 == 0) {
                testDataset.add(exemplos.get(i));
            } else {
                trainDataset.add(exemplos.get(i));
            }
        }

        LogisticRegressionTrainer trainer = new LogisticRegressionTrainer();
        Model<Label> model = trainer.train(trainDataset);
        this.modeloTribuoEmMemoria = model;

        LabelEvaluator evaluator = new LabelEvaluator();
        LabelEvaluation evaluation = evaluator.evaluate(model, testDataset);
        BigDecimal acuracia = BigDecimal.valueOf(evaluation.accuracy()).setScale(4, RoundingMode.HALF_UP);

        modeloRepository.findAll().forEach(m -> {
            m.setAtivo(false);
            modeloRepository.save(m);
        });

        ModeloPreditivo novo = new ModeloPreditivo();
        novo.setVersao("vTribuo-" + LocalDate.now());
        novo.treinar(acuracia);

        return ModeloPreditivoDto.fromEntity(modeloRepository.save(novo));
    }

    // RF-11, UC14, UC15, UC18, RN-09: Executa predicao e gera alertas de apoio a decisao
    @Transactional
    public List<AlertaRiscoDto> executarPredicaoParaAlunosAtivos() {
        if (this.modeloTribuoEmMemoria == null) {
            try {
                treinarModelo();
            } catch (Exception ignored) {
                // Caso haja falha de leitura do CSV, utiliza fallback por regras
            }
        }
        boolean usarModeloMl = this.modeloTribuoEmMemoria != null && this.amostrasTreino >= MINIMO_AMOSTRAS_TREINO;
        ModeloPreditivo modeloAtivo = usarModeloMl
                ? modeloRepository.findFirstByAtivoTrueOrderByDataTreinamentoDescIdDesc().orElse(null)
                : null;

        List<Matricula> matriculasAtivas = matriculaRepository.findByStatusOrderByAlunoNomeAsc(StatusMatricula.ATIVA);
        List<AlertaRiscoDto> gerados = new ArrayList<>();

        for (Matricula m : matriculasAtivas) {
            FeaturesAlunoDto features = extrairFeatures(m);
            BigDecimal probabilidade = calcularProbabilidadeRisco(features);
            NivelRisco nivel = classificarNivelRisco(probabilidade);

            if (nivel == NivelRisco.BAIXO) {
                continue;
            }

            List<AlertaRisco> alertasNovosExistentes = alertaRepository.findByAlunoIdAndStatus(
                    m.getAluno().getId(),
                    StatusAlerta.NOVO
            );
            AlertaRisco alerta = alertasNovosExistentes.isEmpty()
                    ? new AlertaRisco()
                    : alertasNovosExistentes.get(0);

            alerta.setAluno(m.getAluno());
            alerta.setModelo(modeloAtivo);
            alerta.setNivel(nivel);
            alerta.setProbabilidade(probabilidade);
            alerta.setFatores(identificarFatoresContribuintes(features));
            alerta.setSugestoes(gerarSugestoesPedagogicas(features));
            alerta.setDataGeracao(LocalDateTime.now());
            alerta.setStatus(StatusAlerta.NOVO);

            gerados.add(AlertaRiscoDto.fromEntity(alertaRepository.save(alerta)));
        }
        return gerados;
    }

    @Transactional(readOnly = true)
    public List<AlertaRiscoDto> listarAlertas() {
        return alertaRepository.findAllByOrderByProbabilidadeDescDataGeracaoDesc().stream()
                .map(AlertaRiscoDto::fromEntity)
                .toList();
    }

    @Transactional
    public AlertaRiscoDto descartarAlerta(Long alertaId) {
        AlertaRisco alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta de risco nao encontrado."));
        alerta.descartar();
        return AlertaRiscoDto.fromEntity(alertaRepository.save(alerta));
    }

    @Transactional
    public PlanoPedagogicoDto converterEmPlano(Long alertaId, ConverterAlertaRequest req) {
        AlertaRisco alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta de risco nao encontrado."));

        String metas = (req != null && req.metas() != null && !req.metas().isBlank())
                ? req.metas()
                : "Recuperar frequencia para >= 85% e atingir media minima nas disciplinas em atencao.";
        String adaptacoes = (req != null && req.adaptacoes() != null && !req.adaptacoes().isBlank())
                ? req.adaptacoes()
                : "Avaliacoes em etapas progressivas e acompanhamento individualizado.";
        String estrategias = (req != null && req.estrategias() != null && !req.estrategias().isBlank())
                ? req.estrategias()
                : alerta.getSugestoes();
        LocalDate dataRevisao = (req != null && req.dataRevisao() != null)
                ? req.dataRevisao()
                : LocalDate.now().plusDays(30);

        PlanoPedagogicoDto novoPlanoDto = new PlanoPedagogicoDto(
                null,
                alerta.getAluno().getId(),
                alerta.getAluno().getNome(),
                alerta.getAluno().getMatricula(),
                null,
                null,
                alerta.getId(),
                metas,
                adaptacoes,
                estrategias,
                dataRevisao,
                null,
                List.of(),
                List.of()
        );
        PlanoPedagogicoDto salvo = planoService.criar(novoPlanoDto);
        alerta.marcarConvertido();
        alertaRepository.save(alerta);
        return salvo;
    }

    public NivelRisco classificarNivelRisco(BigDecimal probabilidade) {
        if (probabilidade.compareTo(new BigDecimal("0.3000")) < 0) {
            return NivelRisco.BAIXO;
        }
        if (probabilidade.compareTo(new BigDecimal("0.6000")) <= 0) {
            return NivelRisco.MEDIO;
        }
        return NivelRisco.ALTO;
    }

    public BigDecimal calcularProbabilidadeRisco(FeaturesAlunoDto f) {
        BigDecimal probRegras = calcularProbabilidadePorRegras(f);

        if (modeloTribuoEmMemoria != null && amostrasTreino >= MINIMO_AMOSTRAS_TREINO) {
            try {
                double[] valores = {
                        f.mediaGeral().doubleValue(),
                        f.menorMediaDisciplina().doubleValue(),
                        f.disciplinasAbaixoMedia(),
                        f.frequenciaGeral().doubleValue(),
                        f.quedaFrequencia().doubleValue(),
                        f.numOcorrencias()
                };
                ArrayExample<Label> exemplo = new ArrayExample<>(new Label("RETIDO"), FEATURE_NAMES, valores);
                Prediction<Label> pred = modeloTribuoEmMemoria.predict(exemplo);
                Label retidoScore = pred.getOutputScores().get("RETIDO");
                if (retidoScore != null && !Double.isNaN(retidoScore.getScore())) {
                    BigDecimal probTribuo = BigDecimal.valueOf(retidoScore.getScore())
                            .setScale(4, RoundingMode.HALF_UP);
                    // Garante que alertas preventivos de regimento (<80% freq ou <mediaMinima) nunca fiquem abaixo de MEDIO
                    return probTribuo.max(probRegras).setScale(4, RoundingMode.HALF_UP);
                }
            } catch (Exception ignored) {
                // Fallback deterministico por regras quando modelo indisponivel
            }
        }
        return probRegras;
    }

    public BigDecimal calcularProbabilidadePorRegras(FeaturesAlunoDto f) {
        BigDecimal score = new BigDecimal("0.0500");

        if (f.frequenciaGeral().compareTo(new BigDecimal("75.00")) < 0) {
            score = score.add(new BigDecimal("0.4200"));
        } else if (f.frequenciaGeral().compareTo(new BigDecimal("80.00")) < 0) {
            score = score.add(new BigDecimal("0.2800"));
        }

        if (f.mediaGeral().compareTo(f.mediaMinimaRegimento()) < 0) {
            score = score.add(new BigDecimal("0.3000"));
        } else if (f.disciplinasAbaixoMedia() > 0) {
            score = score.add(new BigDecimal("0.1800"));
        }

        if (f.quedaFrequencia().compareTo(new BigDecimal("10.00")) >= 0) {
            score = score.add(new BigDecimal("0.1000"));
        }

        if (f.numOcorrencias() >= 2) {
            score = score.add(new BigDecimal("0.1200"));
        } else if (f.numOcorrencias() == 1) {
            score = score.add(new BigDecimal("0.0600"));
        }

        if (score.compareTo(new BigDecimal("0.9800")) > 0) {
            score = new BigDecimal("0.9800");
        }
        return score.setScale(4, RoundingMode.HALF_UP);
    }

    public String identificarFatoresContribuintes(FeaturesAlunoDto f) {
        record FatorImpacto(String descricao, double impacto) {}
        List<FatorImpacto> impactos = new ArrayList<>();

        if (f.frequenciaGeral().compareTo(new BigDecimal("75.00")) < 0) {
            double desvio = (75.0 - f.frequenciaGeral().doubleValue()) / 75.0;
            impactos.add(new FatorImpacto(
                    "Frequencia abaixo do minimo legal de 75% (" + f.frequenciaGeral() + "%)",
                    0.42 + desvio
            ));
        } else if (f.frequenciaGeral().compareTo(new BigDecimal("80.00")) < 0) {
            double desvio = (80.0 - f.frequenciaGeral().doubleValue()) / 80.0;
            impactos.add(new FatorImpacto(
                    "Alerta preventivo de frequencia abaixo de 80% (" + f.frequenciaGeral() + "%)",
                    0.28 + desvio
            ));
        }
        if (f.mediaGeral().compareTo(f.mediaMinimaRegimento()) < 0) {
            double desvio = (f.mediaMinimaRegimento().doubleValue() - f.mediaGeral().doubleValue()) / 10.0;
            impactos.add(new FatorImpacto(
                    "Media geral abaixo da minima do regimento (" + f.mediaGeral() + " < " + f.mediaMinimaRegimento() + ")",
                    0.35 + desvio
            ));
        }
        if (f.disciplinasAbaixoMedia() > 0) {
            double desvio = f.disciplinasAbaixoMedia() * 0.10;
            impactos.add(new FatorImpacto(
                    f.disciplinasAbaixoMedia() + " disciplina(s) com media abaixo da minima (menor: " + f.menorMediaDisciplina() + ")",
                    0.22 + desvio
            ));
        }
        if (f.quedaFrequencia().compareTo(new BigDecimal("10.00")) >= 0) {
            double desvio = f.quedaFrequencia().doubleValue() / 100.0;
            impactos.add(new FatorImpacto(
                    "Queda recente de frequencia nas ultimas 4 semanas (" + f.quedaFrequencia() + " p.p.)",
                    0.15 + desvio
            ));
        }
        if (f.numOcorrencias() > 0) {
            double desvio = f.numOcorrencias() * 0.06;
            impactos.add(new FatorImpacto(
                    f.numOcorrencias() + " ocorrencia(s) escolar(es) registrada(s)",
                    0.12 + desvio
            ));
        }

        if (impactos.isEmpty()) {
            return "Oscilacao moderada nos indicadores de desempenho";
        }
        return impactos.stream()
                .sorted(Comparator.comparingDouble(FatorImpacto::impacto).reversed())
                .map(FatorImpacto::descricao)
                .reduce((a, b) -> a + "; " + b)
                .orElse("Oscilacao moderada nos indicadores de desempenho");
    }

    // UC18 e RN-08/RN-09: Sugestoes exclusivamente pedagogicas, sem diagnostico clinico
    public String gerarSugestoesPedagogicas(FeaturesAlunoDto f) {
        List<String> sugestoes = new ArrayList<>();
        String fatorPrincipalChave = "";

        if (f.frequenciaGeral().compareTo(new BigDecimal("80.00")) < 0) {
            sugestoes.add("Contato preventivo com o responsavel para alinhamento de assiduidade");
            fatorPrincipalChave = "frequencia";
        }
        if (f.mediaGeral().compareTo(f.mediaMinimaRegimento()) < 0 || f.disciplinasAbaixoMedia() > 0) {
            sugestoes.add("Atividades de reforco pedagogico no contraturno e monitoria orientada");
            sugestoes.add("Ajuste na forma de avaliacao em etapas menores com feedback continuo");
            if (fatorPrincipalChave.isEmpty()) {
                fatorPrincipalChave = "media";
            }
        }
        if (f.numOcorrencias() > 0) {
            sugestoes.add("Escuta pedagogica individualizada pela coordenacao e pactuacao de metas semanais");
            if (fatorPrincipalChave.isEmpty()) {
                fatorPrincipalChave = "ocorrencia";
            }
        }

        if (planoRepository != null) {
            List<PlanoPedagogico> planosExistentes = planoRepository.findAllByOrderByDataRevisaoAsc();
            if (!planosExistentes.isEmpty()) {
                final String chave = fatorPrincipalChave;
                PlanoPedagogico planoCompativel = planosExistentes.stream()
                        .filter(p -> !chave.isEmpty() && (
                                (p.getAlertaOrigem() != null && p.getAlertaOrigem().getFatores().toLowerCase().contains(chave))
                                        || p.getMetas().toLowerCase().contains(chave)
                                        || p.getEstrategias().toLowerCase().contains(chave)
                        ))
                        .findFirst()
                        .orElse(planosExistentes.get(0));
                sugestoes.add("Referencia de plano anterior: " + planoCompativel.getEstrategias());
            }
        }
        return String.join("; ", sugestoes);
    }

    private FeaturesAlunoDto extrairFeatures(Matricula m) {
        BigDecimal mediaMinima = m.getTurma().getAnoLetivo().getMediaMinima();
        List<TurmaDisciplina> disciplinas = turmaDisciplinaRepository.findByTurmaIdOrderByDisciplinaNomeAsc(
                m.getTurma().getId()
        );
        List<Nota> todasNotas = notaRepository.findByMatriculaId(m.getId());

        BigDecimal somaMedias = BigDecimal.ZERO;
        BigDecimal menorMedia = new BigDecimal("10.00");
        int abaixoMedia = 0;
        int comNotas = 0;

        for (TurmaDisciplina td : disciplinas) {
            List<Nota> notasDisc = todasNotas.stream()
                    .filter(n -> n.getAvaliacao().getTurmaDisciplina().getId().equals(td.getId()))
                    .toList();
            if (!notasDisc.isEmpty()) {
                BigDecimal mediaDisc = avaliacaoService.calcularMediaPonderada(notasDisc);
                somaMedias = somaMedias.add(mediaDisc);
                comNotas++;
                if (mediaDisc.compareTo(menorMedia) < 0) {
                    menorMedia = mediaDisc;
                }
                if (mediaDisc.compareTo(mediaMinima) < 0) {
                    abaixoMedia++;
                }
            }
        }

        BigDecimal mediaGeral = comNotas == 0
                ? new BigDecimal("7.00")
                : somaMedias.divide(BigDecimal.valueOf(comNotas), 2, RoundingMode.HALF_UP);
        if (comNotas == 0) {
            menorMedia = new BigDecimal("7.00");
        }

        ResumoFrequenciaDto resumoFreq = frequenciaService.calcularResumoFrequenciaInterno(m);
        BigDecimal quedaFreq = calcularQuedaFrequencia(m.getId());
        long numOcorrencias = ocorrenciaRepository.countByAlunoId(m.getAluno().getId());

        return new FeaturesAlunoDto(
                m.getAluno().getId(),
                m.getAluno().getNome(),
                mediaGeral,
                menorMedia,
                abaixoMedia,
                resumoFreq.percentualPresenca(),
                quedaFreq,
                numOcorrencias,
                mediaMinima
        );
    }

    private BigDecimal calcularQuedaFrequencia(Long matriculaId) {
        List<Frequencia> freqs = frequenciaRepository.findByMatriculaId(matriculaId).stream()
                .sorted(Comparator.comparing(f -> f.getAula().getData()))
                .toList();
        if (freqs.size() < 2) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        LocalDate ultimaData = freqs.get(freqs.size() - 1).getAula().getData();
        LocalDate corteQuatroSemanas = ultimaData.minusWeeks(4);

        List<Frequencia> anteriores = freqs.stream()
                .filter(f -> f.getAula().getData().isBefore(corteQuatroSemanas))
                .toList();
        List<Frequencia> recentes = freqs.stream()
                .filter(f -> !f.getAula().getData().isBefore(corteQuatroSemanas))
                .toList();

        if (anteriores.isEmpty() || recentes.isEmpty()) {
            int metade = freqs.size() / 2;
            anteriores = freqs.subList(0, metade);
            recentes = freqs.subList(metade, freqs.size());
        }

        double taxaAnt = anteriores.stream().filter(Frequencia::isPresente).count() * 100.0 / anteriores.size();
        double taxaRec = recentes.stream().filter(Frequencia::isPresente).count() * 100.0 / recentes.size();
        double queda = Math.max(0.0, taxaAnt - taxaRec);
        return BigDecimal.valueOf(queda).setScale(2, RoundingMode.HALF_UP);
    }

    private List<ArrayExample<Label>> carregarExemplosCsv() {
        List<ArrayExample<Label>> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource("ia/historico_treino.csv").getInputStream(),
                StandardCharsets.UTF_8
        ))) {
            String linha = br.readLine(); // cabecalho
            while ((linha = br.readLine()) != null) {
                if (linha.isBlank()) continue;
                String[] partes = linha.split(",");
                double[] valores = {
                        Double.parseDouble(partes[0]),
                        Double.parseDouble(partes[1]),
                        Double.parseDouble(partes[2]),
                        Double.parseDouble(partes[3]),
                        Double.parseDouble(partes[4]),
                        Double.parseDouble(partes[5])
                };
                String rotulo = partes[6].trim();
                lista.add(new ArrayExample<>(new Label(rotulo), FEATURE_NAMES, valores));
            }
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao ler CSV de treinamento da IA", e);
        }
        return lista;
    }
}
