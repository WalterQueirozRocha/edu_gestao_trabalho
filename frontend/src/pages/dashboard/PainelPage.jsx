import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import { apiFetch } from '../../api/client';
import { ROTULO_PERFIL, formatarDataHora } from '../../utils/formatters';

const CARDS_POR_PERFIL = {
  ADMINISTRADOR: [
    { badge: 'UC02 / UC03', titulo: 'Usuários, Perfis e Vínculos', desc: 'Gerencie contas RBAC, ativação e vínculos responsável–estudante (RN-06).', link: '/usuarios' },
    { badge: 'UC02 / RN-03', titulo: 'Estrutura Acadêmica e Regimento', desc: 'Configure anos letivos, fórmula da média (ponderada ou aritmética), etapas, turmas e disciplinas.', link: '/cadastros-academicos' },
    { badge: 'UC04 / RN-01', titulo: 'Matrículas e Histórico Escolar', desc: 'Realize matrículas com unicidade ativa por turma e preservação integral do histórico (RN-07).', link: '/matriculas' },
    { badge: 'UC14 / RF-11', titulo: 'Painel de Alertas de Risco (IA)', desc: 'Acompanhe os indicadores preditivos de retenção e evasão gerados pelo modelo Tribuo.', link: '/alertas-ia' }
  ],
  COORDENADOR: [
    { badge: 'UC09 / UC10', titulo: 'Resultados, Fechamento e Autorizações', desc: 'Consolide médias e frequência, feche/publique etapas e conceda autorizações excepcionais (RN-04).', link: '/resultados-etapa' },
    { badge: 'UC14 / UC15 / UC18', titulo: 'Predição de Risco e Assistente IA', desc: 'Analise probabilidades de retenção/evasão, fatores contribuintes e converta alertas em PDI.', link: '/alertas-ia' },
    { badge: 'UC13 / RN-08', titulo: 'Plano Pedagógico Inclusivo (PDI)', desc: 'Elabore metas, adaptações e estratégias pedagógicas e autorize profissionais de apoio.', link: '/planos-pedagogicos' },
    { badge: 'UC12 / RF-09', titulo: 'Tratar Ocorrências e Denúncias', desc: 'Realize a triagem restrita de denúncias e ocorrências com encaminhamento para PDI.', link: '/tratamento-ocorrencias' },
    { badge: 'UC11 / RF-08', titulo: 'Comunicados Segmentados', desc: 'Publique avisos institucionais por perfil de público e turma destinatária.', link: '/comunicados' }
  ],
  PROFESSOR: [
    { badge: 'UC05 / RN-02', titulo: 'Diário de Classe e Chamada', desc: 'Registre aulas e frequência em horas com alerta imediato abaixo de 80% e retenção abaixo de 75%.', link: '/diario' },
    { badge: 'UC06 / RN-03', titulo: 'Avaliações e Lançamento de Notas', desc: 'Cadastre avaliações com pesos e lance notas respeitando o bloqueio de etapa fechada (RN-04).', link: '/avaliacoes' },
    { badge: 'UC07 / RF-09', titulo: 'Registrar Ocorrência ou Denúncia', desc: 'Registre ocorrências pedagógicas/disciplinares ou encaminhe relatos à coordenação.', link: '/ocorrencias-denuncias' }
  ],
  PROFISSIONAL_APOIO: [
    { badge: 'UC08 / RN-08', titulo: 'Planos Pedagógicos Autorizados (PDI)', desc: 'Consulte metas, adaptações e estratégias dos estudantes sob seu acompanhamento.', link: '/planos-pedagogicos' },
    { badge: 'UC17 / RF-08', titulo: 'Mural de Comunicados', desc: 'Visualize os avisos institucionais publicados pela coordenação escolar.', link: '/comunicados' }
  ],
  ALUNO: [
    { badge: 'UC16 / RN-05', titulo: 'Meu Boletim Escolar', desc: 'Consulte notas, médias, frequência por disciplina e imprima boletins das etapas publicadas.', link: '/boletim' },
    { badge: 'UC17 / RF-08', titulo: 'Comunicados da Escola', desc: 'Acompanhe avisos e orientações da coordenação para sua turma.', link: '/comunicados' },
    { badge: 'UC07 / RNF-03', titulo: 'Canal de Denúncias', desc: 'Registre relatos (inclusive de forma anônima) e acompanhe o andamento pelo protocolo.', link: '/ocorrencias-denuncias' }
  ],
  RESPONSAVEL: [
    { badge: 'UC16 / RN-06', titulo: 'Boletim dos Estudantes Vinculados', desc: 'Acompanhe notas, médias e assiduidade dos estudantes sob sua responsabilidade.', link: '/boletim' },
    { badge: 'UC17 / RF-08', titulo: 'Comunicados e Reuniões', desc: 'Leia avisos e convocações pedagógicas direcionadas às turmas dos seus filhos.', link: '/comunicados' },
    { badge: 'UC07 / RF-09', titulo: 'Canal de Denúncias', desc: 'Registre relatos à coordenação e consulte protocolos de atendimento.', link: '/ocorrencias-denuncias' }
  ]
};

