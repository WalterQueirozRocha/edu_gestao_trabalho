import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { Campo } from '../../components/Campo';
import { Tabela } from '../../components/Tabela';
import { Alerta } from '../../components/Alerta';
import { Modal } from '../../components/Modal';

export function PainelAlertasIaPage() {
  const [alertas, setAlertas] = useState([]);
  const [alertaParaConverter, setAlertaParaConverter] = useState(null);
  const [metas, setMetas] = useState('');
  const [adaptacoes, setAdaptacoes] = useState('');
  const [estrategias, setEstrategias] = useState('');
  const [dataRevisao, setDataRevisao] = useState('2026-09-30');
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
    try {
      const modelo = await apiFetch('/ia/treinar', { method: 'POST' });
      setMsg({
        tipo: 'sucesso',
        texto: `Modelo Tribuo (${modelo.versao}) treinado com sucesso! Acurácia hold-out: ${(modelo.acuracia * 100).toFixed(1)}%`
      });
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function executarPredicao() {
    try {
      const gerados = await apiFetch('/ia/executar', { method: 'POST' });
      setMsg({
        tipo: 'sucesso',
        texto: `Análise preditiva concluída (UC15). ${gerados.length} alerta(s) de risco médio/alto atualizados.`
      });
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
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

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 12, marginBottom: 16 }}>
        <h1 style={{ margin: 0 }}>Predição de Risco e Assistente Pedagógico IA (UC14, UC15, UC18 — RF-11, RN-09)</h1>
        <div style={{ display: 'flex', gap: 10 }}>
          <button type="button" className="btn btn-secundario" onClick={treinarModelo}>
            Retreinar Modelo Tribuo (CSV Histórico)
          </button>
          <button type="button" className="btn btn-primario" onClick={executarPredicao}>
            Executar Análise de Risco Agora
          </button>
        </div>
      </div>

      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      <div className="alerta-box alerta-info">
        <strong>RN-09 (Apoio à Decisão):</strong> Os alertas e sugestões gerados pelo modelo preditivo não alteram notas, médias ou situação acadêmica automaticamente e dependem da revisão da coordenação.
      </div>

      <section className="card">
        <h2 style={{ marginTop: 0 }}>Painel de Alertas Precoces de Retenção e Evasão</h2>
        <Tabela
          colunas={['Estudante', 'Nível de Risco', 'Probabilidade', 'Fatores Identificados (UC14)', 'Sugestões do Assistente Pedagógico (UC18)', 'Status', 'Decisão da Coordenação']}
          dados={alertas}
          renderLinha={(a) => (
            <tr key={a.id}>
              <td>
                <strong>{a.alunoNome}</strong>
                <div style={{ fontSize: '0.8rem', color: 'var(--cor-texto-secundario)' }}>
                  Matrícula: {a.alunoMatriculaCodigo} | Modelo: {a.modeloVersao}
                </div>
              </td>
              <td>
                <span className={`badge badge-${a.nivel.toLowerCase()}`}>{a.nivel}</span>
              </td>
              <td><strong>{(a.probabilidade * 100).toFixed(1)}%</strong></td>
              <td>{a.fatores}</td>
              <td>{a.sugestoes}</td>
              <td><span className="badge badge-publicada">{a.status}</span></td>
              <td>
                {a.status === 'NOVO' ? (
                  <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                    <button
                      type="button"
                      className="btn btn-sucesso"
                      onClick={() => {
                        setAlertaParaConverter(a);
                        setMetas('Elevar frequência para >= 85% e recuperar média mínima nas disciplinas em atenção.');
                        setAdaptacoes('Avaliações divididas em etapas menores e acompanhamento quinzenal.');
                        setEstrategias(a.sugestoes);
                      }}
                    >
                      Converter em Plano
                    </button>
                    <button type="button" className="btn btn-secundario" onClick={() => descartar(a.id)}>
                      Descartar
                    </button>
                  </div>
                ) : (
                  <span>Processado ({a.status})</span>
                )}
              </td>
            </tr>
          )}
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
