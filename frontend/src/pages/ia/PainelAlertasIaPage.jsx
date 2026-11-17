import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { Campo } from '../../components/Campo';
import { Tabela } from '../../components/Tabela';
import { Alerta } from '../../components/Alerta';
import { Modal } from '../../components/Modal';
import { useAuth } from '../../auth/AuthContext';
import { formatarDataHora } from '../../utils/formatters';

export function PainelAlertasIaPage() {
  const { usuario } = useAuth();
  const ehCoordenador = usuario.perfil === 'COORDENADOR';
  const [alertas, setAlertas] = useState([]);
  const [filtroNivel, setFiltroNivel] = useState('');
  const [filtroStatus, setFiltroStatus] = useState('');
  const [alertaParaConverter, setAlertaParaConverter] = useState(null);
  const [metas, setMetas] = useState('');
  const [adaptacoes, setAdaptacoes] = useState('');
  const [estrategias, setEstrategias] = useState('');
  const [dataRevisao, setDataRevisao] = useState('2026-09-30');
  const [processando, setProcessando] = useState(false);
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  async function carregar() {
    try {
      const lista = await apiFetch('/alertas');
      setAlertas(lista);
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  useEffect(() => {
    carregar();
  }, []);

  async function treinarModelo() {
    setProcessando(true);
    try {
      const modelo = await apiFetch('/ia/treinar', { method: 'POST' });
      setMsg({
        tipo: 'sucesso',
        texto: `Modelo Tribuo (${modelo.versao}) treinado com sucesso! Acurácia hold-out: ${(modelo.acuracia * 100).toFixed(1)}%`
      });
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    } finally {
      setProcessando(false);
    }
  }

  async function executarPredicao() {
    setProcessando(true);
    try {
      const gerados = await apiFetch('/ia/executar', { method: 'POST' });
      setMsg({
        tipo: 'sucesso',
        texto: `Análise preditiva concluída (UC15). ${gerados.length} alerta(s) de risco médio/alto atualizados.`
      });
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    } finally {
      setProcessando(false);
    }
  }

  async function descartar(id) {
    try {
      await apiFetch(`/alertas/${id}/descartar`, { method: 'POST' });
      setMsg({ tipo: 'info', texto: 'Alerta descartado pela coordenação.' });
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function confirmarConversao(e) {
    e.preventDefault();
    if (!alertaParaConverter) return;
    try {
      await apiFetch(`/alertas/${alertaParaConverter.id}/converter`, {
        method: 'POST',
        body: JSON.stringify({
          metas,
          adaptacoes,
          estrategias,
          dataRevisao
        })
      });
      setMsg({
        tipo: 'sucesso',
        texto: `Alerta de ${alertaParaConverter.alunoNome} convertido em Plano Pedagógico (RF-11 → RF-10)!`
      });
      setAlertaParaConverter(null);
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  const filtrados = alertas.filter((a) => {
    if (filtroNivel && a.nivel !== filtroNivel) return false;
    if (filtroStatus && a.status !== filtroStatus) return false;
    return true;
  });

  const qtdAlto = alertas.filter((a) => a.nivel === 'ALTO' && a.status === 'NOVO').length;
  const qtdMedio = alertas.filter((a) => a.nivel === 'MEDIO' && a.status === 'NOVO').length;
  const qtdConvertidos = alertas.filter((a) => a.status === 'CONVERTIDO').length;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Predição de Risco e Assistente Pedagógico IA (UC14, UC15, UC18)</h1>
          <p className="page-subtitle">
            Classificação preditiva de retenção e evasão (Oracle Tribuo — RF-11) com sugestões pedagógicas (RN-09).
          </p>
        </div>
        <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
          <button type="button" className="btn btn-secundario" disabled={processando} onClick={treinarModelo}>
            Retreinar Modelo Tribuo
          </button>
          <button type="button" className="btn btn-primario" disabled={processando} onClick={executarPredicao}>
            {processando ? 'Processando IA...' : 'Executar Análise de Risco Agora'}
          </button>
        </div>
      </div>

      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      <section className="stats-grid" aria-label="Resumo dos alertas preditivos">
        <div className="stat-card stat-perigo">
          <div className="stat-label">Risco Alto Pendente (&gt; 60%)</div>
          <div className="stat-value">{qtdAlto}</div>
          <div className="stat-meta">Exige busca ativa e plano prioritário</div>
        </div>
        <div className="stat-card stat-alerta">
          <div className="stat-label">Risco Médio Pendente (30%–60%)</div>
          <div className="stat-value">{qtdMedio}</div>
          <div className="stat-meta">Acompanhamento preventivo recomendado</div>
        </div>
        <div className="stat-card stat-sucesso">
          <div className="stat-label">Convertidos em PDI (RF-10)</div>
          <div className="stat-value">{qtdConvertidos}</div>
          <div className="stat-meta">Estudantes já com Plano Pedagógico ativo</div>
        </div>
      </section>

      <div className="alerta-box alerta-info">
        <strong>RN-09 (Apoio à Decisão):</strong> Os alertas e sugestões gerados pelo modelo preditivo não alteram notas, médias ou situação acadêmica automaticamente e dependem da revisão da coordenação.
      </div>

      <section className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 12, marginBottom: 16 }}>
          <h2 style={{ margin: 0 }}>Painel de Alertas Precoces de Retenção e Evasão</h2>
          <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
            <select
              className="campo-select"
              aria-label="Filtrar por nível de risco"
              value={filtroNivel}
              onChange={(e) => setFiltroNivel(e.target.value)}
            >
              <option value="">Todos os níveis</option>
              <option value="ALTO">Risco ALTO</option>
              <option value="MEDIO">Risco MÉDIO</option>
            </select>
            <select
              className="campo-select"
              aria-label="Filtrar por status do alerta"
              value={filtroStatus}
              onChange={(e) => setFiltroStatus(e.target.value)}
            >
              <option value="">Todos os status</option>
              <option value="NOVO">NOVO (Pendente)</option>
              <option value="CONVERTIDO">CONVERTIDO</option>
              <option value="DESCARTADO">DESCARTADO</option>
              <option value="ENCERRADO">ENCERRADO</option>
            </select>
          </div>
        </div>

        <Tabela
          colunas={['Estudante', 'Nível / Probabilidade', 'Fatores Identificados (UC14)', 'Sugestões do Assistente Pedagógico (UC18)', 'Status', 'Decisão da Coordenação']}
          dados={filtrados}
          renderLinha={(a) => {
            const pct = Number((a.probabilidade * 100).toFixed(1));
            const corBarra = a.nivel === 'ALTO' ? 'var(--cor-perigo)' : '#d97706';
            const listaFatores = String(a.fatores || '').split(';').map((s) => s.trim()).filter(Boolean);
            const listaSugestoes = String(a.sugestoes || '').split(';').map((s) => s.trim()).filter(Boolean);
            return (
              <tr key={a.id}>
                <td>
                  <strong>{a.alunoNome}</strong>
                  <div style={{ fontSize: '0.8rem', color: 'var(--cor-texto-secundario)' }}>
                    Matrícula: {a.alunoMatriculaCodigo}
                  </div>
                  <div style={{ fontSize: '0.76rem', color: 'var(--cor-texto-secundario)' }}>
                    Modelo: {a.modeloVersao} • {formatarDataHora(a.dataGeracao)}
                  </div>
                </td>
                <td style={{ minWidth: 150 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span className={`badge badge-${a.nivel.toLowerCase()}`}>{a.nivel}</span>
                    <strong>{pct}%</strong>
                  </div>
                  <div className="progress-track" aria-hidden="true">
                    <div className="progress-fill" style={{ width: `${Math.min(100, pct)}%`, backgroundColor: corBarra }} />
                  </div>
                </td>
                <td>
                  <ul style={{ margin: 0, paddingLeft: 18, fontSize: '0.86rem' }}>
                    {listaFatores.map((f, idx) => (
                      <li key={idx}>{f}</li>
                    ))}
                  </ul>
                </td>
                <td>
                  <ul style={{ margin: 0, paddingLeft: 18, fontSize: '0.86rem' }}>
                    {listaSugestoes.map((s, idx) => (
                      <li key={idx}>{s}</li>
                    ))}
                  </ul>
                </td>
                <td>
                  <span className={`badge badge-${a.status.toLowerCase()}`}>{a.status}</span>
                </td>
                <td>
                  {a.status === 'NOVO' ? (
                    <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                      {ehCoordenador && (
                        <button
                          type="button"
                          className="btn btn-sucesso btn-sm"
                          onClick={() => {
                            setAlertaParaConverter(a);
                            setMetas('Elevar frequência para >= 85% e recuperar média mínima nas disciplinas em atenção.');
                            setAdaptacoes('Avaliações divididas em etapas menores e acompanhamento quinzenal.');
                            setEstrategias(a.sugestoes);
                          }}
                        >
                          Converter em PDI
                        </button>
                      )}
                      <button type="button" className="btn btn-secundario btn-sm" onClick={() => descartar(a.id)}>
                        Descartar
                      </button>
                    </div>
                  ) : (
                    <span style={{ fontSize: '0.84rem', color: 'var(--cor-texto-secundario)' }}>
                      {a.status === 'ENCERRADO' ? 'Encerrado (risco normalizado)' : `Processado (${a.status})`}
                    </span>
                  )}
                </td>
              </tr>
            );
          }}
        />
      </section>

      <Modal
        aberto={Boolean(alertaParaConverter)}
        titulo={`Converter Alerta em Plano Pedagógico — ${alertaParaConverter?.alunoNome || ''}`}
        onFechar={() => setAlertaParaConverter(null)}
      >
        <form onSubmit={confirmarConversao} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <Campo id="cv-metas" label="Metas Pedagógicas (Editável)" as="textarea" rows="2" value={metas} onChange={(e) => setMetas(e.target.value)} required />
          <Campo id="cv-adap" label="Adaptações Pedagógicas" as="textarea" rows="2" value={adaptacoes} onChange={(e) => setAdaptacoes(e.target.value)} required />
          <Campo id="cv-estr" label="Estratégias Sugeridas pelo Assistente IA" as="textarea" rows="3" value={estrategias} onChange={(e) => setEstrategias(e.target.value)} required />
          <Campo id="cv-rev" label="Data de Revisão" type="date" value={dataRevisao} onChange={(e) => setDataRevisao(e.target.value)} required />
          <button type="submit" className="btn btn-sucesso">Confirmar e Criar Plano Pedagógico (UC13)</button>
        </form>
      </Modal>
    </div>
  );
}
