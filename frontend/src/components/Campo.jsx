import React from 'react';

export function Campo({ id, label, as = 'input', children, ...props }) {
  const Componente = as;
  const classe =
    as === 'select'
      ? 'campo-select'
      : as === 'textarea'
      ? 'campo-textarea'
      : 'campo-input';

  return (
    <div className="campo-grupo">
      <label htmlFor={id} className="campo-label">
        {label}
      </label>
      <Componente id={id} className={classe} {...props}>
        {children}
      </Componente>
    </div>
  );
}
