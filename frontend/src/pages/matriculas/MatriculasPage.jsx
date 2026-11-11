import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { Campo } from '../../components/Campo';
import { Tabela } from '../../components/Tabela';
import { Alerta } from '../../components/Alerta';

export function MatriculasPage() {
  const [matriculas, setMatriculas] = useState([]);
  const [alunos, setAlunos] = useState([]);
  const [turmas, setTurmas] = useState([]);
  const [alunoId, setAlunoId] = useState('');
  const [turmaId, setTurmaId] = useState('');
  const [filtroAlunoHistId, setFiltroAlunoHistId] = useState('');
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  async function carregar(alunoFiltro = filtroAlunoHistId) {
    try {
      const endpointMat = alunoFiltro ? `/alunos/${alunoFiltro}/matriculas` : '/matriculas';
      const [m, a, t] = await Promise.all([
        apiFetch(endpointMat),
        apiFetch('/usuarios?perfil=ALUNO'),
        apiFetch('/turmas')
      ]);
      setMatriculas(m);
      setAlunos(a);
      setTurmas(t);
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  useEffect(() => {
    carregar(filtroAlunoHistId);
  }, [filtroAlunoHistId]);

  async function realizarMatricula(e) {
    e.preventDefault();
    try {
      await apiFetch('/matriculas', {
        method: 'POST',
        body: JSON.stringify({ alunoId: Number(alunoId), turmaId: Number(turmaId) })
      });
      setMsg({ tipo: 'sucesso', texto: 'Matrícula registrada com sucesso (RF-04).' });
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function alterarStatus(id, status) {
    try {
      await apiFetch(`/matriculas/${id}/status`, {
        method: 'PATCH',
        body: JSON.stringify({ status })
      });
      setMsg({ tipo: 'sucesso', texto: `Status da matrícula atualizado para ${status} (RN-07: histórico preservado).` });
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  return (
    <div>
      <h1 style={{ marginTop: 0 }}>Matrículas e Histórico Escolar (UC04 — RN-01, RN-07)</h1>
      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      <section className="card" aria-labelledby="sec-nova-mat">
        <h2 id="sec-nova-mat" style={{ marginTop: 0 }}>Nova Matrícula</h2>
        <form onSubmit={realizarMatricula} className="form-grid" style={{ alignItems: 'end' }}>
          <Campo id="mat-aluno" label="Estudante" as="select" value={alunoId} onChange={(e) => setAlunoId(e.target.value)} required>
            <option value="">Selecione um estudante...</option>
            {alunos.map((a) => (
              <option key={a.id} value={a.id}>{a.nome} ({a.matricula})</option>
            ))}
          </Campo>
          <Campo id="mat-turma" label="Turma" as="select" value={turmaId} onChange={(e) => setTurmaId(e.target.value)} required>
            <option value="">Selecione uma turma...</option>
            {turmas.map((t) => (
              <option key={t.id} value={t.id}>{t.nome} - {t.serie} ({t.anoLetivoAno})</option>
            ))}
          </Campo>
          <div>
            <button type="submit" className="btn btn-primario">Matricular Estudante</button>
          </div>
        </form>
      </section>

      <section className="card" aria-labelledby="sec-hist-mat">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12, flexWrap: 'wrap', gap: 12 }}>
          <h2 id="sec-hist-mat" style={{ margin: 0 }}>Histórico de Matrículas (RN-07: Sem exclusão física)</h2>
          <Campo
            id="filtro-aluno-hist"
            label="Filtrar Histórico por Estudante"
            as="select"
            value={filtroAlunoHistId}
            onChange={(e) => setFiltroAlunoHistId(e.target.value)}
          >
            <option value="">Todos os estudantes</option>
            {alunos.map((a) => (
              <option key={a.id} value={a.id}>{a.nome} ({a.matricula})</option>
            ))}
          </Campo>
        </div>
        <Tabela
          colunas={['ID', 'Estudante', 'Código', 'Turma / Série', 'Ano Letivo', 'Data', 'Status', 'Alterar Situação']}
          dados={matriculas}
          renderLinha={(m) => (
            <tr key={m.id}>
              <td>{m.id}</td>
              <td><strong>{m.alunoNome}</strong></td>
              <td>{m.alunoMatriculaCodigo}</td>
              <td>{m.turmaNome} ({m.turmaSerie})</td>
              <td>{m.anoLetivo}</td>
              <td>{m.dataMatricula}</td>
              <td>
                <span className={m.status === 'ATIVA' ? 'badge badge-ativa' : 'badge badge-fechada'}>
                  {m.status}
                </span>
              </td>
              <td>
                <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                  {m.status !== 'ENCERRADA' && (
                    <button type="button" className="btn btn-secundario" onClick={() => alterarStatus(m.id, 'ENCERRADA')}>
                      Encerrar
                    </button>
                  )}
                  {m.status !== 'TRANSFERIDA' && (
                    <button type="button" className="btn btn-secundario" onClick={() => alterarStatus(m.id, 'TRANSFERIDA')}>
                      Transferir
                    </button>
                  )}
                  {m.status !== 'CANCELADA' && (
                    <button type="button" className="btn btn-perigo" onClick={() => alterarStatus(m.id, 'CANCELADA')}>
                      Cancelar
                    </button>
                  )}
                </div>
              </td>
            </tr>
          )}
        />
      </section>
    </div>
  );
}
