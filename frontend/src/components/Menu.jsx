import React from 'react';
import { NavLink } from 'react-router-dom';

const ITENS_MENU = [
  { to: '/', label: 'Painel Inicial', perfis: ['ADMINISTRADOR', 'COORDENADOR', 'PROFESSOR', 'PROFISSIONAL_APOIO', 'ALUNO', 'RESPONSAVEL'] },
  { to: '/usuarios', label: 'Usuários e Vínculos', perfis: ['ADMINISTRADOR'] },
  { to: '/cadastros-academicos', label: 'Anos, Etapas, Turmas e Disciplinas', perfis: ['ADMINISTRADOR'] },
  { to: '/matriculas', label: 'Matrículas e Histórico', perfis: ['ADMINISTRADOR'] },
  { to: '/diario', label: 'Diário de Classe (Chamada)', perfis: ['PROFESSOR', 'COORDENADOR'] },
  { to: '/avaliacoes', label: 'Avaliações e Notas', perfis: ['PROFESSOR', 'COORDENADOR'] },
  { to: '/resultados-etapa', label: 'Resultados e Fechamento', perfis: ['COORDENADOR', 'ADMINISTRADOR'] },
  { to: '/boletim', label: 'Boletim Escolar', perfis: ['ALUNO', 'RESPONSAVEL', 'COORDENADOR', 'ADMINISTRADOR'] },
  { to: '/comunicados', label: 'Comunicados', perfis: ['ADMINISTRADOR', 'COORDENADOR', 'PROFESSOR', 'PROFISSIONAL_APOIO', 'ALUNO', 'RESPONSAVEL'] },
  { to: '/ocorrencias-denuncias', label: 'Registrar Ocorrência / Denúncia', perfis: ['PROFESSOR', 'ALUNO', 'RESPONSAVEL', 'COORDENADOR'] },
  { to: '/tratamento-ocorrencias', label: 'Tratar Ocorrências e Denúncias', perfis: ['COORDENADOR', 'ADMINISTRADOR'] },
  { to: '/planos-pedagogicos', label: 'Plano Pedagógico (PDI)', perfis: ['COORDENADOR', 'PROFISSIONAL_APOIO', 'ADMINISTRADOR'] },
  { to: '/alertas-ia', label: 'Alertas de Risco (IA)', perfis: ['COORDENADOR', 'ADMINISTRADOR'] }
];

export function Menu({ perfil }) {
  const visiveis = ITENS_MENU.filter((item) => item.perfis.includes(perfil));

  return (
    <nav aria-label="Menu principal do sistema">
      <ul className="nav-list">
        {visiveis.map((item) => (
          <li key={item.to}>
            <NavLink
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) => (isActive ? 'nav-link ativo' : 'nav-link')}
            >
              {item.label}
            </NavLink>
          </li>
        ))}
      </ul>
    </nav>
  );
}
