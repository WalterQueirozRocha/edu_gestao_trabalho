CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(100) NOT NULL,
    perfil VARCHAR(30) NOT NULL CHECK (perfil IN (
        'ADMINISTRADOR',
        'COORDENADOR',
        'PROFESSOR',
        'PROFISSIONAL_APOIO',
        'ALUNO',
        'RESPONSAVEL'
    )),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cargo VARCHAR(100),
    registro_funcional VARCHAR(30) UNIQUE,
    formacao VARCHAR(150),
    especialidade VARCHAR(100),
    matricula VARCHAR(30) UNIQUE,
    data_nascimento DATE,
    cpf VARCHAR(14) UNIQUE,
    telefone VARCHAR(20)
);

CREATE TABLE aluno_responsavel (
    aluno_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    responsavel_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    PRIMARY KEY (aluno_id, responsavel_id)
);

CREATE TABLE ano_letivo (
    id BIGSERIAL PRIMARY KEY,
    ano INT NOT NULL UNIQUE,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    media_minima NUMERIC(4,2) NOT NULL DEFAULT 6.00,
    frequencia_minima NUMERIC(5,2) NOT NULL DEFAULT 75.00
);

CREATE TABLE etapa (
    id BIGSERIAL PRIMARY KEY,
    ano_letivo_id BIGINT NOT NULL REFERENCES ano_letivo(id) ON DELETE RESTRICT,
    nome VARCHAR(100) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ABERTA' CHECK (status IN ('ABERTA', 'FECHADA', 'PUBLICADA'))
);

CREATE TABLE turma (
    id BIGSERIAL PRIMARY KEY,
    ano_letivo_id BIGINT NOT NULL REFERENCES ano_letivo(id) ON DELETE RESTRICT,
    nome VARCHAR(100) NOT NULL,
    serie VARCHAR(60) NOT NULL,
    turno VARCHAR(20) NOT NULL CHECK (turno IN ('MATUTINO', 'VESPERTINO', 'NOTURNO', 'INTEGRAL')),
    UNIQUE (ano_letivo_id, nome)
);

