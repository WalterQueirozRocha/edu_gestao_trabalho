import React from 'react';

export function Alerta({ tipo = 'info', mensagem, onFechar }) {
  if (!mensagem) return null;
  const classe =
    tipo === 'erro'
      ? 'alerta-box alerta-erro'
      : tipo === 'sucesso'
      ? 'alerta-box alerta-sucesso'
      : 'alerta-box alerta-info';

  return (
    <div className={classe} role="alert" aria-live="polite">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 12 }}>
        <span>{mensagem}</span>
        {onFechar && (
          <button
            type="button"
            className="btn btn-secundario"
            style={{ padding: '2px 8px' }}
            onClick={onFechar}
            aria-label="Fechar aviso"
          >
            ×
          </button>
        )}
      </div>
    </div>
  );
}
