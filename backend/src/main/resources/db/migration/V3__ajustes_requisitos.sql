-- RF-11: situacao final de evasao para treino e registro
ALTER TABLE matricula DROP CONSTRAINT matricula_status_check;
ALTER TABLE matricula ADD CONSTRAINT matricula_status_check
    CHECK (status IN ('ATIVA', 'ENCERRADA', 'TRANSFERIDA', 'CANCELADA', 'EVADIDA'));

-- RF-11: alerta encerrado quando o risco volta a ser baixo
ALTER TABLE alerta_risco DROP CONSTRAINT alerta_risco_status_check;
ALTER TABLE alerta_risco ADD CONSTRAINT alerta_risco_status_check
    CHECK (status IN ('NOVO', 'DESCARTADO', 'CONVERTIDO', 'ENCERRADO'));

-- UC12: tratamento de ocorrencias
ALTER TABLE ocorrencia
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'RECEBIDA'
        CHECK (status IN ('RECEBIDA', 'EM_ANALISE', 'ENCAMINHADA', 'ARQUIVADA', 'CONCLUIDA')),
    ADD COLUMN medidas TEXT,
    ADD COLUMN atualizada_em TIMESTAMP;

-- RN-03: formula da media configuravel por ano letivo
ALTER TABLE ano_letivo
    ADD COLUMN formula_media VARCHAR(20) NOT NULL DEFAULT 'PONDERADA'
        CHECK (formula_media IN ('PONDERADA', 'ARITMETICA'));