CREATE TABLE disciplina (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    codigo VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE turma_disciplina (
    id BIGSERIAL PRIMARY KEY,
    turma_id BIGINT NOT NULL REFERENCES turma(id) ON DELETE RESTRICT,
    disciplina_id BIGINT NOT NULL REFERENCES disciplina(id) ON DELETE RESTRICT,
    professor_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    carga_horaria INT NOT NULL CHECK (carga_horaria > 0),
    UNIQUE (turma_id, disciplina_id)
);

CREATE TABLE matricula (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    turma_id BIGINT NOT NULL REFERENCES turma(id) ON DELETE RESTRICT,
    data_matricula DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVA' CHECK (status IN ('ATIVA', 'ENCERRADA', 'TRANSFERIDA', 'CANCELADA'))
);

-- RN-01: Impede duas matriculas ativas do mesmo aluno na mesma turma
CREATE UNIQUE INDEX uk_matricula_ativa_aluno_turma
    ON matricula (aluno_id, turma_id)
    WHERE status = 'ATIVA';

CREATE TABLE aula (
    id BIGSERIAL PRIMARY KEY,
    turma_disciplina_id BIGINT NOT NULL REFERENCES turma_disciplina(id) ON DELETE RESTRICT,
    data DATE NOT NULL,
    conteudo TEXT NOT NULL,
    horas INT NOT NULL CHECK (horas > 0)
);

CREATE TABLE frequencia (
    id BIGSERIAL PRIMARY KEY,
    aula_id BIGINT NOT NULL REFERENCES aula(id) ON DELETE RESTRICT,
    matricula_id BIGINT NOT NULL REFERENCES matricula(id) ON DELETE RESTRICT,
    presente BOOLEAN NOT NULL,
    justificativa VARCHAR(255),
    UNIQUE (aula_id, matricula_id)
);

CREATE TABLE avaliacao (
    id BIGSERIAL PRIMARY KEY,
    turma_disciplina_id BIGINT NOT NULL REFERENCES turma_disciplina(id) ON DELETE RESTRICT,
    etapa_id BIGINT NOT NULL REFERENCES etapa(id) ON DELETE RESTRICT,
    descricao VARCHAR(150) NOT NULL,
    data DATE NOT NULL,
    peso NUMERIC(5,2) NOT NULL CHECK (peso > 0)
);

CREATE TABLE nota (
    id BIGSERIAL PRIMARY KEY,
    avaliacao_id BIGINT NOT NULL REFERENCES avaliacao(id) ON DELETE RESTRICT,
    matricula_id BIGINT NOT NULL REFERENCES matricula(id) ON DELETE RESTRICT,
    valor NUMERIC(4,2) NOT NULL CHECK (valor >= 0 AND valor <= 10),
    data_lancamento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (avaliacao_id, matricula_id)
);

CREATE TABLE boletim (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL REFERENCES matricula(id) ON DELETE RESTRICT,
    etapa_id BIGINT NOT NULL REFERENCES etapa(id) ON DELETE RESTRICT,
    data_publicacao DATE,
    situacao VARCHAR(20) NOT NULL CHECK (situacao IN ('EM_ANDAMENTO', 'APROVADO', 'RETIDO')),
    UNIQUE (matricula_id, etapa_id)
);

CREATE TABLE autorizacao_alteracao (
    id BIGSERIAL PRIMARY KEY,
    etapa_id BIGINT NOT NULL REFERENCES etapa(id) ON DELETE RESTRICT,
    turma_disciplina_id BIGINT NOT NULL REFERENCES turma_disciplina(id) ON DELETE RESTRICT,
    coordenador_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    motivo TEXT NOT NULL,
    criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valida_ate TIMESTAMP NOT NULL,
    utilizada BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE comunicado (
    id BIGSERIAL PRIMARY KEY,
    autor_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    titulo VARCHAR(150) NOT NULL,
    mensagem TEXT NOT NULL,
    data_publicacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comunicado_turma (
    comunicado_id BIGINT NOT NULL REFERENCES comunicado(id) ON DELETE RESTRICT,
    turma_id BIGINT NOT NULL REFERENCES turma(id) ON DELETE RESTRICT,
    PRIMARY KEY (comunicado_id, turma_id)
);

CREATE TABLE comunicado_perfil (
    comunicado_id BIGINT NOT NULL REFERENCES comunicado(id) ON DELETE RESTRICT,
    perfil VARCHAR(30) NOT NULL CHECK (perfil IN (
        'ADMINISTRADOR',
        'COORDENADOR',
        'PROFESSOR',
        'PROFISSIONAL_APOIO',
        'ALUNO',
        'RESPONSAVEL'
    )),
    PRIMARY KEY (comunicado_id, perfil)
);

CREATE TABLE ocorrencia (
    id BIGSERIAL PRIMARY KEY,
    registrada_por_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    data DATE NOT NULL,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('ATRASO', 'INDISCIPLINA', 'CONFLITO', 'PEDAGOGICA', 'OUTRO')),
    descricao TEXT NOT NULL
);

CREATE TABLE ocorrencia_aluno (
    ocorrencia_id BIGINT NOT NULL REFERENCES ocorrencia(id) ON DELETE RESTRICT,
    aluno_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    PRIMARY KEY (ocorrencia_id, aluno_id)
);

CREATE TABLE denuncia (
    id BIGSERIAL PRIMARY KEY,
    protocolo VARCHAR(40) NOT NULL UNIQUE,
    descricao TEXT NOT NULL,
    anonima BOOLEAN NOT NULL DEFAULT FALSE,
    denunciante_id BIGINT REFERENCES usuario(id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL DEFAULT 'RECEBIDA' CHECK (status IN (
        'RECEBIDA',
        'EM_ANALISE',
        'ENCAMINHADA',
        'ARQUIVADA',
        'CONCLUIDA'
    )),
    medidas TEXT,
    justificativa_arquivamento TEXT,
    criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_denuncia_anonima CHECK (NOT anonima OR denunciante_id IS NULL)
);

CREATE TABLE modelo_preditivo (
    id BIGSERIAL PRIMARY KEY,
    versao VARCHAR(40) NOT NULL,
    data_treinamento DATE NOT NULL,
    acuracia NUMERIC(5,4) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE alerta_risco (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    modelo_id BIGINT REFERENCES modelo_preditivo(id) ON DELETE RESTRICT,
    nivel VARCHAR(20) NOT NULL CHECK (nivel IN ('BAIXO', 'MEDIO', 'ALTO')),
    probabilidade NUMERIC(5,4) NOT NULL,
    fatores TEXT NOT NULL,
    sugestoes TEXT NOT NULL,
    data_geracao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'NOVO' CHECK (status IN ('NOVO', 'DESCARTADO', 'CONVERTIDO'))
);

CREATE TABLE plano_pedagogico (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    coordenador_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    alerta_origem_id BIGINT REFERENCES alerta_risco(id) ON DELETE RESTRICT,
    metas TEXT NOT NULL,
    adaptacoes TEXT NOT NULL,
    estrategias TEXT NOT NULL,
    data_revisao DATE NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE plano_profissional_apoio (
    plano_id BIGINT NOT NULL REFERENCES plano_pedagogico(id) ON DELETE RESTRICT,
    profissional_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    PRIMARY KEY (plano_id, profissional_id)
);

-- RNF-01: Indices para consultas frequentes com tempo de resposta <= 2s
CREATE INDEX idx_usuario_perfil ON usuario(perfil);
CREATE INDEX idx_aluno_responsavel_resp ON aluno_responsavel(responsavel_id);
CREATE INDEX idx_etapa_ano_letivo ON etapa(ano_letivo_id);
CREATE INDEX idx_turma_ano_letivo ON turma(ano_letivo_id);
CREATE INDEX idx_turma_disciplina_turma ON turma_disciplina(turma_id);
CREATE INDEX idx_turma_disciplina_professor ON turma_disciplina(professor_id);
CREATE INDEX idx_matricula_aluno ON matricula(aluno_id);
CREATE INDEX idx_matricula_turma ON matricula(turma_id);
CREATE INDEX idx_aula_turma_disciplina ON aula(turma_disciplina_id);
CREATE INDEX idx_frequencia_matricula ON frequencia(matricula_id);
CREATE INDEX idx_avaliacao_turma_disc_etapa ON avaliacao(turma_disciplina_id, etapa_id);
CREATE INDEX idx_nota_matricula ON nota(matricula_id);
CREATE INDEX idx_boletim_matricula ON boletim(matricula_id);
CREATE INDEX idx_ocorrencia_data ON ocorrencia(data);
CREATE INDEX idx_denuncia_status ON denuncia(status);
CREATE INDEX idx_plano_aluno ON plano_pedagogico(aluno_id);
CREATE INDEX idx_alerta_aluno_status ON alerta_risco(aluno_id, status);
