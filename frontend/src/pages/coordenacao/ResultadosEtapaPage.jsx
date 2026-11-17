import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { Campo } from '../../components/Campo';
import { Tabela } from '../../components/Tabela';
import { Alerta } from '../../components/Alerta';
import { useAuth } from '../../auth/AuthContext';
import { formatarData, formatarDataHora } from '../../utils/formatters';

export function ResultadosEtapaPage() {
  const { usuario } = useAuth();
  const ehCoordenador = usuario.perfil === 'COORDENADOR';
  const [turmas, setTurmas] = useState([]);
  const [etapas, setEtapas] = useState([]);
  const [turmaDisciplinas, setTurmaDisciplinas] = useState([]);
  const [autorizacoes, setAutorizacoes] = useState([]);
  const [turmaId, setTurmaId] = useState('');
  const [etapaId, setEtapaId] = useState('');
  const [resultados, setResultados] = useState([]);
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  const [novaAuth, setNovaAuth] = useState({ etapaId: '', turmaDisciplinaId: '', motivo: '' });

  async function carregarDadosBase() {
    try {
      const [t, e, auths] = await Promise.all([
        apiFetch('/turmas'),
        apiFetch('/etapas'),
        apiFetch('/autorizacoes-alteracao')
      ]);
      setTurmas(t);
      setEtapas(e);
      setAutorizacoes(auths);
      if (t.length > 0 && !turmaId) setTurmaId(String(t[0].id));
      if (e.length > 0 && !etapaId) {
        setEtapaId(String(e[0].id));
        setNovaAuth((p) => ({ ...p, etapaId: e[0].id }));
      }
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  useEffect(() => {
    carregarDadosBase();
  }, []);

  useEffect(() => {
    if (turmaId && etapaId) {
      apiFetch(`/turmas/${turmaId}/resultados?etapaId=${etapaId}`)
        .then(setResultados)
        .catch((err) => setMsg({ tipo: 'erro', texto: err.message }));

      apiFetch(`/turmas/${turmaId}/disciplinas`)
        .then((tds) => {
          setTurmaDisciplinas(tds);
          if (tds.length > 0) setNovaAuth((p) => ({ ...p, turmaDisciplinaId: tds[0].id }));
        })
        .catch(() => {});
    }
  }, [turmaId, etapaId]);

  async function fecharEtapa(id) {
    try {
      await apiFetch(`/etapas/${id}/fechar`, { method: 'POST' });
      setMsg({ tipo: 'sucesso', texto: 'Etapa fechada com sucesso (RN-04: bloqueio ativado).' });
      carregarDadosBase();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function publicarEtapa(id) {
    try {
      await apiFetch(`/etapas/${id}/publicar`, { method: 'POST' });
      setMsg({ tipo: 'sucesso', texto: 'Etapa publicada! Boletins liberados para estudantes e responsáveis (RN-05).' });
      carregarDadosBase();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function emitirAutorizacao(e) {
    e.preventDefault();
    try {
      await apiFetch('/autorizacoes-alteracao', {
        method: 'POST',
        body: JSON.stringify({
          etapaId: Number(novaAuth.etapaId),
          turmaDisciplinaId: Number(novaAuth.turmaDisciplinaId),
          motivo: novaAuth.motivo
        })
      });
      setMsg({ tipo: 'sucesso', texto: 'Autorização temporária de alteração emitida (RN-04).' });
      setNovaAuth({ ...novaAuth, motivo: '' });
      carregarDadosBase();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  return (
    <div>
      <h1 style={{ marginTop: 0 }}>Resultados da Turma, Fechamento e Autorizações (UC09, UC10 — RN-04, RN-05)</h1>
      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      <section className="card">
        <h2 style={{ marginTop: 0 }}>Controle de Etapas Letivas (Fechar e Publicar Boletins)</h2>
        <Tabela
          colunas={['Etapa', 'Ano Letivo', 'Período', 'Status Atual', 'Ações da Coordenação']}
          dados={etapas}
          renderLinha={(e) => (
            <tr key={e.id}>
              <td><strong>{e.nome}</strong></td>
              <td>{e.anoLetivoAno}</td>
              <td>{formatarData(e.dataInicio)} a {formatarData(e.dataFim)}</td>
              <td><span className={`badge badge-${e.status.toLowerCase()}`}>{e.status}</span></td>
              <td>
                <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                  {e.status === 'ABERTA' && (
                    <button type="button" className="btn btn-primario btn-sm" onClick={() => fecharEtapa(e.id)}>
                      Fechar Etapa (RN-04)
                    </button>
                  )}
                  {e.status === 'FECHADA' && (
                    <button type="button" className="btn btn-sucesso btn-sm" onClick={() => publicarEtapa(e.id)}>
                      Publicar Boletins (RN-05)
                    </button>
                  )}
                  {e.status === 'PUBLICADA' && (
                    <span style={{ fontSize: '0.84rem', color: 'var(--cor-texto-secundario)' }}>
                      Boletins publicados
                    </span>
                  )}
                </div>
              </td>
            </tr>
          )}
        />
      </section>

      <section className="card">
        <h2 style={{ marginTop: 0 }}>Resultados Consolidados por Turma e Etapa (UC10)</h2>
        <div className="form-grid">
          <Campo id="res-turma" label="Turma" as="select" value={turmaId} onChange={(e) => setTurmaId(e.target.value)}>
            {turmas.map((t) => <option key={t.id} value={t.id}>{t.nome} ({t.serie})</option>)}
          </Campo>
          <Campo id="res-etapa" label="Etapa" as="select" value={etapaId} onChange={(e) => setEtapaId(e.target.value)}>
            {etapas.map((et) => <option key={et.id} value={et.id}>{et.nome} ({et.status})</option>)}
          </Campo>
        </div>

        <Tabela
          colunas={['Estudante', 'Matrícula', 'Média Geral', 'Frequência Geral', 'Médias por Disciplina', 'Situação']}
          dados={resultados}
          renderLinha={(r) => (
            <tr key={r.matriculaId}>
              <td><strong>{r.alunoNome}</strong></td>
              <td>{r.alunoMatriculaCodigo}</td>
              <td>{r.mediaGeral}</td>
              <td>{r.percentualFrequenciaGeral}%</td>
              <td>
                {r.disciplinas.map((d) => `${d.disciplinaNome}: ${d.mediaPonderada} (${d.percentualFrequencia}%)`).join(' | ')}
              </td>
              <td>
                <span className={`badge badge-${r.situacaoCalculada.toLowerCase()}`}>
                  {r.situacaoCalculada}
                </span>
              </td>
            </tr>
          )}
        />
      </section>

      <section className="card">
        <h2 style={{ marginTop: 0 }}>Autorizar Alteração Excepcional Após Fechamento (RN-04)</h2>
        {ehCoordenador && (
          <form onSubmit={emitirAutorizacao} className="form-grid" style={{ alignItems: 'end' }}>
            <Campo id="au-etapa" label="Etapa Fechada/Publicada" as="select" value={novaAuth.etapaId} onChange={(e) => setNovaAuth({ ...novaAuth, etapaId: e.target.value })} required>
              {etapas.map((et) => <option key={et.id} value={et.id}>{et.nome} ({et.status})</option>)}
            </Campo>
            <Campo id="au-td" label="Turma / Disciplina" as="select" value={novaAuth.turmaDisciplinaId} onChange={(e) => setNovaAuth({ ...novaAuth, turmaDisciplinaId: e.target.value })} required>
              {turmaDisciplinas.map((td) => <option key={td.id} value={td.id}>{td.turmaNome} — {td.disciplinaNome}</option>)}
            </Campo>
            <Campo id="au-motivo" label="Justificativa / Motivo" value={novaAuth.motivo} onChange={(e) => setNovaAuth({ ...novaAuth, motivo: e.target.value })} required />
            <div>
              <button type="submit" className="btn btn-primario">Conceder Autorização</button>
            </div>
          </form>
        )}

        <Tabela
          colunas={['Etapa', 'Turma / Disciplina', 'Coordenador', 'Motivo', 'Validade', 'Utilizada?']}
          dados={autorizacoes}
          renderLinha={(a) => (
            <tr key={a.id}>
              <td>{a.etapaNome}</td>
              <td>{a.turmaNome} — {a.disciplinaNome}</td>
              <td>{a.coordenadorNome}</td>
              <td>{a.motivo}</td>
              <td>{formatarDataHora(a.validaAte)}</td>
              <td>
                <span className={a.utilizada ? 'badge badge-neutro' : 'badge badge-ativa'}>
                  {a.utilizada ? 'Consumida' : 'Disponível'}
                </span>
              </td>
            </tr>
          )}
        />
      </section>
    </div>
  );
}
