import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import { Campo } from '../../components/Campo';
import { Alerta } from '../../components/Alerta';

const CONTAS_DEMO = [
  { label: 'Administrador', email: 'admin@edugestao.ucsal.br' },
  { label: 'Coordenadora', email: 'coordenacao@edugestao.ucsal.br' },
  { label: 'Professor (Matemática)', email: 'marcos.lima@edugestao.ucsal.br' },
  { label: 'Profissional de Apoio', email: 'apoio@edugestao.ucsal.br' },
  { label: 'Responsável', email: 'roberto.alves@email.com' },
  { label: 'Aluno (Lucas)', email: 'lucas.alves@aluno.ucsal.br' }
];

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('admin@edugestao.ucsal.br');
  const [senha, setSenha] = useState('123456');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setErro('');
    setCarregando(true);
    try {
      await login(email, senha);
      navigate('/');
    } catch (err) {
      setErro(err.message);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <main
      id="conteudo-principal"
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 16
      }}
    >
      <div className="card" style={{ maxWidth: 460, width: '100%' }}>
        <h1 style={{ marginTop: 0, color: 'var(--cor-primaria)' }}>EduGestão</h1>
        <p style={{ color: 'var(--cor-texto-secundario)', marginBottom: 20 }}>
          Sistema de Gestão Acadêmica e Acompanhamento Escolar Inclusivo (UC01)
        </p>

        <Alerta tipo="erro" mensagem={erro} onFechar={() => setErro('')} />

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          <Campo
            id="login-email"
            label="E-mail institucional"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <Campo
            id="login-senha"
            label="Senha"
            type="password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            required
          />
          <button type="submit" className="btn btn-primario" disabled={carregando}>
            {carregando ? 'Autenticando...' : 'Entrar na Plataforma'}
          </button>
        </form>

        <hr style={{ margin: '20px 0', borderColor: 'var(--cor-borda)' }} />
        <div>
          <strong style={{ fontSize: '0.88rem', display: 'block', marginBottom: 8 }}>
            Acesso rápido de demonstração (senha padrão: 123456):
          </strong>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {CONTAS_DEMO.map((c) => (
              <button
                key={c.email}
                type="button"
                className="btn btn-secundario"
                style={{ fontSize: '0.8rem', padding: '5px 10px' }}
                onClick={() => {
                  setEmail(c.email);
                  setSenha('123456');
                }}
              >
                {c.label}
              </button>
            ))}
          </div>
        </div>
      </div>
    </main>
  );
}
