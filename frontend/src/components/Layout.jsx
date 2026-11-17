import React from 'react';
import { Outlet } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { Menu } from './Menu';
import { ROTULO_PERFIL, obterIniciais } from '../utils/formatters';

export function Layout() {
  const { usuario, logout } = useAuth();

  return (
    <>
      <a href="#conteudo-principal" className="skip-link">
        Pular para o conteúdo principal
      </a>
      <div className="app-shell">
        <aside className="sidebar">
          <div className="sidebar-header">
            <div className="sidebar-logo" aria-hidden="true">EG</div>
            <div>
              <div className="sidebar-brand">EduGestão</div>
              <div className="sidebar-sub">Gestão Acadêmica &amp; IA</div>
            </div>
          </div>
          {usuario && <Menu perfil={usuario.perfil} />}
          <div className="sidebar-footer">
            UCSAL — Engenharia de Software<br />
            Conformidade LGPD &amp; WCAG 2.2 AA
          </div>
        </aside>
        <div className="main-area">
          <header className="topbar">
            <div className="topbar-user">
              <span className="avatar-circle" aria-hidden="true">
                {obterIniciais(usuario?.nome)}
              </span>
              <div>
                <div style={{ fontWeight: 700, lineHeight: 1.2 }}>{usuario?.nome}</div>
                <div style={{ fontSize: '0.8rem', color: 'var(--cor-texto-secundario)' }}>
                  { usuario?.email }
                </div>
              </div>
              <span className="badge badge-info" style={{ marginLeft: 4 }}>
                {ROTULO_PERFIL[usuario?.perfil] || usuario?.perfil}
              </span>
            </div>
            <button type="button" className="btn btn-secundario btn-sm" onClick={logout}>
              Sair da conta
            </button>
          </header>
          <main id="conteudo-principal" className="content-container" tabIndex="-1">
            <Outlet />
          </main>
        </div>
      </div>
    </>
  );
}
