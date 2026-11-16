# EduGestão

Sistema web de gestão acadêmica e acompanhamento escolar para o Ensino Fundamental e Médio, com predição de risco de retenção e evasão por inteligência artificial.

Projeto semestral da disciplina **Tópicos Avançados em Programação**, do curso de Engenharia de Software da Universidade Católica do Salvador (UCSAL).

## Funcionalidades

| Perfil | O que faz no sistema |
|---|---|
| Administrador | Cadastra usuários, vínculos aluno-responsável, anos letivos, etapas, turmas, disciplinas e matrículas |
| Coordenador | Fecha e publica etapas, autoriza alterações de notas, publica comunicados, trata ocorrências e denúncias, mantém planos pedagógicos e analisa os alertas da IA |
| Professor | Diário de classe (aulas e frequência), avaliações, notas e registro de ocorrências |
| Profissional de apoio | Acompanha os planos pedagógicos dos estudantes autorizados |
| Aluno | Consulta boletim publicado e comunicados, registra denúncias |
| Responsável | Consulta boletim e frequência dos estudantes vinculados |

**Módulo de IA (RF-11).** Um modelo de regressão logística ([Oracle Tribuo](https://tribuo.org/)) estima a probabilidade de retenção ou evasão de cada estudante a partir de médias, frequência, queda de frequência e ocorrências. O risco é classificado em baixo (< 30%), médio (30% a 60%) ou alto (> 60%), e o coordenador recebe um alerta com os fatores contribuintes e sugestões pedagógicas. A predição roda toda segunda-feira às 6h e no fechamento de cada etapa. Enquanto não há histórico suficiente, o sistema usa regras de referência (frequência abaixo de 80% ou média abaixo da mínima). Os alertas são apoio à decisão e não contêm diagnóstico clínico (RN-09).

## Tecnologias

- **Back-end:** Java 21, Spring Boot 3.4, Spring Security (JWT), Spring Data JPA, Flyway, Oracle Tribuo
- **Front-end:** React 18, Vite 5, React Router 6
- **Banco de dados:** PostgreSQL 16

## Estrutura

```
backend/    API REST (Spring Boot), organizada por módulo de domínio
            academico, avaliacao, comunicacao, diario, ia, ocorrencia,
            pedagogico, seguranca, usuario, config
frontend/   SPA em React com telas por perfil
docs/c4/    Diagramas C4 (contexto, contêineres e componentes) em Mermaid
```

## Como executar

### Pré-requisitos

- Java 21 e Maven 3.9+
- Node.js 18+
- PostgreSQL 16

### 1. Banco de dados

Crie o banco e o usuário:

```sql
CREATE USER edugestao WITH PASSWORD 'sua_senha';
CREATE DATABASE edugestao OWNER edugestao;
```

As tabelas e os dados de demonstração são criados automaticamente pelo Flyway na primeira execução do back-end.

### 2. Back-end

Variáveis de ambiente:

| Variável | Obrigatória | Padrão | Descrição |
|---|---|---|---|
| `DB_PASSWORD` | sim | | Senha do usuário do banco |
| `JWT_SECRET` | sim | | Chave de assinatura dos tokens (HS256, mínimo de 32 caracteres) |
| `DB_URL` | não | `jdbc:postgresql://localhost:5432/edugestao` | URL JDBC do banco |
| `DB_USER` | não | `edugestao` | Usuário do banco |
| `PORT` | não | `8080` | Porta da API |
| `CORS_ORIGINS` | não | `http://localhost:5173,http://127.0.0.1:5173` | Origens permitidas |

```bash
cd backend
export DB_PASSWORD=sua_senha
export JWT_SECRET=uma_chave_secreta_com_pelo_menos_32_caracteres
mvn spring-boot:run
```

Também é possível criar o arquivo `backend/src/main/resources/application-local.yml` com essas configurações. Ele é importado automaticamente e não deve ser versionado.

Para produção, o perfil `prod` habilita HTTPS na porta 8443 (variáveis `SSL_KEYSTORE_PATH` e `SSL_KEYSTORE_PASSWORD`).

### 3. Front-end

```bash
cd frontend
npm install
npm run dev
```

Acesse http://localhost:5173. Em desenvolvimento, o Vite encaminha as chamadas `/api` para `http://localhost:8080`. Para apontar para outro endereço, use a variável `VITE_API_URL`.

## Usuários de demonstração

Todos usam a senha `123456`.

| Perfil | E-mail |
|---|---|
| Administrador | admin@edugestao.ucsal.br |
| Coordenador | coordenacao@edugestao.ucsal.br |
| Professor | marcos.lima@edugestao.ucsal.br |
| Professor | fernanda.souza@edugestao.ucsal.br |
| Profissional de apoio | apoio@edugestao.ucsal.br |
| Responsável | roberto.alves@email.com |
| Aluno | lucas.alves@aluno.ucsal.br |

## Testes

```bash
cd backend
mvn test
```

Os testes unitários cobrem as regras de negócio:

| Regra | Classe de teste |
|---|---|
| RN-01: matrícula ativa única por turma e ano letivo | `MatriculaServiceTest` |
| RN-02: frequência por horas, mínimo de 75% e alerta abaixo de 80% | `FrequenciaServiceTest` |
| RN-03: média ponderada ou aritmética conforme o regimento | `AvaliacaoServiceTest` |
| RN-04: bloqueio de alteração em etapa fechada sem autorização | `AutorizacaoAlteracaoServiceTest` |
| RF-11 / RN-09: faixas de risco, regras de referência e sugestões pedagógicas | `PredicaoRiscoServiceTest` |

O workflow `.github/workflows/ci.yml` compila o back-end e o front-end e roda os testes a cada push e pull request.

## Documentação

- Diagramas C4 em [`docs/c4`](docs/c4), em Mermaid
- Requisitos, regras de negócio, BPMN, diagramas UML e definição da IA no documento da etapa 1 do projeto

## Equipe

Cauan Guimarães Silva, Guilherme Ferreira Sampaio, João Vitor Senhorinho Guilherme, Luan Gabriel Ornelas Sacramento Oliveira e Walter Queiroz Rocha.

Orientação: Prof. Elton Figueiredo da Silva.
