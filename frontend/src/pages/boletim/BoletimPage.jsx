import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { useAuth } from '../../auth/AuthContext';
import { Campo } from '../../components/Campo';
import { Tabela } from '../../components/Tabela';
import { Alerta } from '../../components/Alerta';

export function BoletimPage() {
  const { usuario } = useAuth();
  const [alunosVinculados, setAlunosVinculados] = useState([]);
  const [alunoSelecionado, setAlunoSelecionado] = useState('');
  const [boletins, setBoletins] = useState([]);
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  useEffect(() => {
    if (usuario.perfil === 'ALUNO') {
      apiFetch('/boletins/me')
        .then(setBoletins)
        .catch((err) => setMsg({ tipo: 'erro', texto: err.message }));
    } else if (usuario.perfil === 'RESPONSAVEL') {
      apiFetch('/responsavel/alunos')
        .then((lista) => {
          setAlunosVinculados(lista);
          if (lista.length > 0) setAlunoSelecionado(String(lista[0].id));
        })
        .catch((err) => setMsg({ tipo: 'erro', texto: err.message }));
    } else {
      apiFetch('/usuarios?perfil=ALUNO')
        .then((lista) => {
          setAlunosVinculados(lista);
          if (lista.length > 0) setAlunoSelecionado(String(lista[0].id));
        })
        .catch((err) => setMsg({ tipo: 'erro', texto: err.message }));
    }
  }, [usuario]);

  useEffect(() => {
    if (alunoSelecionado && usuario.perfil !== 'ALUNO') {
      apiFetch(`/alunos/${alunoSelecionado}/boletim`)
        .then(setBoletins)
        .catch((err) => setMsg({ tipo: 'erro', texto: err.message }));
    }
  }, [alunoSelecionado, usuario]);

  return (
    <div>
      <div className="nao-imprimir" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h1 style={{ margin: 0 }}>Boletim Escolar (UC16 — RF-07, RN-05, RN-06)</h1>
        <button type="button" className="btn btn-primario" onClick={() => window.print()}>
          Imprimir / Salvar em PDF
        </button>
      </div>

      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      {usuario.perfil !== 'ALUNO' && (
        <section className="card nao-imprimir">
          <Campo
            id="bol-aluno"
            label="Selecionar Estudante Vinculado (RN-06)"
            as="select"
            value={alunoSelecionado}
            onChange={(e) => setAlunoSelecionado(e.target.value)}
          >
            {alunosVinculados.map((a) => (
              <option key={a.id} value={a.id}>{a.nome} (Matrícula: {a.matricula})</option>
            ))}
          </Campo>
        </section>
      )}

      {boletins.length === 0 ? (
        <div className="card">
          <p>Nenhum boletim publicado disponível para visualização no momento (RN-05).</p>
        </div>
      ) : (
        boletins.map((b) => (
          <article key={`${b.matriculaId}-${b.etapaId}`} className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', marginBottom: 12 }}>
              <div>
                <h2 style={{ margin: 0 }}>
                  {b.etapaNome} — Ano Letivo {b.anoLetivo}
                </h2>
                <p style={{ margin: '4px 0 0', color: 'var(--cor-texto-secundario)' }}>
                  Estudante: <strong>{b.alunoNome}</strong> ({b.alunoMatriculaCodigo}) | Turma: <strong>{b.turmaNome}</strong> ({b.turmaSerie})
                </p>
              </div>
              <div style={{ textAlign: 'right' }}>
                <span className={`badge badge-${b.situacao.toLowerCase()}`}>{b.situacao}</span>
                <div style={{ fontSize: '0.85rem', marginTop: 4 }}>
                  Média Geral: <strong>{b.mediaGeralEtapa}</strong> | Frequência Geral: <strong>{b.percentualFrequenciaGeral}%</strong>
                </div>
              </div>
            </div>

            <Tabela
              colunas={['Código', 'Disciplina', 'Média Ponderada (RN-03)', 'Frequência na Disciplina (RN-02)', 'Situação Parcial']}
              dados={b.disciplinas}
              renderLinha={(d) => (
                <tr key={d.turmaDisciplinaId}>
                  <td>{d.disciplinaCodigo}</td>
                  <td><strong>{d.disciplinaNome}</strong></td>
                  <td>{d.mediaPonderada} (mín. {b.mediaMinimaExigida})</td>
                  <td>{d.percentualFrequencia}% (mín. {b.frequenciaMinimaExigida}%)</td>
                  <td>
                    {d.aprovadoMedia && d.aprovadoFrequencia ? (
                      <span className="badge badge-aprovado">REGULAR</span>
                    ) : (
                      <span className="badge badge-retido">EM ATENÇÃO</span>
                    )}
                  </td>
                </tr>
              )}
            />
          </article>
        ))
      )}
    </div>
  );
}
