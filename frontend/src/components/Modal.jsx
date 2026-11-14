import React, { useEffect, useRef } from 'react';

export function Modal({ aberto, titulo, onFechar, children }) {
  const cardRef = useRef(null);

  // RNF-04 (WCAG 2.2): foco entra no modal, Esc fecha e o foco volta ao elemento de origem
  useEffect(() => {
    if (!aberto) return undefined;
    const origem = document.activeElement;
    cardRef.current?.focus();
    function aoPressionar(e) {
      if (e.key === 'Escape') onFechar();
    }
    document.addEventListener('keydown', aoPressionar);
    return () => {
      document.removeEventListener('keydown', aoPressionar);
      origem?.focus?.();
    };
  }, [aberto]);

  if (!aberto) return null;

  return (
    <div className="modal-backdrop" role="dialog" aria-modal="true" aria-labelledby="modal-titulo">
      <div className="modal-card" ref={cardRef} tabIndex={-1}>
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
