import React, { createContext, useContext, useState } from 'react';
import { apiFetch } from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(() => {
    const salvo = localStorage.getItem('edugestao_usuario');
    return salvo ? JSON.parse(salvo) : null;
  });

  async function login(email, senha) {
    const resp = await apiFetch('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, senha })
    });
    localStorage.setItem('edugestao_token', resp.token);
    localStorage.setItem('edugestao_usuario', JSON.stringify(resp.usuario));
    setUsuario(resp.usuario);
    return resp.usuario;
  }

  function logout() {
    localStorage.removeItem('edugestao_token');
    localStorage.removeItem('edugestao_usuario');
    setUsuario(null);
  }

  return (
    <AuthContext.Provider value={{ usuario, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
