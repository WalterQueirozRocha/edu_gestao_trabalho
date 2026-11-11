import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { useAuth } from '../../auth/AuthContext';
import { Campo } from '../../components/Campo';
import { Alerta } from '../../components/Alerta';

const PERFIS_ALVO = ['ALUNO', 'RESPONSAVEL', 'PROFESSOR', 'PROFISSIONAL_APOIO'];

export function ComunicadosPage() {
  const { usuario } = useAuth();
  const podePublicar = usuario.perfil === 'COORDENADOR' || usuario.perfil === 'ADMINISTRADOR';

  const [comunicados, setComunicados] = useState([]);
  const [turmas, setTurmas] = useState([]);
  const [titulo, setTitulo] = useState('');
  const [mensagem, setMensagem] = useState('');
  const [perfisSelecionados, setPerfisSelecionados] = useState(['ALUNO', 'RESPONSAVEL']);
  const [turmasSelecionadas, setTurmasSelecionadas] = useState([]);
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  async function carregar() {
    try {
      const lista = await apiFetch('/comunicados');
      setComunicados(lista);
      if (podePublicar) {
        const ts = await apiFetch('/turmas');
        setTurmas(ts);
      }
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  useEffect(() => {
    carregar();
  }, []);

  async function publicar(e) {
    e.preventDefault();
    try {
      await apiFetch('/comunicados', {
        method: 'POST',
        body: JSON.stringify({
          titulo,
          mensagem,
          publicoAlvo: perfisSelecionados,
          turmasIds: turmasSelecionadas
        })
      });
      setMsg({ tipo: 'sucesso', texto: 'Comunicado publicado com sucesso (RF-08).' });
      setTitulo('');
      setMensagem('');
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  function alternarPerfil(p) {
    setPerfisSelecionados((atual) =>
      atual.includes(p) ? atual.filter((x) => x !== p) : [...atual, p]
    );
  }

  function alternarTurma(id) {
    setTurmasSelecionadas((atual) =>
      atual.includes(id) ? atual.filter((x) => x !== id) : [...atual, id]
    );
  }

  return (
    <div>
      <h1 style={{ marginTop: 0 }}>Comunicados Escolares (UC11, UC17 — RF-08)</h1>
      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      {podePublicar && (
        <section className="card">
          <h2 style={{ marginTop: 0 }}>Publicar Novo Comunicado</h2>
          <form onSubmit={publicar} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            <Campo id="com-titulo" label="Título do Aviso" value={titulo} onChange={(e) => setTitulo(e.target.value)} required />
            <Campo id="com-msg" label="Mensagem" as="textarea" rows="3" value={mensagem} onChange={(e) => setMensagem(e.target.value)} required />

            <fieldset style={{ border: '1px solid var(--cor-borda)', borderRadius: 6, padding: 12 }}>
              <legend style={{ fontWeight: 600 }}>Público-Alvo por Perfil</legend>
              <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
                {PERFIS_ALVO.map((p) => (
                  <label key={p} style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
                    <input
                      type="checkbox"
                      checked={perfisSelecionados.includes(p)}
                      onChange={() => alternarPerfil(p)}
                    />
                    {p}
                  </label>
                ))}
              </div>
            </fieldset>

            <fieldset style={{ border: '1px solid var(--cor-borda)', borderRadius: 6, padding: 12 }}>
              <legend style={{ fontWeight: 600 }}>Turmas Destinatárias (vazio = todas)</legend>
              <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
                {turmas.map((t) => (
                  <label key={t.id} style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
                    <input
                      type="checkbox"
                      checked={turmasSelecionadas.includes(t.id)}
                      onChange={() => alternarTurma(t.id)}
                    />
                    {t.nome}
                  </label>
                ))}
              </div>
            </fieldset>

            <div>
              <button type="submit" className="btn btn-primario">Publicar Comunicado</button>
            </div>
          </form>
        </section>
      )}

      <section>
        <h2>Mural de Comunicados</h2>
        {comunicados.map((c) => (
          <article key={c.id} className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
              <h3 style={{ marginTop: 0, color: 'var(--cor-primaria)' }}>{c.titulo}</h3>
              <span style={{ fontSize: '0.85rem', color: 'var(--cor-texto-secundario)' }}>
                {c.dataPublicacao?.replace('T', ' ').substring(0, 16)} — Por: {c.autorNome}
              </span>
            </div>
            <p style={{ marginBottom: 10 }}>{c.mensagem}</p>
            <div style={{ fontSize: '0.82rem', color: 'var(--cor-texto-secundario)' }}>
              Público: {c.publicoAlvo?.length > 0 ? c.publicoAlvo.join(', ') : 'Todos'} | Turmas:{' '}
              {c.turmasNomes?.length > 0 ? c.turmasNomes.join(', ') : 'Todas as turmas'}
            </div>
          </article>
        ))}
      </section>
    </div>
  );
}
