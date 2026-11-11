import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { useAuth } from '../../auth/AuthContext';
import { Campo } from '../../components/Campo';
import { Tabela } from '../../components/Tabela';
import { Alerta } from '../../components/Alerta';

export function PlanoPedagogicoPage() {
  const { usuario } = useAuth();
  const ehCoordenacao = usuario.perfil === 'COORDENADOR' || usuario.perfil === 'ADMINISTRADOR';

  const [planos, setPlanos] = useState([]);
  const [alunos, setAlunos] = useState([]);
  const [profissionais, setProfissionais] = useState([]);
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  const [editPlanoId, setEditPlanoId] = useState(null);
  const [alunoId, setAlunoId] = useState('');
  const [metas, setMetas] = useState('');
  const [adaptacoes, setAdaptacoes] = useState('');
  const [estrategias, setEstrategias] = useState('');
  const [dataRevisao, setDataRevisao] = useState('2026-09-30');
  const [profissionaisSelecionados, setProfissionaisSelecionados] = useState([]);

  async function carregar() {
    try {
      if (ehCoordenacao) {
        const [pl, al, pr] = await Promise.all([
          apiFetch('/planos'),
          apiFetch('/usuarios?perfil=ALUNO'),
          apiFetch('/usuarios?perfil=PROFISSIONAL_APOIO')
        ]);
        setPlanos(pl);
        setAlunos(al);
        setProfissionais(pr);
        if (al.length > 0 && !alunoId) setAlunoId(String(al[0].id));
      } else {
        const pl = await apiFetch('/apoio/planos');
        setPlanos(pl);
      }
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  useEffect(() => {
    carregar();
  }, [ehCoordenacao]);

  function iniciarEdicaoPlano(p) {
    setEditPlanoId(p.id);
    setAlunoId(String(p.alunoId));
    setMetas(p.metas);
    setAdaptacoes(p.adaptacoes);
    setEstrategias(p.estrategias);
    setDataRevisao(p.dataRevisao);
    setProfissionaisSelecionados(p.profissionaisIds || []);
  }

  function cancelarEdicaoPlano() {
    setEditPlanoId(null);
    setMetas('');
    setAdaptacoes('');
    setEstrategias('');
    setProfissionaisSelecionados([]);
  }

  async function salvarPlano(e) {
    e.preventDefault();
    try {
      const payload = {
        alunoId: Number(alunoId),
        metas,
        adaptacoes,
        estrategias,
        dataRevisao,
        profissionaisIds: profissionaisSelecionados
      };
      if (editPlanoId) {
        await apiFetch(`/planos/${editPlanoId}`, {
          method: 'PUT',
          body: JSON.stringify(payload)
        });
        await apiFetch(`/planos/${editPlanoId}/profissionais`, {
          method: 'PUT',
          body: JSON.stringify({ profissionaisIds: profissionaisSelecionados })
        });
        setMsg({ tipo: 'sucesso', texto: 'Plano pedagógico revisado e profissionais atualizados (UC08, UC13).' });
        cancelarEdicaoPlano();
      } else {
        await apiFetch('/planos', {
          method: 'POST',
          body: JSON.stringify(payload)
        });
        setMsg({ tipo: 'sucesso', texto: 'Plano pedagógico inclusivo registrado sem dados clínicos (RF-10, RN-08).' });
        setMetas('');
        setAdaptacoes('');
        setEstrategias('');
      }
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  function alternarProfissional(id) {
    setProfissionaisSelecionados((atual) =>
      atual.includes(id) ? atual.filter((x) => x !== id) : [...atual, id]
    );
  }

  return (
    <div>
      <h1 style={{ marginTop: 0 }}>Acompanhamento Pedagógico Inclusivo — PDI (UC08, UC13 — RF-10, RN-08)</h1>
      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      <div className="alerta-box alerta-info">
        <strong>Conformidade LGPD e RN-08:</strong> Este módulo armazena exclusivamente metas, adaptações e estratégias pedagógicas. Nenhum diagnóstico clínico ou médico deve ser registrado.
      </div>

      {ehCoordenacao && (
        <section className="card">
          <h2 style={{ marginTop: 0 }}>
            {editPlanoId ? `Revisar / Editar Plano Pedagógico #${editPlanoId}` : 'Elaborar Novo Plano Pedagógico Individualizado'}
          </h2>
          <form onSubmit={salvarPlano} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            <div className="form-grid">
              <Campo id="pl-aluno" label="Estudante" as="select" value={alunoId} onChange={(e) => setAlunoId(e.target.value)} disabled={Boolean(editPlanoId)} required>
                {alunos.map((a) => <option key={a.id} value={a.id}>{a.nome} ({a.matricula})</option>)}
              </Campo>
              <Campo id="pl-rev" label="Data da Próxima Revisão" type="date" value={dataRevisao} onChange={(e) => setDataRevisao(e.target.value)} required />
            </div>

            <Campo id="pl-metas" label="Metas Pedagógicas" as="textarea" rows="2" value={metas} onChange={(e) => setMetas(e.target.value)} required />
            <Campo id="pl-adap" label="Adaptações Curriculares / Avaliativas" as="textarea" rows="2" value={adaptacoes} onChange={(e) => setAdaptacoes(e.target.value)} required />
            <Campo id="pl-estr" label="Estratégias de Acompanhamento" as="textarea" rows="2" value={estrategias} onChange={(e) => setEstrategias(e.target.value)} required />

            <fieldset style={{ border: '1px solid var(--cor-borda)', borderRadius: 6, padding: 12 }}>
              <legend style={{ fontWeight: 600 }}>Profissionais de Apoio Autorizados (UC08)</legend>
              <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
                {profissionais.map((p) => (
                  <label key={p.id} style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
                    <input
                      type="checkbox"
                      checked={profissionaisSelecionados.includes(p.id)}
                      onChange={() => alternarProfissional(p.id)}
                    />
                    {p.nome} ({p.especialidade})
                  </label>
                ))}
              </div>
            </fieldset>

            <div style={{ display: 'flex', gap: 10 }}>
              <button type="submit" className="btn btn-primario">
                {editPlanoId ? 'Atualizar Plano e Autorizações' : 'Salvar Plano Pedagógico'}
              </button>
              {editPlanoId && (
                <button type="button" className="btn btn-secundario" onClick={cancelarEdicaoPlano}>
                  Cancelar Edição
                </button>
              )}
            </div>
          </form>
        </section>
      )}

      <section className="card">
        <h2 style={{ marginTop: 0 }}>
          {ehCoordenacao ? 'Planos Pedagógicos Registrados' : 'Planos Autorizados para Meu Acompanhamento (UC08)'}
        </h2>
        <Tabela
          colunas={ehCoordenacao
            ? ['Estudante', 'Metas Pedagógicas', 'Adaptações', 'Estratégias', 'Revisão', 'Profissionais Autorizados', 'Ação']
            : ['Estudante', 'Metas Pedagógicas', 'Adaptações', 'Estratégias', 'Revisão', 'Profissionais Autorizados']}
          dados={planos}
          renderLinha={(p) => (
            <tr key={p.id}>
              <td>
                <strong>{p.alunoNome}</strong>
                <div style={{ fontSize: '0.8rem', color: 'var(--cor-texto-secundario)' }}>
                  Matrícula: {p.alunoMatriculaCodigo}
                  {p.alertaOrigemId && ` | Origem: Alerta IA #${p.alertaOrigemId}`}
                </div>
              </td>
              <td>{p.metas}</td>
              <td>{p.adaptacoes}</td>
              <td>{p.estrategias}</td>
              <td>{p.dataRevisao}</td>
              <td>{p.profissionaisNomes?.length > 0 ? p.profissionaisNomes.join(', ') : 'Apenas Coordenação'}</td>
              {ehCoordenacao && (
                <td>
                  <button type="button" className="btn btn-secundario" onClick={() => iniciarEdicaoPlano(p)}>
                    Revisar / Autorizar
                  </button>
                </td>
              )}
            </tr>
          )}
        />
      </section>
    </div>
  );
}
