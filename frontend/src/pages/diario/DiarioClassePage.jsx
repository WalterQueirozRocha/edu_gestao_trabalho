import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { Campo } from '../../components/Campo';
import { Tabela } from '../../components/Tabela';
import { Alerta } from '../../components/Alerta';
import { formatarData } from '../../utils/formatters';

export function DiarioClassePage() {
  const [turmasDisciplinas, setTurmasDisciplinas] = useState([]);
  const [tdSelecionada, setTdSelecionada] = useState('');
  const [aulas, setAulas] = useState([]);
  const [aulaSelecionada, setAulaSelecionada] = useState(null);
  const [chamada, setChamada] = useState([]);
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  const [novaAula, setNovaAula] = useState({ data: '2026-08-18', conteudo: '', horas: 4 });

  useEffect(() => {
    apiFetch('/professor/turmas')
      .then((lista) => {
        setTurmasDisciplinas(lista);
        if (lista.length > 0) setTdSelecionada(String(lista[0].id));
      })
      .catch((err) => setMsg({ tipo: 'erro', texto: err.message }));
  }, []);

  useEffect(() => {
    if (tdSelecionada) {
      carregarAulas(tdSelecionada);
    }
  }, [tdSelecionada]);

  async function carregarAulas(tdId) {
    try {
      const lista = await apiFetch(`/aulas?turmaDisciplinaId=${tdId}`);
      setAulas(lista);
      if (lista.length > 0) {
        selecionarAulaParaChamada(lista[0]);
      } else {
        setAulaSelecionada(null);
        setChamada([]);
      }
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function selecionarAulaParaChamada(aula) {
    setAulaSelecionada(aula);
    try {
      const td = turmasDisciplinas.find((item) => String(item.id) === String(aula.turmaDisciplinaId));
      const [existentes, matriculas] = await Promise.all([
        apiFetch(`/aulas/${aula.id}/frequencias`),
        td ? apiFetch(`/matriculas?turmaId=${td.turmaId}`) : Promise.resolve([])
      ]);

      const ativos = matriculas.filter((m) => m.status === 'ATIVA');
      const resumos = await Promise.all(
        ativos.map((m) => apiFetch(`/matriculas/${m.id}/frequencia`).catch(() => null))
      );

      const listaChamada = ativos.map((m, i) => {
        const reg = existentes.find((f) => f.matriculaId === m.id);
        const resumo = resumos[i];
        return {
          matriculaId: m.id,
          alunoId: m.alunoId,
          alunoNome: m.alunoNome,
          presente: reg ? reg.presente : true,
          jaRegistradoPresente: reg ? reg.presente : null,
          justificativa: reg?.justificativa || '',
          resumoFreq: resumo
        };
      });
      setChamada(listaChamada);
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  function calcularFreqSimulada(item) {
    const r = item.resumoFreq;
    if (!r) return { percentual: 100, horasFaltas: 0, horasRegistradas: 0, freqMinima: 75 };
    const horasAula = Number(aulaSelecionada?.horas || 0);
    let totalHoras = r.horasRegistradas;
    let horasPres = r.horasPresentes;

    if (item.jaRegistradoPresente === null) {
      totalHoras += horasAula;
      if (item.presente) horasPres += horasAula;
    } else if (item.jaRegistradoPresente !== item.presente) {
      horasPres += item.presente ? horasAula : -horasAula;
    }

    const percentual = totalHoras === 0 ? 100 : Number(((horasPres * 100) / totalHoras).toFixed(2));
    return {
      percentual,
      horasFaltas: Math.max(0, totalHoras - horasPres),
      horasRegistradas: totalHoras,
      freqMinima: Number(r.frequenciaMinimaExigida || 75)
    };
  }

  function marcarTodos(presente) {
    setChamada((atual) => atual.map((item) => ({ ...item, presente })));
  }

  async function registrarNovaAula(e) {
    e.preventDefault();
    try {
      const criada = await apiFetch('/aulas', {
        method: 'POST',
        body: JSON.stringify({
          turmaDisciplinaId: Number(tdSelecionada),
          data: novaAula.data,
          conteudo: novaAula.conteudo,
          horas: Number(novaAula.horas)
        })
      });
      setMsg({ tipo: 'sucesso', texto: 'Aula registrada no diário eletrônico.' });
      setNovaAula({ ...novaAula, conteudo: '' });
      await carregarAulas(tdSelecionada);
      selecionarAulaParaChamada(criada);
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function salvarChamada() {
    if (!aulaSelecionada) return;
    try {
      await apiFetch(`/aulas/${aulaSelecionada.id}/frequencias`, {
        method: 'PUT',
        body: JSON.stringify({ frequencias: chamada })
      });
      setMsg({ tipo: 'sucesso', texto: 'Frequência registrada e percentuais atualizados (RN-02 / RN-04).' });
      await selecionarAulaParaChamada(aulaSelecionada);
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Diário de Classe Eletrônico e Frequência (UC05 — RF-05, RN-02, RN-04)</h1>
          <p className="page-subtitle">
            Registro de aulas ministradas e chamada por horas com cálculo imediato de assiduidade.
          </p>
        </div>
      </div>

      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      <section className="card">
        <div className="form-grid" style={{ alignItems: 'end' }}>
          <Campo
            id="diario-td"
            label="Turma / Disciplina vinculada"
            as="select"
            value={tdSelecionada}
            onChange={(e) => setTdSelecionada(e.target.value)}
          >
            {turmasDisciplinas.map((td) => (
              <option key={td.id} value={td.id}>
                {td.turmaNome} — {td.disciplinaNome} ({td.cargaHoraria}h)
              </option>
            ))}
          </Campo>
        </div>

        <form onSubmit={registrarNovaAula} className="form-grid" style={{ alignItems: 'end' }}>
          <Campo
            id="aula-data"
            label="Data da Aula"
            type="date"
            value={novaAula.data}
            onChange={(e) => setNovaAula({ ...novaAula, data: e.target.value })}
            required
          />
          <Campo
            id="aula-conteudo"
            label="Conteúdo Ministrado"
            value={novaAula.conteudo}
            onChange={(e) => setNovaAula({ ...novaAula, conteudo: e.target.value })}
            required
          />
          <Campo
            id="aula-horas"
            label="Horas-aula"
            type="number"
            min="1"
            max="10"
            value={novaAula.horas}
            onChange={(e) => setNovaAula({ ...novaAula, horas: e.target.value })}
            required
          />
          <div>
            <button type="submit" className="btn btn-primario">Registrar Aula</button>
          </div>
        </form>
      </section>

      <div className="grid-cards" style={{ gridTemplateColumns: '1fr 1.5fr' }}>
        <section className="card">
          <h2 style={{ marginTop: 0 }}>Aulas Registradas ({aulas.length})</h2>
          <Tabela
            colunas={['Data', 'Conteúdo', 'Horas', 'Chamada']}
            dados={aulas}
            renderLinha={(a) => (
              <tr key={a.id}>
                <td>{formatarData(a.data)}</td>
                <td>{a.conteudo}</td>
                <td><strong>{a.horas}h</strong></td>
                <td>
                  <button
                    type="button"
                    className={aulaSelecionada?.id === a.id ? 'btn btn-primario btn-sm' : 'btn btn-secundario btn-sm'}
                    onClick={() => selecionarAulaParaChamada(a)}
                  >
                    {aulaSelecionada?.id === a.id ? 'Aberta' : 'Chamada'}
                  </button>
                </td>
              </tr>
            )}
          />
        </section>

        <section className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 10, marginBottom: 12 }}>
            <h2 style={{ margin: 0 }}>
              Chamada {aulaSelecionada ? `— ${formatarData(aulaSelecionada.data)} (${aulaSelecionada.horas}h)` : ''}
            </h2>
            {aulaSelecionada && chamada.length > 0 && (
              <div style={{ display: 'flex', gap: 6 }}>
                <button type="button" className="btn btn-secundario btn-sm" onClick={() => marcarTodos(true)}>
                  Marcar todos presentes
                </button>
                <button type="button" className="btn btn-secundario btn-sm" onClick={() => marcarTodos(false)}>
                  Marcar todos ausentes
                </button>
              </div>
            )}
          </div>
          {aulaSelecionada ? (
            <>
              <p style={{ marginTop: 0, marginBottom: 12, fontSize: '0.88rem', color: 'var(--cor-texto-secundario)' }}>
                Conteúdo: <strong>{aulaSelecionada.conteudo}</strong>
              </p>
              <Tabela
                colunas={['Estudante', 'Presença', '% Presença / Faltas (RN-02)', 'Situação Frequência', 'Justificativa (opcional)']}
                dados={chamada}
                renderLinha={(item, idx) => {
                  const freq = calcularFreqSimulada(item);
                  const retido = freq.horasRegistradas > 0 && freq.percentual < freq.freqMinima;
                  const alerta80 = freq.horasRegistradas > 0 && !retido && freq.percentual < 80;
                  return (
                    <tr key={item.matriculaId}>
                      <td><strong>{item.alunoNome}</strong></td>
                      <td>
                        <label style={{ display: 'inline-flex', alignItems: 'center', gap: 6, cursor: 'pointer', fontWeight: 600 }}>
                          <input
                            type="checkbox"
                            checked={item.presente}
                            onChange={(e) => {
                              const copia = [...chamada];
                              copia[idx] = { ...item, presente: e.target.checked };
                              setChamada(copia);
                            }}
                          />
                          <span style={{ color: item.presente ? 'var(--cor-secundaria)' : 'var(--cor-perigo)' }}>
                            {item.presente ? 'Presente' : 'Falta'}
                          </span>
                        </label>
                      </td>
                      <td>
                        <strong>{freq.percentual}%</strong>{' '}
                        <span style={{ fontSize: '0.8rem', color: 'var(--cor-texto-secundario)' }}>
                          ({freq.horasFaltas}h falta / {freq.horasRegistradas}h)
                        </span>
                      </td>
                      <td>
                        {retido ? (
                          <span className="badge badge-retido">RETIDO (&lt; {freq.freqMinima}%)</span>
                        ) : alerta80 ? (
                          <span className="badge badge-medio">ALERTA (&lt; 80%)</span>
                        ) : (
                          <span className="badge badge-aprovado">REGULAR</span>
                        )}
                      </td>
                      <td>
                        <input
                          type="text"
                          className="campo-input"
                          placeholder="Ex.: Atestado médico"
                          aria-label={`Justificativa de ${item.alunoNome}`}
                          value={item.justificativa}
                          onChange={(e) => {
                            const copia = [...chamada];
                            copia[idx] = { ...item, justificativa: e.target.value };
                            setChamada(copia);
                          }}
                        />
                      </td>
                    </tr>
                  );
                }}
              />
              <div style={{ marginTop: 16 }}>
                <button type="button" className="btn btn-sucesso" onClick={salvarChamada}>
                  Salvar Frequência da Aula
                </button>
              </div>
            </>
          ) : (
            <p>Selecione ou cadastre uma aula para registrar a presença dos estudantes.</p>
          )}
        </section>
      </div>
    </div>
  );
}
