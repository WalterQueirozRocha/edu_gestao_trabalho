import React, { useEffect, useState } from 'react';
import { apiFetch } from '../../api/client';
import { Campo } from '../../components/Campo';
import { Tabela } from '../../components/Tabela';
import { Alerta } from '../../components/Alerta';

export function UsuariosPage() {
  const [usuarios, setUsuarios] = useState([]);
  const [filtroPerfil, setFiltroPerfil] = useState('');
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  const [editandoId, setEditandoId] = useState(null);
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [perfil, setPerfil] = useState('ALUNO');
  const [cargo, setCargo] = useState('');
  const [registroFuncional, setRegistroFuncional] = useState('');
  const [formacao, setFormacao] = useState('');
  const [especialidade, setEspecialidade] = useState('');
  const [matricula, setMatricula] = useState('');
  const [dataNascimento, setDataNascimento] = useState('');
  const [cpf, setCpf] = useState('');
  const [telefone, setTelefone] = useState('');

  const [alunoVinculoId, setAlunoVinculoId] = useState('');
  const [responsavelVinculoId, setResponsavelVinculoId] = useState('');
  const [buscaTexto, setBuscaTexto] = useState('');

  async function carregar() {
    try {
      const query = filtroPerfil ? `?perfil=${filtroPerfil}` : '';
      const lista = await apiFetch(`/usuarios${query}`);
      setUsuarios(lista);
    } catch (e) {
      setMsg({ tipo: 'erro', texto: e.message });
    }
  }

  useEffect(() => {
    carregar();
  }, [filtroPerfil]);

  function iniciarEdicao(u) {
    setEditandoId(u.id);
    setNome(u.nome || '');
    setEmail(u.email || '');
    setSenha('');
    setPerfil(u.perfil);
    setCargo(u.cargo || '');
    setRegistroFuncional(u.registroFuncional || '');
    setFormacao(u.formacao || '');
    setEspecialidade(u.especialidade || '');
    setMatricula(u.matricula || '');
    setDataNascimento(u.dataNascimento || '');
    setCpf(u.cpf || '');
    setTelefone(u.telefone || '');
  }

  function limparFormulario() {
    setEditandoId(null);
    setNome('');
    setEmail('');
    setSenha('');
    setCargo('');
    setRegistroFuncional('');
    setFormacao('');
    setEspecialidade('');
    setMatricula('');
    setDataNascimento('');
    setCpf('');
    setTelefone('');
  }

  async function criarUsuario(e) {
    e.preventDefault();
    try {
      const payload = {
        nome,
        email,
        senha,
        perfil,
        cargo: cargo || null,
        registroFuncional: registroFuncional || null,
        formacao: formacao || null,
        especialidade: especialidade || null,
        matricula: matricula || null,
        dataNascimento: dataNascimento || null,
        cpf: cpf || null,
        telefone: telefone || null
      };
      if (editandoId) {
        await apiFetch(`/usuarios/${editandoId}`, {
          method: 'PUT',
          body: JSON.stringify(payload)
        });
        setMsg({ tipo: 'sucesso', texto: 'Dados do usuário atualizados com sucesso.' });
      } else {
        await apiFetch('/usuarios', {
          method: 'POST',
          body: JSON.stringify(payload)
        });
        setMsg({ tipo: 'sucesso', texto: 'Usuário cadastrado com sucesso.' });
      }
      limparFormulario();
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function alternarAtivo(u) {
    try {
      await apiFetch(`/usuarios/${u.id}/ativo`, {
        method: 'PATCH',
        body: JSON.stringify({ ativo: !u.ativo })
      });
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function vincularResponsavel(e) {
    e.preventDefault();
    if (!alunoVinculoId || !responsavelVinculoId) return;
    try {
      const alunoAtual = usuarios.find((u) => String(u.id) === String(alunoVinculoId));
      const atuais = alunoAtual?.responsaveisIds || [];
      const novos = Array.from(new Set([...atuais, Number(responsavelVinculoId)]));
      await apiFetch(`/alunos/${alunoVinculoId}/responsaveis`, {
        method: 'PUT',
        body: JSON.stringify({ responsaveisIds: novos })
      });
      setMsg({ tipo: 'sucesso', texto: 'Vínculo responsável–estudante registrado (RN-06).' });
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  async function removerVinculoResponsavel(aluno, respId) {
    try {
      const novos = (aluno.responsaveisIds || []).filter((id) => id !== respId);
      await apiFetch(`/alunos/${aluno.id}/responsaveis`, {
        method: 'PUT',
        body: JSON.stringify({ responsaveisIds: novos })
      });
      setMsg({ tipo: 'sucesso', texto: 'Vínculo de responsável atualizado (RN-06).' });
      carregar();
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message });
    }
  }

  const alunos = usuarios.filter((u) => u.perfil === 'ALUNO');
  const responsaveis = usuarios.filter((u) => u.perfil === 'RESPONSAVEL');
  const mapaNomes = Object.fromEntries(usuarios.map((u) => [u.id, u.nome]));
  const usuariosFiltrados = usuarios.filter((u) => {
    if (!buscaTexto.trim()) return true;
    const termo = buscaTexto.toLowerCase();
    return (
      u.nome?.toLowerCase().includes(termo) ||
      u.email?.toLowerCase().includes(termo) ||
      u.matricula?.toLowerCase().includes(termo)
    );
  });

  return (
    <div>
      <h1 style={{ marginTop: 0 }}>Usuários, Perfis e Vínculos (UC02, UC03, RN-06)</h1>
      <Alerta tipo={msg.tipo} mensagem={msg.texto} onFechar={() => setMsg({ tipo: '', texto: '' })} />

      <section className="card" aria-labelledby="sec-novo-usuario">
        <h2 id="sec-novo-usuario" style={{ marginTop: 0 }}>
          {editandoId ? `Editar Usuário #${editandoId}` : 'Cadastrar Usuário'}
        </h2>
        <form onSubmit={criarUsuario}>
          <div className="form-grid">
            <Campo id="u-nome" label="Nome completo" value={nome} onChange={(e) => setNome(e.target.value)} required />
            <Campo id="u-email" label="E-mail" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
            <Campo id="u-senha" label={editandoId ? 'Nova senha (opcional)' : 'Senha inicial (mín. 8 caracteres)'} type="password" value={senha} onChange={(e) => setSenha(e.target.value)} required={!editandoId} minLength={8} autoComplete="new-password" />
            <Campo id="u-perfil" label="Perfil de acesso" as="select" value={perfil} onChange={(e) => setPerfil(e.target.value)} disabled={Boolean(editandoId)}>
              <option value="ALUNO">ALUNO</option>
              <option value="RESPONSAVEL">RESPONSAVEL</option>
              <option value="PROFESSOR">PROFESSOR</option>
              <option value="COORDENADOR">COORDENADOR</option>
              <option value="PROFISSIONAL_APOIO">PROFISSIONAL_APOIO</option>
              <option value="ADMINISTRADOR">ADMINISTRADOR</option>
            </Campo>

            {perfil === 'ALUNO' && (
              <>
                <Campo id="u-mat" label="Código de Matrícula" value={matricula} onChange={(e) => setMatricula(e.target.value)} required />
                <Campo id="u-nasc" label="Data de Nascimento" type="date" value={dataNascimento} onChange={(e) => setDataNascimento(e.target.value)} />
              </>
            )}
            {perfil === 'PROFESSOR' && (
              <>
                <Campo id="u-reg" label="Registro Funcional" value={registroFuncional} onChange={(e) => setRegistroFuncional(e.target.value)} required />
                <Campo id="u-form" label="Formação Acadêmica" value={formacao} onChange={(e) => setFormacao(e.target.value)} />
              </>
            )}
            {perfil === 'RESPONSAVEL' && (
              <>
                <Campo id="u-cpf" label="CPF" value={cpf} onChange={(e) => setCpf(e.target.value)} required />
                <Campo id="u-tel" label="Telefone" value={telefone} onChange={(e) => setTelefone(e.target.value)} />
              </>
            )}
            {perfil === 'COORDENADOR' && (
              <Campo id="u-cargo" label="Cargo" value={cargo} onChange={(e) => setCargo(e.target.value)} />
            )}
            {perfil === 'PROFISSIONAL_APOIO' && (
              <Campo id="u-esp" label="Especialidade" value={especialidade} onChange={(e) => setEspecialidade(e.target.value)} />
            )}
          </div>
          <div style={{ display: 'flex', gap: 10 }}>
            <button type="submit" className="btn btn-primario">
              {editandoId ? 'Atualizar Usuário' : 'Salvar Usuário'}
            </button>
            {editandoId && (
              <button type="button" className="btn btn-secundario" onClick={limparFormulario}>
                Cancelar Edição
              </button>
            )}
          </div>
        </form>
      </section>

      <section className="card" aria-labelledby="sec-vinculo-resp">
        <h2 id="sec-vinculo-resp" style={{ marginTop: 0 }}>Vincular Responsável a Estudante (RN-06)</h2>
        <form onSubmit={vincularResponsavel} className="form-grid" style={{ alignItems: 'end' }}>
          <Campo id="v-aluno" label="Estudante" as="select" value={alunoVinculoId} onChange={(e) => setAlunoVinculoId(e.target.value)} required>
            <option value="">Selecione...</option>
            {alunos.map((a) => (
              <option key={a.id} value={a.id}>{a.nome} ({a.matricula})</option>
            ))}
          </Campo>
          <Campo id="v-resp" label="Responsável" as="select" value={responsavelVinculoId} onChange={(e) => setResponsavelVinculoId(e.target.value)} required>
            <option value="">Selecione...</option>
            {responsaveis.map((r) => (
              <option key={r.id} value={r.id}>{r.nome} ({r.cpf})</option>
            ))}
          </Campo>
          <div>
            <button type="submit" className="btn btn-sucesso">Vincular Responsável</button>
          </div>
        </form>
      </section>

      <section className="card" aria-labelledby="sec-lista-usuarios">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'end', flexWrap: 'wrap', gap: 12, marginBottom: 14 }}>
          <h2 id="sec-lista-usuarios" style={{ margin: 0 }}>Usuários Cadastrados ({usuariosFiltrados.length})</h2>
          <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
            <Campo
              id="busca-usuario"
              label="Buscar por nome, e-mail ou matrícula"
              placeholder="Digite para filtrar..."
              value={buscaTexto}
              onChange={(e) => setBuscaTexto(e.target.value)}
            />
            <Campo id="filtro-perfil" label="Filtrar por perfil" as="select" value={filtroPerfil} onChange={(e) => setFiltroPerfil(e.target.value)}>
              <option value="">Todos os perfis</option>
              <option value="ADMINISTRADOR">ADMINISTRADOR</option>
              <option value="COORDENADOR">COORDENADOR</option>
              <option value="PROFESSOR">PROFESSOR</option>
              <option value="PROFISSIONAL_APOIO">PROFISSIONAL_APOIO</option>
              <option value="ALUNO">ALUNO</option>
              <option value="RESPONSAVEL">RESPONSAVEL</option>
            </Campo>
          </div>
        </div>

        <Tabela
          colunas={['ID', 'Nome', 'E-mail', 'Perfil', 'Detalhes / Vínculos', 'Status', 'Ações']}
          dados={usuariosFiltrados}
          renderLinha={(u) => (
            <tr key={u.id}>
              <td>{u.id}</td>
              <td><strong>{u.nome}</strong></td>
              <td>{u.email}</td>
              <td><span className="badge badge-info">{u.perfil}</span></td>
              <td>
                {u.matricula && `Matrícula: ${u.matricula} `}
                {u.registroFuncional && `Reg: ${u.registroFuncional} `}
                {u.cpf && `CPF: ${u.cpf} `}
                {u.responsaveisIds?.length > 0 && (
                  <div style={{ marginTop: 4, display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                    {u.responsaveisIds.map((rid) => (
                      <span key={rid} className="badge badge-aberta" style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
                        {mapaNomes[rid] ? `Resp: ${mapaNomes[rid]}` : `Resp #${rid}`}
                        <button
                          type="button"
                          title="Remover vínculo"
                          aria-label={`Remover vínculo com responsável ${mapaNomes[rid] || rid}`}
                          style={{ border: 'none', background: 'transparent', cursor: 'pointer', fontWeight: 700, padding: 0 }}
                          onClick={() => removerVinculoResponsavel(u, rid)}
                        >
                          ×
                        </button>
                      </span>
                    ))}
                  </div>
                )}
              </td>
              <td>
                <span className={u.ativo ? 'badge badge-ativa' : 'badge badge-retido'}>
                  {u.ativo ? 'ATIVO' : 'INATIVO'}
                </span>
              </td>
              <td>
                <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                  <button type="button" className="btn btn-primario" onClick={() => iniciarEdicao(u)}>
                    Editar
                  </button>
                  <button type="button" className="btn btn-secundario" onClick={() => alternarAtivo(u)}>
                    {u.ativo ? 'Desativar' : 'Ativar'}
                  </button>
                </div>
              </td>
            </tr>
          )}
        />
      </section>
    </div>
  );
}
