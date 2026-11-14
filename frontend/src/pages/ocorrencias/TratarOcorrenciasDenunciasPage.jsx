import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { Campo } from '../../components/Campo';
import { Tabela } from '../../components/Tabela';
import { Alerta } from '../../components/Alerta';
import { Modal } from '../../components/Modal';

export function TratarOcorrenciasDenunciasPage() {
  const [ocorrencias, setOcorrencias] = useState([]);
  const [denuncias, setDenuncias] = useState([]);
  const [alunos, setAlunos] = useState([]);
  // Caso em tratamento: { tipo: 'denuncia' | 'ocorrencia', id, rotulo }
  const [caso, setCaso] = useState(null);
  const [statusNovo, setStatusNovo] = useState('ENCAMINHADA');
  const [medidas, setMedidas] = useState('');
  const [justificativa, setJustificativa] = useState('');
  const [requerPlano, setRequerPlano] = useState(false);
  const [alunoIdPlano, setAlunoIdPlano] = useState('');
  const [metasPlano, setMetasPlano] = useState('');
  const [adaptacoesPlano, setAdaptacoesPlano] = useState('');
  const [estrategiasPlano, setEstrategiasPlano] = useState('');
  const [dataRevisaoPlano, setDataRevisaoPlano] = useState('2026-09-30');
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  async function carregar() {
    try {
      const [oc, den, al] = await Promise.all([
        apiFetch('/ocorrencias'),
        apiFetch('/denuncias'),
        apiFetch('/usuarios?perfil=ALUNO')
      ]);
      setOcorrencias(oc);
      setDenuncias(den);
      setAlunos(al);
      if (al.length > 0 && !alunoIdPlano) setAlunoIdPlano(String(al[0].id));
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  useEffect(() => {
    carregar();
  }, []);

  function abrirTratamento(novoCaso, medidasAtuais, justificativaAtual) {
    setCaso(novoCaso);
    setStatusNovo('ENCAMINHADA');
    setMedidas(medidasAtuais || '');
    setJustificativa(justificativaAtual || '');
  }

  async function salvarTratamento(e) {
    e.preventDefault();
    if (!caso) return;
    try {
      const recurso = caso.tipo === 'ocorrencia' ? 'ocorrencias' : 'denuncias';
      await apiFetch(`/${recurso}/${caso.id}`, {
        method: 'PATCH',
        body: JSON.stringify({
          status: statusNovo,
          medidas: medidas || null,
          justificativaArquivamento: justificativa || null,
          requerAcompanhamentoPedagogico: statusNovo !== 'ARQUIVADA' && requerPlano,
          alunoIdPlano: statusNovo !== 'ARQUIVADA' && requerPlano ? Number(alunoIdPlano) : null,
          metasPlano: metasPlano || null,
          adaptacoesPlano: adaptacoesPlano || null,
          estrategiasPlano: estrategiasPlano || null,
          dataRevisaoPlano: dataRevisaoPlano || null
        })
      });
      setMsg({
        tipo: 'sucesso',
        texto: `${caso.rotulo} atualizada para ${statusNovo}${requerPlano && statusNovo !== 'ARQUIVADA' ? ' e Plano Pedagógico (RF-10) registrado.' : '.'}`
      });
      setCaso(null);
      setRequerPlano(false);
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  return (
    <div>
      <h1 style={{ marginTop: 0 }}>Tratamento Restrito de Ocorrências e Denúncias (UC12 — RF-09)</h1>
      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      <section className="card">
        <h2 style={{ marginTop: 0 }}>Denúncias Recebidas (Acesso Restrito à Equipe Responsável)</h2>
        <Tabela
          colunas={['Protocolo', 'Data', 'Autor', 'Relato', 'Status', 'Medidas / Justificativa', 'Ação']}
          dados={denuncias}
          renderLinha={(d) => (
            <tr key={d.id}>
              <td><strong>{d.protocolo}</strong></td>
              <td>{d.criadaEm?.replace('T', ' ').substring(0, 16)}</td>
              <td>{d.anonima ? 'Anônimo' : d.denuncianteNome}</td>
              <td>{d.descricao}</td>
              <td><span className="badge badge-medio">{d.status}</span></td>
              <td>{d.medidas || d.justificativaArquivamento || '—'}</td>
              <td>
                <button
                  type="button"
                  className="btn btn-primario"
                  onClick={() => {
                    abrirTratamento({ tipo: 'denuncia', id: d.id, rotulo: `Denúncia ${d.protocolo}` }, d.medidas, d.justificativaArquivamento);
                  }}
                >
                  Analisar Caso
                </button>
              </td>
            </tr>
          )}
        />
      </section>

      <section className="card">
        <h2 style={{ marginTop: 0 }}>Histórico de Ocorrências Escolares</h2>
        <Tabela
          colunas={['Data', 'Tipo', 'Estudantes Envolvidos', 'Descrição', 'Registrada por', 'Status', 'Medidas', 'Ação']}
          dados={ocorrencias}
          renderLinha={(o) => (
            <tr key={o.id}>
              <td>{o.data}</td>
              <td><span className="badge badge-fechada">{o.tipo}</span></td>
              <td><strong>{o.alunosNomes?.join(', ')}</strong></td>
              <td>{o.descricao}</td>
              <td>{o.registradaPorNome}</td>
              <td><span className="badge badge-medio">{o.status}</span></td>
              <td>{o.medidas || '—'}</td>
              <td>
                <button
                  type="button"
                  className="btn btn-primario"
                  onClick={() => abrirTratamento({ tipo: 'ocorrencia', id: o.id, rotulo: `Ocorrência #${o.id}` }, o.medidas, null)}
                >
                  Tratar Ocorrência
                </button>
              </td>
            </tr>
          )}
        />
      </section>

      <Modal
        aberto={Boolean(caso)}
        titulo={`Tratar ${caso?.rotulo || ''}`}
        onFechar={() => setCaso(null)}
      >
        <form onSubmit={salvarTratamento} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <Campo id="tr-status" label="Decisão do Atendimento (BPMN Fig. 3)" as="select" value={statusNovo} onChange={(e) => setStatusNovo(e.target.value)}>
            <option value="EM_ANALISE">EM_ANALISE</option>
            <option value="ENCAMINHADA">ENCAMINHADA (Procedente)</option>
            <option value="CONCLUIDA">CONCLUIDA</option>
            <option value="ARQUIVADA">ARQUIVADA (Improcedente — exige justificativa)</option>
          </Campo>

          {statusNovo === 'ARQUIVADA' ? (
            <Campo
              id="tr-just"
              label="Justificativa de Arquivamento (Obrigatória)"
              as="textarea"
              rows="3"
              value={justificativa}
              onChange={(e) => setJustificativa(e.target.value)}
              required
            />
          ) : (
            <>
              <Campo
                id="tr-med"
                label="Medidas Pedagógicas e Encaminhamentos"
                as="textarea"
                rows="3"
                value={medidas}
                onChange={(e) => setMedidas(e.target.value)}
                required
              />

              <label style={{ display: 'inline-flex', alignItems: 'center', gap: 8, fontWeight: 600 }}>
                <input
                  type="checkbox"
                  checked={requerPlano}
                  onChange={(e) => setRequerPlano(e.target.checked)}
                />
                Requer acompanhamento pedagógico? Registrar Plano Pedagógico (BPMN Fig. 3 — RF-10)
              </label>

              {requerPlano && (
                <div style={{ border: '1px solid var(--cor-borda)', borderRadius: 6, padding: 12, display: 'flex', flexDirection: 'column', gap: 10 }}>
                  <Campo id="tr-pl-aluno" label="Estudante para Acompanhamento (RF-10)" as="select" value={alunoIdPlano} onChange={(e) => setAlunoIdPlano(e.target.value)} required>
                    {alunos.map((a) => (
                      <option key={a.id} value={a.id}>{a.nome} ({a.matricula})</option>
                    ))}
                  </Campo>
                  <Campo id="tr-pl-metas" label="Metas Pedagógicas" value={metasPlano} onChange={(e) => setMetasPlano(e.target.value)} required />
                  <Campo id="tr-pl-adap" label="Adaptações Pedagógicas" value={adaptacoesPlano} onChange={(e) => setAdaptacoesPlano(e.target.value)} required />
                  <Campo id="tr-pl-estr" label="Estratégias de Acompanhamento" value={estrategiasPlano} onChange={(e) => setEstrategiasPlano(e.target.value)} required />
                  <Campo id="tr-pl-rev" label="Data de Revisão" type="date" value={dataRevisaoPlano} onChange={(e) => setDataRevisaoPlano(e.target.value)} required />
                </div>
              )}
            </>
          )}

          <button type="submit" className="btn btn-sucesso">Salvar Decisão</button>
        </form>
      </Modal>
    </div>
  );
}
