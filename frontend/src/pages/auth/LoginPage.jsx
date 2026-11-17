import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import { Campo } from '../../components/Campo';
import { Alerta } from '../../components/Alerta';

const CONTAS_DEMO = [
  { label: 'Administrador', sub: 'Usuários, turmas e matrículas', email: 'admin@edugestao.ucsal.br' },
  { label: 'Coordenação', sub: 'Etapas, IA, PDI e denúncias', email: 'coordenacao@edugestao.ucsal.br' },
  { label: 'Prof. Matemática', sub: 'Marcos Lima (Diário e notas)', email: 'marcos.lima@edugestao.ucsal.br' },
  { label: 'Profa. Português', sub: 'Fernanda Souza (Diário e notas)', email: 'fernanda.souza@edugestao.ucsal.br' },
  { label: 'Apoio Pedagógico', sub: 'Acompanhamento de PDI (AEE)', email: 'apoio@edugestao.ucsal.br' },
  { label: 'Responsável', sub: 'Roberto Alves (2 filhos)', email: 'roberto.alves@email.com' },
  { label: 'Estudante', sub: 'Lucas Alves (9º Ano A)', email: 'lucas.alves@aluno.ucsal.br' }
];

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('coordenacao@edugestao.ucsal.br');
  const [senha, setSenha] = useState('123456');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  async function autenticarComCredenciais(emailAlvo, senhaAlvo) {
    setErro('');
    setCarregando(true);
    try {
      await login(emailAlvo, senhaAlvo);
      navigate('/');
    } catch (err) {
      setErro(err.message);
    } finally {
      setCarregando(false);
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    await autenticarComCredenciais(email, senha);
  }

  return (
    <main id="conteudo-principal" className="login-shell">
      <section className="login-hero" aria-label="Apresentação institucional">
        <div>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: 10, marginBottom: 24 }}>
            <div className="sidebar-logo" aria-hidden="true">EG</div>
            <span style={{ fontWeight: 800, fontSize: '1.25rem', letterSpacing: '-0.02em' }}>
              EduGestão UCSAL
            </span>
          </div>
          <h1 style={{ color: '#ffffff', fontSize: '2.1rem', marginTop: 0, marginBottom: 14, lineHeight: 1.2 }}>
            Gestão Acadêmica, Acompanhamento Inclusivo e Predição de Risco por IA
          </h1>
          <p style={{ color: '#e2e8f0', fontSize: '1.02rem', maxWidth: 520, marginBottom: 28 }}>
            Plataforma integrada para o Ensino Fundamental e Médio com diário eletrônico por horas,
            fechamento de etapas, planos pedagógicos individualizados (PDI) e alertas preventivos de
            retenção e evasão com Oracle Tribuo.
          </p>

          <div style={{ display: 'grid', gap: 12, maxWidth: 520 }}>
            <div style={{ background: 'rgba(255,255,255,0.1)', padding: '12px 16px', borderRadius: 8, border: '1px solid rgba(255,255,255,0.16)' }}>
              <strong>IA de Apoio à Decisão (RF-11 / RN-09)</strong>
              <div style={{ fontSize: '0.86rem', color: '#e2e8f0', marginTop: 2 }}>
                Regressão logística com fatores explicáveis e sugestões pedagógicas sem diagnóstico clínico.
              </div>
            </div>
            <div style={{ background: 'rgba(255,255,255,0.1)', padding: '12px 16px', borderRadius: 8, border: '1px solid rgba(255,255,255,0.16)' }}>
              <strong>Segurança RBAC, LGPD e Acessibilidade WCAG 2.2 AA</strong>
              <div style={{ fontSize: '0.86rem', color: '#e2e8f0', marginTop: 2 }}>
                6 perfis de acesso, vínculo familiar restrito (RN-06) e canal de denúncias com protocolo.
              </div>
            </div>
          </div>
        </div>

        <div style={{ fontSize: '0.82rem', color: '#cbd5e1', marginTop: 32 }}>
          Tópicos Avançados em Programação — Engenharia de Software (UCSAL)
        </div>
      </section>

      <section className="login-form-pane" aria-label="Formulário de autenticação">
        <div className="card" style={{ maxWidth: 480, width: '100%', marginBottom: 0 }}>
          <h2 style={{ marginTop: 0, fontSize: '1.35rem', color: 'var(--cor-primaria)' }}>
            Acessar Plataforma (UC01)
          </h2>
          <p style={{ color: 'var(--cor-texto-secundario)', marginTop: 4, marginBottom: 18, fontSize: '0.92rem' }}>
            Entre com seu e-mail institucional ou selecione um perfil de demonstração abaixo.
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
            <button type="submit" className="btn btn-primario" disabled={carregando} style={{ padding: '11px 16px' }}>
              {carregando ? 'Autenticando...' : 'Entrar na Plataforma'}
            </button>
          </form>

          <hr style={{ margin: '20px 0 14px', border: 'none', borderTop: '1px solid var(--cor-borda-suave)' }} />

          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
              <strong style={{ fontSize: '0.84rem', color: 'var(--cor-texto)' }}>
                Perfis de demonstração (clique para preencher ou entrar):
              </strong>
              <span style={{ fontSize: '0.76rem', color: 'var(--cor-texto-secundario)' }}>
                Senha: <code>123456</code>
              </span>
            </div>
            <div className="demo-grid">
              {CONTAS_DEMO.map((c) => {
                const ativo = email === c.email;
                return (
                  <button
                    key={c.email}
                    type="button"
                    className={ativo ? 'demo-btn selecionado' : 'demo-btn'}
                    disabled={carregando}
                    onClick={() => {
                      setEmail(c.email);
                      setSenha('123456');
                      autenticarComCredenciais(c.email, '123456');
                    }}
                  >
                    <strong style={{ fontSize: '0.84rem', color: 'var(--cor-primaria)' }}>{c.label}</strong>
                    <span style={{ fontSize: '0.74rem', color: 'var(--cor-texto-secundario)' }}>{c.sub}</span>
                  </button>
                );
              })}
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}
