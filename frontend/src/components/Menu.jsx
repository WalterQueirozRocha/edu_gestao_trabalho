import React from 'react';
import { NavLink } from 'react-router-dom';

const GRUPOS_MENU = [
  {
    secao: 'Visão Geral',
    itens: [
      {
        to: '/',
        label: 'Painel Inicial',
        icone: 'M3 10.5L12 3l9 7.5V20a1 1 0 0 1-1 1h-5.5v-6.5h-5V21H4a1 1 0 0 1-1-1v-9.5z',
        perfis: ['ADMINISTRADOR', 'COORDENADOR', 'PROFESSOR', 'PROFISSIONAL_APOIO', 'ALUNO', 'RESPONSAVEL']
      },
      {
        to: '/comunicados',
        label: 'Mural de Comunicados',
        icone: 'M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0',
        perfis: ['ADMINISTRADOR', 'COORDENADOR', 'PROFESSOR', 'PROFISSIONAL_APOIO', 'ALUNO', 'RESPONSAVEL']
      }
    ]
  },
  {
    secao: 'Administração',
    itens: [
      {
        to: '/usuarios',
        label: 'Usuários e Vínculos',
        icone: 'M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM22 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75',
        perfis: ['ADMINISTRADOR']
      },
      {
        to: '/cadastros-academicos',
        label: 'Estrutura Acadêmica',
        icone: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5',
        perfis: ['ADMINISTRADOR']
      },
      {
        to: '/matriculas',
        label: 'Matrículas e Histórico',
        icone: 'M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8zM14 2v6h6M16 13H8M16 17H8M10 9H8',
        perfis: ['ADMINISTRADOR']
      }
    ]
  },
  {
    secao: 'Ensino & Avaliação',
    itens: [
      {
        to: '/diario',
        label: 'Diário de Classe',
        icone: 'M8 2v4M16 2v4M3 10h18M5 4h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2zM9 16l2 2 4-4',
        perfis: ['PROFESSOR', 'COORDENADOR']
      },
      {
        to: '/avaliacoes',
        label: 'Avaliações e Notas',
        icone: 'M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z',
        perfis: ['PROFESSOR', 'COORDENADOR']
      },
      {
        to: '/resultados-etapa',
        label: 'Resultados e Fechamento',
        icone: 'M18 20V10M12 20V4M6 20v-6',
        perfis: ['COORDENADOR', 'ADMINISTRADOR']
      },
      {
        to: '/boletim',
        label: 'Boletim Escolar',
        icone: 'M4 19.5A2.5 2.5 0 0 1 6.5 17H20M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z',
        perfis: ['ALUNO', 'RESPONSAVEL', 'COORDENADOR', 'ADMINISTRADOR']
      }
    ]
  },
  {
    secao: 'Acompanhamento & IA',
    itens: [
      {
        to: '/alertas-ia',
        label: 'Alertas de Risco (IA)',
        icone: 'M12 2a4 4 0 0 1 4 4v1a3 3 0 0 1 3 3v2a3 3 0 0 1-3 3v1a4 4 0 0 1-8 0v-1a3 3 0 0 1-3-3v-2a3 3 0 0 1 3-3V6a4 4 0 0 1 4-4zM9 10h.01M15 10h.01M9.5 14a3.5 3.5 0 0 0 5 0',
        perfis: ['COORDENADOR', 'ADMINISTRADOR'],
        destaqueIa: true
      },
      {
        to: '/planos-pedagogicos',
        label: 'Plano Pedagógico (PDI)',
        icone: 'M9 11l3 3L22 4M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11',
        perfis: ['COORDENADOR', 'PROFISSIONAL_APOIO', 'ADMINISTRADOR']
      },
      {
        to: '/ocorrencias-denuncias',
        label: 'Ocorrências e Denúncias',
        icone: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10zM12 8v4M12 16h.01',
        perfis: ['PROFESSOR', 'ALUNO', 'RESPONSAVEL', 'COORDENADOR']
      },
      {
        to: '/tratamento-ocorrencias',
        label: 'Triagem de Casos',
        icone: 'M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2',
        perfis: ['COORDENADOR', 'ADMINISTRADOR']
      }
    ]
  }
];

export function Menu({ perfil }) {
  const gruposVisiveis = GRUPOS_MENU.map((grupo) => ({
    ...grupo,
    itens: grupo.itens.filter((item) => item.perfis.includes(perfil))
  })).filter((g) => g.itens.length > 0);

  return (
    <nav aria-label="Menu principal do sistema" className="sidebar-nav">
      {gruposVisiveis.map((grupo) => (
        <div key={grupo.secao} className="nav-group">
          <div className="nav-group-label">{grupo.secao}</div>
          <ul className="nav-list">
            {grupo.itens.map((item) => (
              <li key={item.to}>
                <NavLink
                  to={item.to}
                  end={item.to === '/'}
                  className={({ isActive }) =>
                    `nav-link${isActive ? ' ativo' : ''}${item.destaqueIa ? ' nav-link-ia' : ''}`
                  }
                >
                  <svg
                    className="nav-icon"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    aria-hidden="true"
                  >
                    <path d={item.icone} />
                  </svg>
                  <span>{item.label}</span>
                  {item.destaqueIa && <span className="nav-ia-pill">IA</span>}
                </NavLink>
              </li>
            ))}
          </ul>
        </div>
      ))}
    </nav>
  );
}
