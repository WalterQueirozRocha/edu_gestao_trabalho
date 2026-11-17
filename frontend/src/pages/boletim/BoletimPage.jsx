import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { useAuth } from '../../auth/AuthContext';
import { Campo } from '../../components/Campo';
import { Tabela } from '../../components/Tabela';
import { Alerta } from '../../components/Alerta';
import { formatarData } from '../../utils/formatters';

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
      <div className="page-header nao-imprimir">
        <div>
          <h1 className="page-title">Boletim Escolar (UC16 — RF-07, RN-05, RN-06)</h1>
          <p className="page-subtitle">
            Consulta de médias e frequência consolidada por etapa letiva publicada.
          </p>
        </div>
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
          <p style={{ margin: 0 }}>Nenhum boletim publicado disponível para visualização no momento (RN-05).</p>
        </div>
      ) : (
        boletins.map((b) => (
          <article key={`${b.matriculaId}-${b.etapaId}`} className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', gap: 12, marginBottom: 16, paddingBottom: 12, borderBottom: '1px solid var(--cor-borda-suave)' }}>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' }}>
                  <h2 style={{ margin: 0 }}>
                    {b.etapaNome} — Ano Letivo {b.anoLetivo}
                  </h2>
                  <span className={`badge badge-${b.etapaStatus.toLowerCase()}`}>{b.etapaStatus}</span>
                </div>
                <p style={{ margin: '6px 0 0', color: 'var(--cor-texto-secundario)', fontSize: '0.92rem' }}>
                  Estudante: <strong>{b.alunoNome}</strong> ({b.alunoMatriculaCodigo}) | Turma: <strong>{b.turmaNome}</strong> ({b.turmaSerie})
                  {b.dataPublicacao && ` | Publicado em: ${formatarData(b.dataPublicacao)}`}
                </p>
              </div>
              <div style={{ textAlign: 'right' }}>
                <span className={`badge badge-${b.situacao.toLowerCase()}`} style={{ fontSize: '0.85rem', padding: '4px 12px' }}>
                  Situação: {b.situacao}
                </span>
                <div style={{ fontSize: '0.88rem', marginTop: 6 }}>
                  Média Geral: <strong>{b.mediaGeralEtapa}</strong> (mín. {b.mediaMinimaExigida}) | Frequência Geral: <strong>{b.percentualFrequenciaGeral}%</strong> (mín. {b.frequenciaMinimaExigida}%)
                </div>
              </div>
            </div>

            <Tabela
              colunas={['Código', 'Disciplina', 'Média na Etapa (RN-03)', 'Frequência na Disciplina (RN-02)', 'Situação Parcial']}
              dados={b.disciplinas}
              renderLinha={(d) => (
                <tr key={d.turmaDisciplinaId}>
                  <td><strong>{d.disciplinaCodigo}</strong></td>
                  <td><strong>{d.disciplinaNome}</strong></td>
                  <td>
                    <strong style={{ color: d.aprovadoMedia ? 'var(--cor-secundaria)' : 'var(--cor-perigo)' }}>
                      {d.mediaPonderada}
                    </strong>{' '}
                    <span style={{ fontSize: '0.8rem', color: 'var(--cor-texto-secundario)' }}>
                      (mín. {b.mediaMinimaExigida})
                    </span>
                  </td>
                  <td>
                    <strong style={{ color: d.aprovadoFrequencia ? 'var(--cor-secundaria)' : 'var(--cor-perigo)' }}>
                      {d.percentualFrequencia}%
                    </strong>{' '}
                    <span style={{ fontSize: '0.8rem', color: 'var(--cor-texto-secundario)' }}>
                      (mín. {b.frequenciaMinimaExigida}%)
                    </span>
                  </td>
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
