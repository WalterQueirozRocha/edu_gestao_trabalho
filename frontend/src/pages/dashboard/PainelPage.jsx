import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';

const CARDS_POR_PERFIL = {
  ADMINISTRADOR: [
    { titulo: 'Usuários e Vínculos (UC02/UC03)', desc: 'Gerencie contas, perfis RBAC e vínculos responsável-estudante (RN-06).', link: '/usuarios' },
    { titulo: 'Estrutura Acadêmica (UC02)', desc: 'Cadastre anos letivos, etapas, turmas, disciplinas e professores.', link: '/cadastros-academicos' },
    { titulo: 'Matrículas (UC04)', desc: 'Realize matrículas com validação de unicidade ativa (RN-01) e histórico (RN-07).', link: '/matriculas' }
  ],
  COORDENADOR: [
    { titulo: 'Resultados e Etapas (UC09/UC10)', desc: 'Revise médias e frequência da turma, feche/publique etapas e autorize alterações (RN-04).', link: '/resultados-etapa' },
    { titulo: 'Alertas de Risco IA (UC14/UC15/UC18)', desc: 'Analise predições de retenção/evasão e converta sugestões em planos pedagógicos.', link: '/alertas-ia' },
    { titulo: 'Plano Pedagógico Inclusivo (UC13)', desc: 'Registre metas, adaptações e estratégias sem dados clínicos (RN-08).', link: '/planos-pedagogicos' },
    { titulo: 'Tratar Ocorrências e Denúncias (UC12)', desc: 'Analise, encaminhe ou arquive denúncias com acesso restrito (RF-09).', link: '/tratamento-ocorrencias' },
    { titulo: 'Comunicados (UC11)', desc: 'Publique avisos segmentados por perfil e turma.', link: '/comunicados' }
  ],
  PROFESSOR: [
    { titulo: 'Diário de Classe (UC05)', desc: 'Registre aulas e chamada diária com cálculo imediato de frequência (RN-02).', link: '/diario' },
    { titulo: 'Avaliações e Notas (UC06)', desc: 'Cadastre avaliações com pesos e lance notas da etapa (RN-03/RN-04).', link: '/avaliacoes' },
    { titulo: 'Ocorrências e Denúncias (UC07)', desc: 'Registre ocorrências pedagógicas/disciplinares ou denúncias.', link: '/ocorrencias-denuncias' }
  ],
  PROFISSIONAL_APOIO: [
    { titulo: 'Acompanhar Planos Pedagógicos (UC08)', desc: 'Consulte metas, adaptações e estratégias dos estudantes autorizados (RN-08).', link: '/planos-pedagogicos' },
    { titulo: 'Comunicados', desc: 'Visualize os avisos institucionais publicados pela escola.', link: '/comunicados' }
  ],
  ALUNO: [
    { titulo: 'Meu Boletim Escolar (UC16)', desc: 'Consulte notas, médias, frequência e imprima boletins publicados (RN-05).', link: '/boletim' },
    { titulo: 'Comunicados (UC17)', desc: 'Acompanhe avisos da coordenação para sua turma.', link: '/comunicados' },
    { titulo: 'Canal de Denúncias (UC07)', desc: 'Registre relatos (inclusive anônimos) e acompanhe pelo protocolo.', link: '/ocorrencias-denuncias' }
  ],
  RESPONSAVEL: [
    { titulo: 'Boletim dos Estudantes Vinculados (UC16)', desc: 'Acompanhe notas e frequência dos estudantes sob sua responsabilidade (RN-05/RN-06).', link: '/boletim' },
    { titulo: 'Comunicados (UC17)', desc: 'Leia avisos e convocações de reuniões pedagógicas.', link: '/comunicados' },
    { titulo: 'Canal de Denúncias (UC07)', desc: 'Registre ocorrências/denúncias e consulte protocolos.', link: '/ocorrencias-denuncias' }
  ]
};

export function PainelPage() {
  const { usuario } = useAuth();
  const cards = CARDS_POR_PERFIL[usuario?.perfil] || [];

  return (
    <div>
      <h1 style={{ marginTop: 0 }}>Painel Inicial</h1>
      <p style={{ color: 'var(--cor-texto-secundario)', marginBottom: 24 }}>
        Bem-vindo(a), <strong>{usuario?.nome}</strong>. Selecione abaixo uma funcionalidade compatível com seu perfil ({usuario?.perfil}).
      </p>

      <div className="grid-cards">
        {cards.map((c) => (
          <article key={c.link} className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <div>
              <h2 style={{ marginTop: 0, fontSize: '1.1rem', color: 'var(--cor-primaria)' }}>{c.titulo}</h2>
              <p style={{ fontSize: '0.92rem', color: 'var(--cor-texto-secundario)' }}>{c.desc}</p>
            </div>
            <div style={{ marginTop: 12 }}>
              <Link to={c.link} className="btn btn-primario">
                Acessar módulo
              </Link>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
