const STYLES = {
  success: { box: 'border-success-border bg-success-soft text-success-text', title: 'Sucesso' },
  error: { box: 'border-danger-border bg-danger-soft text-danger-text', title: 'Erro' },
  warning: { box: 'border-warning-border bg-warning-soft text-warning-text', title: 'Atenção' },
};

export default function Alert({ type = 'error', title, children, onClose }) {
  const style = STYLES[type];
  return (
    <div role={type === 'error' ? 'alert' : 'status'} className={`rounded-lg border p-3 text-sm shadow-sm ${style.box}`}>
      <div className="flex items-start justify-between gap-3">
        <p className="font-medium">{title ?? style.title}</p>
        {onClose && (
          <button type="button" aria-label="Fechar aviso" onClick={onClose} className="leading-none">
            ×
          </button>
        )}
      </div>
      <div className="mt-1">{children}</div>
    </div>
  );
}
