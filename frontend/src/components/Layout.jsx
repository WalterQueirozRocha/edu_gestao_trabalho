import React from 'react';
import { Outlet } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { Menu } from './Menu';

export function Layout() {
  const { usuario, logout } = useAuth();

  return (
    <>
      <a href="#conteudo-principal" className="skip-link">
        Pular para o conteúdo principal
      </a>
      <div className="app-shell">
        <aside className="sidebar">
          <div className="sidebar-brand">EduGestão</div>
          <div className="sidebar-sub">Gestão Acadêmica e Acompanhamento</div>
          {usuario && <Menu perfil={usuario.perfil} />}
        </aside>
        <div className="main-area">
          <header className="topbar">
            <div>
              <strong>{usuario?.nome}</strong>{' '}
              <span className="badge badge-publicada" style={{ marginLeft: 8 }}>
                {usuario?.perfil}
              </span>
            </div>
            <button type="button" className="btn btn-secundario" onClick={logout}>
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
