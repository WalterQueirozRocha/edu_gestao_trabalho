import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { Campo } from '../../components/Campo';
import { Tabela } from '../../components/Tabela';
import { Alerta } from '../../components/Alerta';

export function CadastrosAcademicosPage() {
  const [anos, setAnos] = useState([]);
  const [etapas, setEtapas] = useState([]);
  const [turmas, setTurmas] = useState([]);
  const [disciplinas, setDisciplinas] = useState([]);
  const [professores, setProfessores] = useState([]);
  const [turmaSelecionada, setTurmaSelecionada] = useState('');
  const [turmaDisciplinas, setTurmaDisciplinas] = useState([]);
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  const [editAnoId, setEditAnoId] = useState(null);
  const [editEtapaId, setEditEtapaId] = useState(null);
  const [editTurmaId, setEditTurmaId] = useState(null);
  const [editDiscId, setEditDiscId] = useState(null);

  const [novoAno, setNovoAno] = useState({ ano: 2027, dataInicio: '2027-02-01', dataFim: '2027-12-15', mediaMinima: 6.0, frequenciaMinima: 75.0 });
  const [novaEtapa, setNovaEtapa] = useState({ anoLetivoId: '', nome: '', dataInicio: '', dataFim: '' });
  const [novaTurma, setNovaTurma] = useState({ anoLetivoId: '', nome: '', serie: '', turno: 'MATUTINO' });
  const [novaDisc, setNovaDisc] = useState({ nome: '', codigo: '' });
  const [novoVinculo, setNovoVinculo] = useState({ disciplinaId: '', professorId: '', cargaHoraria: 80 });

  async function carregarTudo() {
    try {
      const [a, e, t, d, profs] = await Promise.all([
        apiFetch('/anos-letivos'),
        apiFetch('/etapas'),
        apiFetch('/turmas'),
        apiFetch('/disciplinas'),
        apiFetch('/usuarios?perfil=PROFESSOR')
      ]);
      setAnos(a);
      setEtapas(e);
      setTurmas(t);
      setDisciplinas(d);
      setProfessores(profs);
      if (a.length > 0 && !novaEtapa.anoLetivoId) {
        setNovaEtapa((prev) => ({ ...prev, anoLetivoId: a[0].id }));
        setNovaTurma((prev) => ({ ...prev, anoLetivoId: a[0].id }));
      }
      if (t.length > 0 && !turmaSelecionada) {
        setTurmaSelecionada(String(t[0].id));
      }
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  useEffect(() => {
    carregarTudo();
  }, []);

  useEffect(() => {
    if (turmaSelecionada) {
      apiFetch(`/turmas/${turmaSelecionada}/disciplinas`)
        .then(setTurmaDisciplinas)
        .catch((err) => setMsg({ tipo: 'erro', texto: err.message }));
    }
  }, [turmaSelecionada]);

  async function salvarAno(e) {
    e.preventDefault();
    try {
      if (editAnoId) {
        await apiFetch(`/anos-letivos/${editAnoId}`, { method: 'PUT', body: JSON.stringify(novoAno) });
        setMsg({ tipo: 'sucesso', texto: 'Ano letivo atualizado.' });
        setEditAnoId(null);
      } else {
        await apiFetch('/anos-letivos', { method: 'POST', body: JSON.stringify(novoAno) });
        setMsg({ tipo: 'sucesso', texto: 'Ano letivo cadastrado.' });
      }
      carregarTudo();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function salvarEtapa(e) {
    e.preventDefault();
    try {
      if (editEtapaId) {
        await apiFetch(`/etapas/${editEtapaId}`, { method: 'PUT', body: JSON.stringify(novaEtapa) });
        setMsg({ tipo: 'sucesso', texto: 'Etapa atualizada.' });
        setEditEtapaId(null);
      } else {
        await apiFetch('/etapas', { method: 'POST', body: JSON.stringify(novaEtapa) });
        setMsg({ tipo: 'sucesso', texto: 'Etapa cadastrada.' });
      }
      setNovaEtapa((p) => ({ ...p, nome: '' }));
      carregarTudo();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function salvarTurma(e) {
    e.preventDefault();
    try {
      if (editTurmaId) {
        await apiFetch(`/turmas/${editTurmaId}`, { method: 'PUT', body: JSON.stringify(novaTurma) });
        setMsg({ tipo: 'sucesso', texto: 'Turma atualizada.' });
        setEditTurmaId(null);
      } else {
        await apiFetch('/turmas', { method: 'POST', body: JSON.stringify(novaTurma) });
        setMsg({ tipo: 'sucesso', texto: 'Turma cadastrada.' });
      }
      setNovaTurma((p) => ({ ...p, nome: '', serie: '' }));
      carregarTudo();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function salvarDisciplina(e) {
    e.preventDefault();
    try {
      if (editDiscId) {
        await apiFetch(`/disciplinas/${editDiscId}`, { method: 'PUT', body: JSON.stringify(novaDisc) });
        setMsg({ tipo: 'sucesso', texto: 'Disciplina atualizada.' });
        setEditDiscId(null);
      } else {
        await apiFetch('/disciplinas', { method: 'POST', body: JSON.stringify(novaDisc) });
        setMsg({ tipo: 'sucesso', texto: 'Disciplina cadastrada.' });
      }
      setNovaDisc({ nome: '', codigo: '' });
      carregarTudo();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function salvarVinculoDisciplina(e) {
    e.preventDefault();
    try {
      await apiFetch(`/turmas/${turmaSelecionada}/disciplinas`, {
        method: 'POST',
        body: JSON.stringify(novoVinculo)
      });
      setMsg({ tipo: 'sucesso', texto: 'Disciplina e professor vinculados à turma.' });
      const atualizadas = await apiFetch(`/turmas/${turmaSelecionada}/disciplinas`);
      setTurmaDisciplinas(atualizadas);
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  return (
    <div>
      <h1 style={{ marginTop: 0 }}>Cadastros Acadêmicos (UC02 — RF-03)</h1>
      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      <div className="grid-cards" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(380px, 1fr))' }}>
        <section className="card">
          <h2 style={{ marginTop: 0 }}>Anos Letivos e Regimento (RN-02 / RN-03)</h2>
          <form onSubmit={salvarAno} className="form-grid">
            <Campo id="al-ano" label="Ano" type="number" value={novoAno.ano} onChange={(e) => setNovoAno({ ...novoAno, ano: Number(e.target.value) })} required />
            <Campo id="al-ini" label="Início" type="date" value={novoAno.dataInicio} onChange={(e) => setNovoAno({ ...novoAno, dataInicio: e.target.value })} required />
            <Campo id="al-fim" label="Fim" type="date" value={novoAno.dataFim} onChange={(e) => setNovoAno({ ...novoAno, dataFim: e.target.value })} required />
            <Campo id="al-med" label="Média Mínima" type="number" step="0.1" value={novoAno.mediaMinima} onChange={(e) => setNovoAno({ ...novoAno, mediaMinima: Number(e.target.value) })} required />
            <Campo id="al-freq" label="Freq. Mínima (%)" type="number" step="0.5" value={novoAno.frequenciaMinima} onChange={(e) => setNovoAno({ ...novoAno, frequenciaMinima: Number(e.target.value) })} required />
            <div style={{ display: 'flex', alignItems: 'end', gap: 6 }}>
              <button type="submit" className="btn btn-primario">{editAnoId ? 'Atualizar Ano' : 'Criar Ano Letivo'}</button>
              {editAnoId && (
                <button type="button" className="btn btn-secundario" onClick={() => setEditAnoId(null)}>Cancelar</button>
              )}
            </div>
          </form>
          <Tabela
            colunas={['Ano', 'Período', 'Média Mín.', 'Freq. Mín.', 'Ação']}
            dados={anos}
            renderLinha={(a) => (
              <tr key={a.id}>
                <td><strong>{a.ano}</strong></td>
                <td>{a.dataInicio} a {a.dataFim}</td>
                <td>{a.mediaMinima}</td>
                <td>{a.frequenciaMinima}%</td>
                <td>
                  <button
                    type="button"
                    className="btn btn-secundario"
                    onClick={() => {
                      setEditAnoId(a.id);
                      setNovoAno({
                        ano: a.ano,
                        dataInicio: a.dataInicio,
                        dataFim: a.dataFim,
                        mediaMinima: a.mediaMinima,
                        frequenciaMinima: a.frequenciaMinima
                      });
                    }}
                  >
                    Editar
                  </button>
                </td>
              </tr>
            )}
          />
        </section>

        <section className="card">
          <h2 style={{ marginTop: 0 }}>Etapas Letivas</h2>
          <form onSubmit={salvarEtapa} className="form-grid">
            <Campo id="et-ano" label="Ano Letivo" as="select" value={novaEtapa.anoLetivoId} onChange={(e) => setNovaEtapa({ ...novaEtapa, anoLetivoId: Number(e.target.value) })} required>
              {anos.map((a) => <option key={a.id} value={a.id}>{a.ano}</option>)}
            </Campo>
            <Campo id="et-nome" label="Nome da Etapa" value={novaEtapa.nome} onChange={(e) => setNovaEtapa({ ...novaEtapa, nome: e.target.value })} required />
            <Campo id="et-ini" label="Início" type="date" value={novaEtapa.dataInicio} onChange={(e) => setNovaEtapa({ ...novaEtapa, dataInicio: e.target.value })} required />
            <Campo id="et-fim" label="Fim" type="date" value={novaEtapa.dataFim} onChange={(e) => setNovaEtapa({ ...novaEtapa, dataFim: e.target.value })} required />
            <div style={{ display: 'flex', alignItems: 'end', gap: 6 }}>
              <button type="submit" className="btn btn-primario">{editEtapaId ? 'Atualizar Etapa' : 'Adicionar Etapa'}</button>
              {editEtapaId && (
                <button type="button" className="btn btn-secundario" onClick={() => setEditEtapaId(null)}>Cancelar</button>
              )}
            </div>
          </form>
          <Tabela
            colunas={['Etapa', 'Ano', 'Período', 'Status', 'Ação']}
            dados={etapas}
            renderLinha={(e) => (
              <tr key={e.id}>
                <td><strong>{e.nome}</strong></td>
                <td>{e.anoLetivoAno}</td>
                <td>{e.dataInicio} a {e.dataFim}</td>
                <td><span className={`badge badge-${e.status.toLowerCase()}`}>{e.status}</span></td>
                <td>
                  <button
                    type="button"
                    className="btn btn-secundario"
                    onClick={() => {
                      setEditEtapaId(e.id);
                      setNovaEtapa({
                        anoLetivoId: e.anoLetivoId,
                        nome: e.nome,
                        dataInicio: e.dataInicio,
                        dataFim: e.dataFim
                      });
                    }}
                  >
                    Editar
                  </button>
                </td>
              </tr>
            )}
          />
        </section>
      </div>

      <div className="grid-cards" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(380px, 1fr))' }}>
        <section className="card">
          <h2 style={{ marginTop: 0 }}>Turmas</h2>
          <form onSubmit={salvarTurma} className="form-grid">
            <Campo id="tu-ano" label="Ano Letivo" as="select" value={novaTurma.anoLetivoId} onChange={(e) => setNovaTurma({ ...novaTurma, anoLetivoId: Number(e.target.value) })} required>
              {anos.map((a) => <option key={a.id} value={a.id}>{a.ano}</option>)}
            </Campo>
            <Campo id="tu-nome" label="Nome (ex.: 9º Ano A)" value={novaTurma.nome} onChange={(e) => setNovaTurma({ ...novaTurma, nome: e.target.value })} required />
            <Campo id="tu-serie" label="Série / Etapa" value={novaTurma.serie} onChange={(e) => setNovaTurma({ ...novaTurma, serie: e.target.value })} required />
            <Campo id="tu-turno" label="Turno" as="select" value={novaTurma.turno} onChange={(e) => setNovaTurma({ ...novaTurma, turno: e.target.value })}>
              <option value="MATUTINO">MATUTINO</option>
              <option value="VESPERTINO">VESPERTINO</option>
              <option value="NOTURNO">NOTURNO</option>
              <option value="INTEGRAL">INTEGRAL</option>
            </Campo>
            <div style={{ display: 'flex', alignItems: 'end', gap: 6 }}>
              <button type="submit" className="btn btn-primario">{editTurmaId ? 'Atualizar Turma' : 'Criar Turma'}</button>
              {editTurmaId && (
                <button type="button" className="btn btn-secundario" onClick={() => setEditTurmaId(null)}>Cancelar</button>
              )}
            </div>
          </form>
          <Tabela
            colunas={['Turma', 'Série', 'Turno', 'Ano', 'Ação']}
            dados={turmas}
            renderLinha={(t) => (
              <tr key={t.id}>
                <td><strong>{t.nome}</strong></td>
                <td>{t.serie}</td>
                <td>{t.turno}</td>
                <td>{t.anoLetivoAno}</td>
                <td>
                  <button
                    type="button"
                    className="btn btn-secundario"
                    onClick={() => {
                      setEditTurmaId(t.id);
                      setNovaTurma({
                        anoLetivoId: t.anoLetivoId,
                        nome: t.nome,
                        serie: t.serie,
                        turno: t.turno
                      });
                    }}
                  >
                    Editar
                  </button>
                </td>
              </tr>
            )}
          />
        </section>

        <section className="card">
          <h2 style={{ marginTop: 0 }}>Disciplinas</h2>
          <form onSubmit={salvarDisciplina} className="form-grid">
            <Campo id="di-nome" label="Nome da Disciplina" value={novaDisc.nome} onChange={(e) => setNovaDisc({ ...novaDisc, nome: e.target.value })} required />
            <Campo id="di-cod" label="Código (ex.: MAT-01)" value={novaDisc.codigo} onChange={(e) => setNovaDisc({ ...novaDisc, codigo: e.target.value })} required />
            <div style={{ display: 'flex', alignItems: 'end', gap: 6 }}>
              <button type="submit" className="btn btn-primario">{editDiscId ? 'Atualizar Disciplina' : 'Criar Disciplina'}</button>
              {editDiscId && (
                <button type="button" className="btn btn-secundario" onClick={() => setEditDiscId(null)}>Cancelar</button>
              )}
            </div>
          </form>
          <Tabela
            colunas={['Código', 'Disciplina', 'Ação']}
            dados={disciplinas}
            renderLinha={(d) => (
              <tr key={d.id}>
                <td><strong>{d.codigo}</strong></td>
                <td>{d.nome}</td>
                <td>
                  <button
                    type="button"
                    className="btn btn-secundario"
                    onClick={() => {
                      setEditDiscId(d.id);
                      setNovaDisc({ nome: d.nome, codigo: d.codigo });
                    }}
                  >
                    Editar
                  </button>
                </td>
              </tr>
            )}
          />
        </section>
      </div>

      <section className="card">
        <h2 style={{ marginTop: 0 }}>Vincular Disciplinas e Professores à Turma</h2>
        <div className="form-grid" style={{ alignItems: 'end' }}>
          <Campo id="vt-turma" label="Selecionar Turma" as="select" value={turmaSelecionada} onChange={(e) => setTurmaSelecionada(e.target.value)}>
            {turmas.map((t) => <option key={t.id} value={t.id}>{t.nome} ({t.anoLetivoAno})</option>)}
          </Campo>
          <Campo id="vt-disc" label="Disciplina" as="select" value={novoVinculo.disciplinaId} onChange={(e) => setNovoVinculo({ ...novoVinculo, disciplinaId: Number(e.target.value) })}>
            <option value="">Selecione...</option>
            {disciplinas.map((d) => <option key={d.id} value={d.id}>{d.nome} ({d.codigo})</option>)}
          </Campo>
          <Campo id="vt-prof" label="Professor Responsável" as="select" value={novoVinculo.professorId} onChange={(e) => setNovoVinculo({ ...novoVinculo, professorId: Number(e.target.value) })}>
            <option value="">Selecione...</option>
            {professores.map((p) => <option key={p.id} value={p.id}>{p.nome}</option>)}
          </Campo>
          <Campo id="vt-ch" label="Carga Horária (h)" type="number" value={novoVinculo.cargaHoraria} onChange={(e) => setNovoVinculo({ ...novoVinculo, cargaHoraria: Number(e.target.value) })} />
          <div>
            <button type="button" className="btn btn-sucesso" onClick={salvarVinculoDisciplina}>Vincular à Turma</button>
          </div>
        </div>

        <Tabela
          colunas={['Disciplina', 'Código', 'Professor', 'Carga Horária']}
          dados={turmaDisciplinas}
          renderLinha={(td) => (
            <tr key={td.id}>
              <td><strong>{td.disciplinaNome}</strong></td>
              <td>{td.disciplinaCodigo}</td>
              <td>{td.professorNome}</td>
              <td>{td.cargaHoraria}h</td>
            </tr>
          )}
        />
      </section>
    </div>
  );
}
