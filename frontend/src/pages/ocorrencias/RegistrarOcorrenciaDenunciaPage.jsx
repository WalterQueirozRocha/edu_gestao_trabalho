import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { useAuth } from '../../auth/AuthContext';
import { Campo } from '../../components/Campo';
import { Alerta } from '../../components/Alerta';

export function RegistrarOcorrenciaDenunciaPage() {
  const { usuario } = useAuth();
  const podeRegistrarOcorrencia = ['PROFESSOR', 'COORDENADOR', 'ADMINISTRADOR'].includes(usuario.perfil);

  const [alunos, setAlunos] = useState([]);
  const [alunoId, setAlunoId] = useState('');
  const [tipoOcorrencia, setTipoOcorrencia] = useState('PEDAGOGICA');
  const [descOcorrencia, setDescOcorrencia] = useState('');

  const [descDenuncia, setDescDenuncia] = useState('');
  const [anonima, setAnonima] = useState(true);
  const [protocoloGerado, setProtocoloGerado] = useState('');

  const [protocoloBusca, setProtocoloBusca] = useState('DEN-2026-0001');
  const [resultadoProtocolo, setResultadoProtocolo] = useState(null);
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  useEffect(() => {
    if (podeRegistrarOcorrencia) {
      apiFetch('/usuarios?perfil=ALUNO')
        .then((lista) => {
          setAlunos(lista);
          if (lista.length > 0) setAlunoId(String(lista[0].id));
        })
        .catch(() => {});
    }
  }, [podeRegistrarOcorrencia]);

  async function salvarOcorrencia(e) {
    e.preventDefault();
    try {
      await apiFetch('/ocorrencias', {
        method: 'POST',
        body: JSON.stringify({
          tipo: tipoOcorrencia,
          descricao: descOcorrencia,
          alunosIds: [Number(alunoId)]
        })
      });
      setMsg({ tipo: 'sucesso', texto: 'Ocorrência escolar registrada com sucesso (RF-09).' });
      setDescOcorrencia('');
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function enviarDenuncia(e) {
    e.preventDefault();
    try {
      const resp = await apiFetch('/denuncias', {
        method: 'POST',
        body: JSON.stringify({
          descricao: descDenuncia,
          anonima
        })
      });
      setProtocoloGerado(resp.protocolo);
      setProtocoloBusca(resp.protocolo);
      setDescDenuncia('');
      setMsg({ tipo: 'sucesso', texto: `Denúncia registrada! Protocolo de acompanhamento: ${resp.protocolo}` });
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function consultarProtocolo(e) {
    e.preventDefault();
    try {
      const resp = await apiFetch(`/denuncias/protocolo/${protocoloBusca}`);
      setResultadoProtocolo(resp);
    } catch (err) {
      setResultadoProtocolo(null);
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  return (
    <div>
      <h1 style={{ marginTop: 0 }}>Ocorrências e Canal de Denúncias (UC07 — RF-09)</h1>
      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      <div className="grid-cards" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(360px, 1fr))' }}>
        {podeRegistrarOcorrencia && (
          <section className="card">
            <h2 style={{ marginTop: 0 }}>Registrar Ocorrência Escolar</h2>
            <form onSubmit={salvarOcorrencia} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              <Campo id="oc-aluno" label="Estudante Envolvido" as="select" value={alunoId} onChange={(e) => setAlunoId(e.target.value)} required>
                {alunos.map((a) => <option key={a.id} value={a.id}>{a.nome} ({a.matricula})</option>)}
              </Campo>
              <Campo id="oc-tipo" label="Tipo (Sem tipos clínicos — RN-08)" as="select" value={tipoOcorrencia} onChange={(e) => setTipoOcorrencia(e.target.value)}>
                <option value="PEDAGOGICA">PEDAGÓGICA</option>
                <option value="ATRASO">ATRASO</option>
                <option value="INDISCIPLINA">INDISCIPLINA</option>
                <option value="CONFLITO">CONFLITO</option>
                <option value="OUTRO">OUTRO</option>
              </Campo>
              <Campo id="oc-desc" label="Descrição Objetiva" as="textarea" rows="3" value={descOcorrencia} onChange={(e) => setDescOcorrencia(e.target.value)} required />
              <button type="submit" className="btn btn-primario">Registrar Ocorrência</button>
            </form>
          </section>
        )}

        <section className="card">
          <h2 style={{ marginTop: 0 }}>Registrar Denúncia (Acesso Restrito à Coordenação)</h2>
          <form onSubmit={enviarDenuncia} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            <Campo id="den-desc" label="Relato Detalhado" as="textarea" rows="4" value={descDenuncia} onChange={(e) => setDescDenuncia(e.target.value)} required />
            <label style={{ display: 'inline-flex', alignItems: 'center', gap: 8, fontWeight: 600 }}>
              <input type="checkbox" checked={anonima} onChange={(e) => setAnonima(e.target.checked)} />
              Registrar de forma anônima (LGPD — RNF-03)
            </label>
            <button type="submit" className="btn btn-perigo">Enviar Denúncia e Gerar Protocolo</button>
          </form>
          {protocoloGerado && (
            <div className="alerta-box alerta-sucesso" style={{ marginTop: 12 }}>
              Protocolo gerado: <strong>{protocoloGerado}</strong>. Guarde este código para consultar o andamento.
            </div>
          )}
        </section>
      </div>

      <section className="card">
        <h2 style={{ marginTop: 0 }}>Consultar Andamento pelo Protocolo (BPMN Figura 3)</h2>
        <form onSubmit={consultarProtocolo} className="form-grid" style={{ alignItems: 'end' }}>
          <Campo id="prot-busca" label="Número do Protocolo" value={protocoloBusca} onChange={(e) => setProtocoloBusca(e.target.value)} required />
          <div>
            <button type="submit" className="btn btn-primario">Consultar Protocolo</button>
          </div>
        </form>

        {resultadoProtocolo && (
          <div className="alerta-box alerta-info" style={{ marginTop: 12 }}>
            <div><strong>Protocolo:</strong> {resultadoProtocolo.protocolo}</div>
            <div><strong>Status Atual:</strong> <span className="badge badge-publicada">{resultadoProtocolo.status}</span></div>
            <div><strong>Relato:</strong> {resultadoProtocolo.descricao}</div>
            {resultadoProtocolo.medidas && <div><strong>Medidas / Encaminhamentos:</strong> {resultadoProtocolo.medidas}</div>}
            {resultadoProtocolo.justificativaArquivamento && (
              <div><strong>Justificativa de Arquivamento:</strong> {resultadoProtocolo.justificativaArquivamento}</div>
            )}
          </div>
        )}
      </section>
    </div>
  );
}
