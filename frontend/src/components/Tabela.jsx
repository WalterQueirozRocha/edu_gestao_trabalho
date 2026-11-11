import React from 'react';

export function Tabela({ caption, colunas, dados, renderLinha, mensagemVazia = 'Nenhum registro encontrado.' }) {
  return (
    <div className="tabela-wrapper">
      <table className="tabela">
        {caption && <caption style={{ textAlign: 'left', fontWeight: 700, marginBottom: 8 }}>{caption}</caption>}
        <thead>
          <tr>
            {colunas.map((col, idx) => (
              <th key={idx} scope="col">
                {col}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {dados.length === 0 ? (
            <tr>
              <td colSpan={colunas.length}>{mensagemVazia}</td>
            </tr>
          ) : (
            dados.map(renderLinha)
          )}
        </tbody>
      </table>
    </div>
  );
}
