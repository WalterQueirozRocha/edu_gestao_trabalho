import React from 'react';

export function Modal({ aberto, titulo, onFechar, children }) {
  if (!aberto) return null;

  return (
    <div className="modal-backdrop" role="dialog" aria-modal="true" aria-labelledby="modal-titulo">
      <div className="modal-card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <h2 id="modal-titulo" style={{ margin: 0, fontSize: '1.2rem' }}>
            {titulo}
          </h2>
          <button type="button" className="btn btn-secundario" onClick={onFechar}>
            Fechar
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}
