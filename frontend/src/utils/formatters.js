export const ROTULO_PERFIL = {
  ADMINISTRADOR: 'Administrador',
  COORDENADOR: 'Coordenação Pedagógica',
  PROFESSOR: 'Professor(a)',
  PROFISSIONAL_APOIO: 'Profissional de Apoio',
  ALUNO: 'Estudante',
  RESPONSAVEL: 'Responsável Legal'
};

export function formatarData(isoDate) {
  if (!isoDate) return '—';
  const partes = String(isoDate).substring(0, 10).split('-');
  if (partes.length !== 3) return isoDate;
  return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

export function formatarDataHora(isoDateTime) {
  if (!isoDateTime) return '—';
  const texto = String(isoDateTime).replace('T', ' ');
  const data = formatarData(texto.substring(0, 10));
  const hora = texto.length >= 16 ? texto.substring(11, 16) : '';
  return hora ? `${data} às ${hora}` : data;
}

export function obterIniciais(nome) {
  if (!nome) return 'EG';
  const limpo = nome.replace(/\(.*?\)/g, '').trim();
  const partes = limpo.split(/\s+/).filter(Boolean);
  if (partes.length === 1) return partes[0].substring(0, 2).toUpperCase();
  return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
}
