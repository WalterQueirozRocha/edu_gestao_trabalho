import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { Campo } from '../../components/Campo';
import { Tabela } from '../../components/Tabela';
import { Alerta } from '../../components/Alerta';

export function AvaliacoesNotasPage() {
  const [turmasDisciplinas, setTurmasDisciplinas] = useState([]);
  const [etapas, setEtapas] = useState([]);
  const [tdSelecionada, setTdSelecionada] = useState('');
  const [etapaSelecionada, setEtapaSelecionada] = useState('');
  const [avaliacoes, setAvaliacoes] = useState([]);
  const [avaliacaoAtiva, setAvaliacaoAtiva] = useState(null);
  const [notas, setNotas] = useState([]);
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  const [novaAv, setNovaAv] = useState({ descricao: '', data: '2026-08-28', peso: 5.0 });

  useEffect(() => {
    Promise.all([apiFetch('/professor/turmas'), apiFetch('/etapas')])
      .then(([tds, ets]) => {
        setTurmasDisciplinas(tds);
        setEtapas(ets);
        if (tds.length > 0) setTdSelecionada(String(tds[0].id));
        if (ets.length > 0) setEtapaSelecionada(String(ets[0].id));
      })
      .catch((err) => setMsg({ tipo: 'erro', texto: err.message }));
  }, []);

  useEffect(() => {
    if (tdSelecionada && etapaSelecionada) {
      carregarAvaliacoes();
    }
  }, [tdSelecionada, etapaSelecionada]);

  async function carregarAvaliacoes() {
    try {
      const lista = await apiFetch(`/avaliacoes?turmaDisciplinaId=${tdSelecionada}&etapaId=${etapaSelecionada}`);
      setAvaliacoes(lista);
      if (lista.length > 0) {
        selecionarAvaliacao(lista[0]);
      } else {
        setAvaliacaoAtiva(null);
        setNotas([]);
      }
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function selecionarAvaliacao(av) {
    setAvaliacaoAtiva(av);
    try {
      const td = turmasDisciplinas.find((item) => String(item.id) === String(av.turmaDisciplinaId));
      const [notasExistentes, matriculas] = await Promise.all([
        apiFetch(`/avaliacoes/${av.id}/notas`),
        td ? apiFetch(`/matriculas?turmaId=${td.turmaId}`) : Promise.resolve([])
      ]);
      const ativos = matriculas.filter((m) => m.status === 'ATIVA');
      const planilha = ativos.map((m) => {
        const encontrada = notasExistentes.find((n) => n.matriculaId === m.id);
        return {
          matriculaId: m.id,
          alunoId: m.alunoId,
          alunoNome: m.alunoNome,
          valor: encontrada ? encontrada.valor : 0
        };
      });
      setNotas(planilha);
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function criarAvaliacao(e) {
    e.preventDefault();
    try {
      const criada = await apiFetch('/avaliacoes', {
        method: 'POST',
        body: JSON.stringify({
          turmaDisciplinaId: Number(tdSelecionada),
          etapaId: Number(etapaSelecionada),
          descricao: novaAv.descricao,
          data: novaAv.data,
          peso: Number(novaAv.peso)
        })
      });
      setMsg({ tipo: 'sucesso', texto: 'Avaliação cadastrada com sucesso (RF-06).' });
      setNovaAv({ ...novaAv, descricao: '' });
      await carregarAvaliacoes();
      selecionarAvaliacao(criada);
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function salvarNotas() {
    if (!avaliacaoAtiva) return;
    try {
      await apiFetch(`/avaliacoes/${avaliacaoAtiva.id}/notas`, {
        method: 'PUT',
        body: JSON.stringify({
          notas: notas.map((n) => ({
            matriculaId: n.matriculaId,
            valor: Number(n.valor)
          }))
        })
      });
      setMsg({ tipo: 'sucesso', texto: 'Notas lançadas com sucesso (RN-03 / RN-04).' });
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  return (
    <div>
      <h1 style={{ marginTop: 0 }}>Avaliações e Lançamento de Notas (UC06 — RF-06, RN-03, RN-04)</h1>
      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      <section className="card">
        <div className="form-grid">
          <Campo
            id="av-td"
            label="Turma / Disciplina"
            as="select"
            value={tdSelecionada}
            onChange={(e) => setTdSelecionada(e.target.value)}
          >
            {turmasDisciplinas.map((td) => (
              <option key={td.id} value={td.id}>{td.turmaNome} — {td.disciplinaNome}</option>
            ))}
          </Campo>
          <Campo
            id="av-etapa"
            label="Etapa Letiva"
            as="select"
            value={etapaSelecionada}
            onChange={(e) => setEtapaSelecionada(e.target.value)}
          >
            {etapas.map((et) => (
              <option key={et.id} value={et.id}>{et.nome} ({et.status})</option>
            ))}
          </Campo>
        </div>

        <form onSubmit={criarAvaliacao} className="form-grid" style={{ alignItems: 'end' }}>
          <Campo
            id="av-desc"
            label="Descrição da Avaliação"
            value={novaAv.descricao}
            onChange={(e) => setNovaAv({ ...novaAv, descricao: e.target.value })}
            required
          />
          <Campo
            id="av-data"
            label="Data"
            type="date"
            value={novaAv.data}
            onChange={(e) => setNovaAv({ ...novaAv, data: e.target.value })}
            required
          />
          <Campo
            id="av-peso"
            label="Peso na Média Ponderada (RN-03)"
            type="number"
            step="0.5"
            min="0.1"
            value={novaAv.peso}
            onChange={(e) => setNovaAv({ ...novaAv, peso: e.target.value })}
            required
          />
          <div>
            <button type="submit" className="btn btn-primario">Cadastrar Avaliação</button>
          </div>
        </form>
      </section>

      <div className="grid-cards" style={{ gridTemplateColumns: '1fr 1.4fr' }}>
        <section className="card">
          <h2 style={{ marginTop: 0 }}>Avaliações da Etapa</h2>
          <Tabela
            colunas={['Descrição', 'Data', 'Peso', 'Ação']}
            dados={avaliacoes}
            renderLinha={(av) => (
              <tr key={av.id}>
                <td><strong>{av.descricao}</strong></td>
                <td>{av.data}</td>
                <td>{av.peso}</td>
                <td>
                  <button
                    type="button"
                    className={avaliacaoAtiva?.id === av.id ? 'btn btn-primario' : 'btn btn-secundario'}
                    onClick={() => selecionarAvaliacao(av)}
                  >
                    Lançar Notas
                  </button>
                </td>
              </tr>
            )}
          />
        </section>

        <section className="card">
          <h2 style={{ marginTop: 0 }}>
            Planilha de Notas {avaliacaoAtiva ? `(${avaliacaoAtiva.descricao} — Peso ${avaliacaoAtiva.peso})` : ''}
          </h2>
          {avaliacaoAtiva ? (
            <>
              <Tabela
                colunas={['Estudante', 'Nota (0,00 a 10,00)']}
                dados={notas}
                renderLinha={(n, idx) => (
                  <tr key={n.matriculaId}>
                    <td><strong>{n.alunoNome}</strong></td>
                    <td>
                      <input
                        type="number"
                        className="campo-input"
                        aria-label={`Nota de ${n.alunoNome}`}
                        min="0"
                        max="10"
                        step="0.1"
                        style={{ maxWidth: 120 }}
                        value={n.valor}
                        onChange={(e) => {
                          const copia = [...notas];
                          copia[idx] = { ...n, valor: e.target.value };
                          setNotas(copia);
                        }}
                      />
                    </td>
                  </tr>
                )}
              />
              <div style={{ marginTop: 16 }}>
                <button type="button" className="btn btn-sucesso" onClick={salvarNotas}>
                  Salvar Notas da Avaliação
                </button>
              </div>
            </>
          ) : (
            <p>Selecione ou crie uma avaliação para lançar as notas dos estudantes.</p>
          )}
        </section>
      </div>
    </div>
  );
}
