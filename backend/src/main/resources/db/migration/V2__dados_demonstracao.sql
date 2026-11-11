-- Senha padrao de demonstracao para todos os usuarios: 123456
-- Hash BCrypt ($2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG)

INSERT INTO usuario (id, nome, email, senha_hash, perfil, ativo, cargo, registro_funcional, formacao, especialidade, matricula, data_nascimento, cpf, telefone)
VALUES
    (1, 'Administrador Geral', 'admin@edugestao.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'ADMINISTRADOR', TRUE, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (2, 'Helena Costa (Coordenadora)', 'coordenacao@edugestao.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'COORDENADOR', TRUE, 'Coordenadora Pedagogica Geral', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (3, 'Marcos Lima (Prof. Matematica/Fisica)', 'marcos.lima@edugestao.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'PROFESSOR', TRUE, NULL, 'PROF-2026-01', 'Licenciatura em Matematica', NULL, NULL, NULL, NULL, NULL),
    (4, 'Fernanda Souza (Profa. Portugues/Historia)', 'fernanda.souza@edugestao.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'PROFESSOR', TRUE, NULL, 'PROF-2026-02', 'Licenciatura em Letras', NULL, NULL, NULL, NULL, NULL),
    (5, 'Camila Mendes (Apoio Pedagogico)', 'apoio@edugestao.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'PROFISSIONAL_APOIO', TRUE, NULL, NULL, NULL, 'Atendimento Educacional Especializado (AEE)', NULL, NULL, NULL, NULL),
    (6, 'Roberto Alves (Responsavel)', 'roberto.alves@email.com', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'RESPONSAVEL', TRUE, NULL, NULL, NULL, NULL, NULL, NULL, '111.222.333-44', '(71) 99111-2233'),
    (7, 'Patricia Nunes (Responsavel)', 'patricia.nunes@email.com', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'RESPONSAVEL', TRUE, NULL, NULL, NULL, NULL, NULL, NULL, '555.666.777-88', '(71) 99222-3344'),
    (8, 'Lucas Alves', 'lucas.alves@aluno.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'ALUNO', TRUE, NULL, NULL, NULL, NULL, '20260001', '2011-03-14', NULL, NULL),
    (9, 'Beatriz Alves', 'beatriz.alves@aluno.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'ALUNO', TRUE, NULL, NULL, NULL, NULL, '20260002', '2011-07-22', NULL, NULL),
    (10, 'Gabriel Nunes', 'gabriel.nunes@aluno.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'ALUNO', TRUE, NULL, NULL, NULL, NULL, '20260003', '2010-11-05', NULL, NULL),
    (11, 'Mariana Santos', 'mariana.santos@aluno.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'ALUNO', TRUE, NULL, NULL, NULL, NULL, '20260004', '2011-01-19', NULL, NULL),
    (12, 'Pedro Henrique Oliveira', 'pedro.oliveira@aluno.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'ALUNO', TRUE, NULL, NULL, NULL, NULL, '20260005', '2010-09-30', NULL, NULL),
    (13, 'Julia Carvalho', 'julia.carvalho@aluno.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'ALUNO', TRUE, NULL, NULL, NULL, NULL, '20260006', '2011-05-12', NULL, NULL),
    (14, 'Rafael Ribeiro', 'rafael.ribeiro@aluno.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'ALUNO', TRUE, NULL, NULL, NULL, NULL, '20260007', '2010-04-08', NULL, NULL),
    (15, 'Ana Clara Martins', 'ana.martins@aluno.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'ALUNO', TRUE, NULL, NULL, NULL, NULL, '20260008', '2010-12-01', NULL, NULL),
    (16, 'Thiago Barros', 'thiago.barros@aluno.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'ALUNO', TRUE, NULL, NULL, NULL, NULL, '20260009', '2010-06-17', NULL, NULL),
    (17, 'Larissa Teixeira', 'larissa.teixeira@aluno.ucsal.br', '$2a$10$voJWJYorGaiVuH6G8jr0g.XB1i.rW.U.B30cr4aAWOBhoGwiXkdXG', 'ALUNO', TRUE, NULL, NULL, NULL, NULL, '20260010', '2010-08-25', NULL, NULL);

SELECT setval('usuario_id_seq', 17, true);

-- Vinculos Responsavel -> Aluno (RN-06)
INSERT INTO aluno_responsavel (aluno_id, responsavel_id) VALUES
    (8, 6),
    (9, 6),
    (10, 7),
    (11, 7);

-- Ano Letivo 2026
INSERT INTO ano_letivo (id, ano, data_inicio, data_fim, media_minima, frequencia_minima)
VALUES (1, 2026, '2026-02-02', '2026-12-18', 6.00, 75.00);

SELECT setval('ano_letivo_id_seq', 1, true);

-- 4 Etapas Letivas (I Unidade PUBLICADA, II Unidade FECHADA, III e IV ABERTAS)
INSERT INTO etapa (id, ano_letivo_id, nome, data_inicio, data_fim, status) VALUES
    (1, 1, 'I Unidade', '2026-02-02', '2026-04-15', 'PUBLICADA'),
    (2, 1, 'II Unidade', '2026-04-16', '2026-06-30', 'FECHADA'),
    (3, 1, 'III Unidade', '2026-08-03', '2026-10-09', 'ABERTA'),
    (4, 1, 'IV Unidade', '2026-10-10', '2026-12-18', 'ABERTA');

SELECT setval('etapa_id_seq', 4, true);

-- 2 Turmas
INSERT INTO turma (id, ano_letivo_id, nome, serie, turno) VALUES
    (1, 1, '9º Ano A', '9º Ano - Ensino Fundamental', 'MATUTINO'),
    (2, 1, '1º Ano EM B', '1º Ano - Ensino Medio', 'VESPERTINO');

SELECT setval('turma_id_seq', 2, true);

-- 4 Disciplinas
INSERT INTO disciplina (id, nome, codigo) VALUES
    (1, 'Matematica', 'MAT-01'),
    (2, 'Lingua Portuguesa', 'POR-01'),
    (3, 'Historia', 'HIS-01'),
    (4, 'Ciencias / Fisica', 'CIE-01');

SELECT setval('disciplina_id_seq', 4, true);

-- Turma-Disciplina com professores vinculados
INSERT INTO turma_disciplina (id, turma_id, disciplina_id, professor_id, carga_horaria) VALUES
    (1, 1, 1, 3, 80),
    (2, 1, 2, 4, 80),
    (3, 1, 3, 4, 60),
    (4, 1, 4, 3, 60),
    (5, 2, 1, 3, 80),
    (6, 2, 2, 4, 80);

SELECT setval('turma_disciplina_id_seq', 6, true);

-- Matriculas dos 10 alunos
INSERT INTO matricula (id, aluno_id, turma_id, data_matricula, status) VALUES
    (1, 8, 1, '2026-01-20', 'ATIVA'),
    (2, 9, 1, '2026-01-20', 'ATIVA'),
    (3, 10, 1, '2026-01-21', 'ATIVA'),
    (4, 11, 1, '2026-01-21', 'ATIVA'),
    (5, 12, 1, '2026-01-22', 'ATIVA'),
    (6, 13, 1, '2026-01-22', 'ATIVA'),
    (7, 14, 2, '2026-01-23', 'ATIVA'),
    (8, 15, 2, '2026-01-23', 'ATIVA'),
    (9, 16, 2, '2026-01-24', 'ATIVA'),
    (10, 17, 2, '2026-01-24', 'ATIVA');

SELECT setval('matricula_id_seq', 10, true);

-- Aulas de Matematica no 9º Ano A (5 aulas de 4h = 20h registradas)
INSERT INTO aula (id, turma_disciplina_id, data, conteudo, horas) VALUES
    (1, 1, '2026-02-10', 'Equacoes do 2º grau e formula resolutiva', 4),
    (2, 1, '2026-02-24', 'Funcoes afim e quadratica', 4),
    (3, 1, '2026-03-10', 'Teorema de Tales e semelhança de triangulos', 4),
    (4, 1, '2026-03-24', 'Relaçoes metricas no triangulo retangulo', 4),
    (5, 1, '2026-04-07', 'Revisao geral e resolucao de problemas', 4),
    (6, 2, '2026-02-12', 'Interpretacao textual e generos argumentativos', 4),
    (7, 2, '2026-03-12', 'Oracoes subordinadas substantivas e adjetivas', 4);

SELECT setval('aula_id_seq', 7, true);

-- Frequencias:
-- Matricula 1 (Lucas Alves): 3 presencas em 5 aulas de Mat + 1 presenca em 2 aulas de Port = 4/7 (57.14% -> risco ALTO)
-- Matricula 2 (Beatriz Alves): 100% presente
-- Matricula 3 (Gabriel Nunes): 4 presencas em 5 aulas de Mat + 1 presenca em 2 aulas de Port = 5/7 (71.43% -> abaixo de 75%)
-- Matricula 4 (Mariana Santos): 4 presencas em 5 de Mat + 2 em 2 de Port = 6/7 (85.71%)
-- Matricula 5 (Pedro Oliveira): 3 presencas em 5 de Mat + 1 em 2 de Port = 4/7 (57.14%)
INSERT INTO frequencia (aula_id, matricula_id, presente, justificativa) VALUES
    (1, 1, TRUE, NULL), (2, 1, FALSE, 'Atraso no transporte'), (3, 1, TRUE, NULL), (4, 1, FALSE, NULL), (5, 1, TRUE, NULL),
    (6, 1, TRUE, NULL), (7, 1, FALSE, NULL),
    (1, 2, TRUE, NULL), (2, 2, TRUE, NULL), (3, 2, TRUE, NULL), (4, 2, TRUE, NULL), (5, 2, TRUE, NULL),
    (6, 2, TRUE, NULL), (7, 2, TRUE, NULL),
    (1, 3, TRUE, NULL), (2, 3, TRUE, NULL), (3, 3, FALSE, NULL), (4, 3, TRUE, NULL), (5, 3, TRUE, NULL),
    (6, 3, TRUE, NULL), (7, 3, FALSE, 'Consulta medica'),
    (1, 4, TRUE, NULL), (2, 4, TRUE, NULL), (3, 4, TRUE, NULL), (4, 4, FALSE, NULL), (5, 4, TRUE, NULL),
    (6, 4, TRUE, NULL), (7, 4, TRUE, NULL),
    (1, 5, FALSE, NULL), (2, 5, TRUE, NULL), (3, 5, FALSE, NULL), (4, 5, TRUE, NULL), (5, 5, TRUE, NULL),
    (6, 5, FALSE, NULL), (7, 5, TRUE, NULL),
    (1, 6, TRUE, NULL), (2, 6, TRUE, NULL), (3, 6, TRUE, NULL), (4, 6, TRUE, NULL), (5, 6, TRUE, NULL),
    (6, 6, TRUE, NULL), (7, 6, TRUE, NULL);

-- Avaliacoes da I Unidade (PUBLICADA) e III Unidade (ABERTA)
INSERT INTO avaliacao (id, turma_disciplina_id, etapa_id, descricao, data, peso) VALUES
    (1, 1, 1, 'Prova Escrita - Equacoes e Funcoes', '2026-03-18', 6.00),
    (2, 1, 1, 'Lista de Exercicios Praticos', '2026-04-02', 4.00),
    (3, 2, 1, 'Producao Textual Dissertativa', '2026-03-20', 5.00),
    (4, 2, 1, 'Avaliacao Gramatical', '2026-04-05', 5.00),
    (5, 1, 3, 'Simulado Parcial III Unidade', '2026-08-25', 10.00);

SELECT setval('avaliacao_id_seq', 5, true);

-- Notas na I Unidade
-- Matricula 1 (Lucas): Mat 4.0 e 5.0 (media 4.40), Port 5.0 e 5.5 (media 5.25) -> Abaixo da media 6.00
-- Matricula 2 (Beatriz): Mat 8.5 e 9.0 (media 8.70), Port 9.0 e 8.0 (media 8.50) -> Aprovada
-- Matricula 3 (Gabriel): Mat 5.5 e 6.0 (media 5.70), Port 6.0 e 6.5 (media 6.25) -> Risco Medio
-- Matricula 4 (Mariana): Mat 7.5 e 8.0 (media 7.70), Port 8.0 e 7.5 (media 7.75)
-- Matricula 5 (Pedro): Mat 3.5 e 4.5 (media 3.90), Port 4.5 e 5.0 (media 4.75) -> Risco Alto
-- Matricula 6 (Julia): Mat 9.0 e 9.5 (media 9.20), Port 8.5 e 9.0 (media 8.75)
INSERT INTO nota (avaliacao_id, matricula_id, valor, data_lancamento) VALUES
    (1, 1, 4.00, '2026-03-25 10:00:00'), (2, 1, 5.00, '2026-04-08 10:00:00'),
    (3, 1, 5.00, '2026-03-26 11:00:00'), (4, 1, 5.50, '2026-04-09 11:00:00'),
    (1, 2, 8.50, '2026-03-25 10:05:00'), (2, 2, 9.00, '2026-04-08 10:05:00'),
    (3, 2, 9.00, '2026-03-26 11:05:00'), (4, 2, 8.00, '2026-04-09 11:05:00'),
    (1, 3, 5.50, '2026-03-25 10:10:00'), (2, 3, 6.00, '2026-04-08 10:10:00'),
    (3, 3, 6.00, '2026-03-26 11:10:00'), (4, 3, 6.50, '2026-04-09 11:10:00'),
    (1, 4, 7.50, '2026-03-25 10:15:00'), (2, 4, 8.00, '2026-04-08 10:15:00'),
    (3, 4, 8.00, '2026-03-26 11:15:00'), (4, 4, 7.50, '2026-04-09 11:15:00'),
    (1, 5, 3.50, '2026-03-25 10:20:00'), (2, 5, 4.50, '2026-04-08 10:20:00'),
    (3, 5, 4.50, '2026-03-26 11:20:00'), (4, 5, 5.00, '2026-04-09 11:20:00'),
    (1, 6, 9.00, '2026-03-25 10:25:00'), (2, 6, 9.50, '2026-04-08 10:25:00'),
    (3, 6, 8.50, '2026-03-26 11:25:00'), (4, 6, 9.00, '2026-04-09 11:25:00');

-- Boletins publicados da I Unidade
INSERT INTO boletim (matricula_id, etapa_id, data_publicacao, situacao) VALUES
    (1, 1, '2026-04-15', 'RETIDO'),
    (2, 1, '2026-04-15', 'APROVADO'),
    (3, 1, '2026-04-15', 'RETIDO'),
    (4, 1, '2026-04-15', 'APROVADO'),
    (5, 1, '2026-04-15', 'RETIDO'),
    (6, 1, '2026-04-15', 'APROVADO');

-- Comunicados
INSERT INTO comunicado (id, autor_id, titulo, mensagem, data_publicacao) VALUES
    (1, 2, 'Publicacao dos Boletins da I Unidade', 'Informamos que os boletins da I Unidade ja estao disponiveis para consulta pelos estudantes e responsaveis.', '2026-04-15 14:00:00'),
    (2, 2, 'Reuniao Pedagogica com Responsaveis do 9º Ano A', 'Convidamos os responsaveis do 9º Ano A para reuniao de acompanhamento de desempenho e frequencia.', '2026-04-20 09:00:00');

SELECT setval('comunicado_id_seq', 2, true);

INSERT INTO comunicado_perfil (comunicado_id, perfil) VALUES
    (1, 'ALUNO'), (1, 'RESPONSAVEL'), (1, 'PROFESSOR'),
    (2, 'RESPONSAVEL');

INSERT INTO comunicado_turma (comunicado_id, turma_id) VALUES
    (2, 1);

-- Ocorrencias
INSERT INTO ocorrencia (id, registrada_por_id, data, tipo, descricao) VALUES
    (1, 3, '2026-03-15', 'PEDAGOGICA', 'Dificuldade recorrente na entrega das listas de exercicios de Matematica.'),
    (2, 4, '2026-03-28', 'ATRASO', 'Atrasos consecutivos no primeiro horario da manha.');

SELECT setval('ocorrencia_id_seq', 2, true);

INSERT INTO ocorrencia_aluno (ocorrencia_id, aluno_id) VALUES
    (1, 8), (1, 12),
    (2, 8);

-- Denuncia de exemplo
INSERT INTO denuncia (id, protocolo, descricao, anonima, denunciante_id, status, medidas, criada_em, atualizada_em) VALUES
    (1, 'DEN-2026-0001', 'Relato de conflito recorrente entre estudantes no intervalo do turno matutino.', TRUE, NULL, 'EM_ANALISE', 'Coordenacao iniciou escuta individual e mediacao pedagogica.', '2026-04-10 10:30:00', '2026-04-11 15:00:00');

SELECT setval('denuncia_id_seq', 1, true);

-- Modelo Preditivo e Alertas iniciais
INSERT INTO modelo_preditivo (id, versao, data_treinamento, acuracia, ativo) VALUES
    (1, 'v1.0-2026', '2026-04-01', 0.8750, TRUE);

SELECT setval('modelo_preditivo_id_seq', 1, true);

INSERT INTO alerta_risco (id, aluno_id, modelo_id, nivel, probabilidade, fatores, sugestoes, data_geracao, status) VALUES
    (1, 8, 1, 'ALTO', 0.8200, 'Frequencia abaixo de 80% (57,1%); Media geral abaixo da minima (4,83); 2 ocorrencias registradas', 'Atividades de reforco no contraturno; Reuniao de alinhamento com o responsavel; Avaliacao diagnostica em etapas menores', '2026-04-15 16:00:00', 'CONVERTIDO'),
    (2, 12, 1, 'ALTO', 0.7800, 'Frequencia abaixo de 80% (57,1%); Media geral abaixo da minima (4,33); 1 ocorrencia pedagogica', 'Monitoria orientada em Matematica e Portugues; Contato imediato com a familia; Adaptacao do cronograma de atividades', '2026-04-15 16:05:00', 'NOVO'),
    (3, 10, 1, 'MEDIO', 0.5400, 'Frequencia abaixo de 75% (71,4%); Menor media por disciplina abaixo de 6,0 (Matematica: 5,70)', 'Acompanhamento preventivo de frequencia; Atividades extras de fixacao em Matematica', '2026-04-15 16:10:00', 'NOVO');

SELECT setval('alerta_risco_id_seq', 3, true);

-- Plano Pedagogico Inclusivo (RF-10, RN-08) originado do Alerta 1
INSERT INTO plano_pedagogico (id, aluno_id, coordenador_id, alerta_origem_id, metas, adaptacoes, estrategias, data_revisao, criado_em) VALUES
    (1, 8, 2, 1, 'Elevar a frequencia mensal para no minimo 85% e atingir media >= 6,0 em Matematica e Lingua Portuguesa ate o fim da III Unidade.', 'Avaliacoes parceladas em duas etapas com enunciados objetivos e tempo adicional de 20 minutos.', 'Encontros semanais com o apoio pedagogico, roteiro estruturado de estudos semanais e comunicacao quinzenal com o responsavel.', '2026-08-30', '2026-04-16 09:00:00');

SELECT setval('plano_pedagogico_id_seq', 1, true);

INSERT INTO plano_profissional_apoio (plano_id, profissional_id) VALUES
    (1, 5);