export function PainelPage() {
  const { usuario } = useAuth();
  const cards = CARDS_POR_PERFIL[usuario?.perfil] || [];
  const [indicadores, setIndicadores] = useState([]);
  const [ultimosComunicados, setUltimosComunicados] = useState([]);

  useEffect(() => {
    if (!usuario) return;
    apiFetch('/comunicados')
      .then((lista) => setUltimosComunicados((lista || []).slice(0, 2)))
      .catch(() => {});

    async function carregarResumoPerfil() {
      try {
        if (usuario.perfil === 'ADMINISTRADOR' || usuario.perfil === 'COORDENADOR') {
          const [turmas, etapas, alertas, denuncias] = await Promise.all([
            apiFetch('/turmas').catch(() => []),
            apiFetch('/etapas').catch(() => []),
            apiFetch('/alertas').catch(() => []),
            apiFetch('/denuncias').catch(() => [])
          ]);
          const alertasAtivos = alertas.filter((a) => a.status === 'NOVO');
          const altoRisco = alertasAtivos.filter((a) => a.nivel === 'ALTO').length;
          const pendentes = denuncias.filter((d) => d.status === 'RECEBIDA' || d.status === 'EM_ANALISE').length;
          const publicadas = etapas.filter((e) => e.status === 'PUBLICADA').length;
          setIndicadores([
            { label: 'Turmas Ativas', valor: turmas.length, meta: `${etapas.length} etapas (${publicadas} publicada(s))`, classe: '' },
            { label: 'Alertas IA Pendentes', valor: alertasAtivos.length, meta: `${altoRisco} em nível de risco ALTO`, classe: altoRisco > 0 ? 'stat-perigo' : 'stat-alerta' },
            { label: 'Denúncias em Triagem', valor: pendentes, meta: `${denuncias.length} registrada(s) no canal`, classe: pendentes > 0 ? 'stat-alerta' : 'stat-sucesso' }
          ]);
        } else if (usuario.perfil === 'PROFESSOR') {
          const tds = await apiFetch('/professor/turmas').catch(() => []);
          const cargaTotal = tds.reduce((acc, t) => acc + (t.cargaHoraria || 0), 0);
          setIndicadores([
            { label: 'Alocações (Turma / Disciplina)', valor: tds.length, meta: 'Turmas sob sua regência no ano letivo', classe: '' },
            { label: 'Carga Horária Total', valor: `${cargaTotal}h`, meta: 'Soma das disciplinas vinculadas', classe: 'stat-sucesso' }
          ]);
        } else if (usuario.perfil === 'ALUNO') {
          const boletins = await apiFetch('/boletins/me').catch(() => []);
          const ultimo = boletins[0];
          setIndicadores([
            { label: 'Etapas Publicadas', valor: boletins.length, meta: 'Disponíveis para consulta (RN-05)', classe: '' },
            { label: 'Média Geral Recente', valor: ultimo ? ultimo.mediaGeralEtapa : '—', meta: ultimo ? `${ultimo.etapaNome} (${ultimo.turmaNome})` : 'Aguardando publicação', classe: 'stat-sucesso' },
            { label: 'Frequência Geral', valor: ultimo ? `${ultimo.percentualFrequenciaGeral}%` : '—', meta: 'Mínimo exigido: 75%', classe: ultimo && Number(ultimo.percentualFrequenciaGeral) < 75 ? 'stat-perigo' : '' }
          ]);
        } else if (usuario.perfil === 'RESPONSAVEL') {
          const filhos = await apiFetch('/responsavel/alunos').catch(() => []);
          setIndicadores([
            { label: 'Estudantes Vinculados', valor: filhos.length, meta: filhos.map((f) => f.nome).join(', ') || 'Vínculo familiar ativo (RN-06)', classe: 'stat-sucesso' }
          ]);
        } else if (usuario.perfil === 'PROFISSIONAL_APOIO') {
          const planos = await apiFetch('/apoio/planos').catch(() => []);
          setIndicadores([
            { label: 'Planos Pedagógicos (PDI)', valor: planos.length, meta: 'Estudantes autorizados para acompanhamento (UC08)', classe: 'stat-sucesso' }
          ]);
        }
      } catch {
        // Indicadores sao complementares
      }
    }

    carregarResumoPerfil();
  }, [usuario]);

  return (
    <div>
      <section className="welcome-banner" aria-label="Boas-vindas">
        <div>
          <span
            className="badge"
            style={{
              background: 'rgba(255, 255, 255, 0.16)',
              color: '#ffffff',
              border: '1px solid rgba(255, 255, 255, 0.25)',
              marginBottom: 10
            }}
          >
            Ano Letivo 2026 • {ROTULO_PERFIL[usuario?.perfil] || usuario?.perfil}
          </span>
          <h1>Olá, {usuario?.nome}</h1>
          <p>
            Acompanhe os indicadores escolares e acesse rapidamente os módulos compatíveis com a sua função.
          </p>
        </div>
      </section>

      {indicadores.length > 0 && (
        <section className="stats-grid" aria-label="Indicadores rápidos do perfil">
          {indicadores.map((ind) => (
            <div key={ind.label} className={`stat-card ${ind.classe || ''}`}>
              <div className="stat-label">{ind.label}</div>
              <div className="stat-value">{ind.valor}</div>
              <div className="stat-meta">{ind.meta}</div>
            </div>
          ))}
        </section>
      )}

      <section aria-label="Módulos disponíveis">
        <div className="grid-cards">
          {cards.map((c) => (
            <article
              key={c.link}
              className="card card-interativo"
              style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between', marginBottom: 0 }}
            >
              <div>
                <span className="badge badge-info" style={{ marginBottom: 10 }}>
                  {c.badge}
                </span>
                <h2 style={{ marginTop: 0, marginBottom: 8, fontSize: '1.1rem', color: 'var(--cor-primaria)' }}>
                  {c.titulo}
                </h2>
                <p style={{ fontSize: '0.91rem', color: 'var(--cor-texto-secundario)', margin: 0 }}>
                  {c.desc}
                </p>
              </div>
              <div style={{ marginTop: 18 }}>
                <Link to={c.link} className="btn btn-primario" style={{ width: '100%' }}>
                  Acessar módulo →
                </Link>
              </div>
            </article>
          ))}
        </div>
      </section>

      {ultimosComunicados.length > 0 && (
        <section className="card" style={{ marginTop: 8 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
            <h2 style={{ margin: 0 }}>Últimos Comunicados Institucionais</h2>
            <Link to="/comunicados" className="btn btn-secundario btn-sm">
              Ver mural completo
            </Link>
          </div>
          <div style={{ display: 'grid', gap: 10 }}>
            {ultimosComunicados.map((c) => (
              <div
                key={c.id}
                style={{
                  padding: '12px 14px',
                  borderRadius: 8,
                  background: '#f8fafc',
                  border: '1px solid var(--cor-borda-suave)'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', gap: 8 }}>
                  <strong style={{ color: 'var(--cor-primaria)' }}>{c.titulo}</strong>
                  <span style={{ fontSize: '0.8rem', color: 'var(--cor-texto-secundario)' }}>
                    {formatarDataHora(c.dataPublicacao)} — {c.autorNome}
                  </span>
                </div>
                <p style={{ margin: '6px 0 0', fontSize: '0.9rem', color: 'var(--cor-texto-secundario)' }}>
                  {c.mensagem}
                </p>
              </div>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}
