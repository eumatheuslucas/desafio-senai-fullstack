import { useEffect } from 'react';

const SIZES = { md: 'max-w-lg', xl: 'max-w-4xl' };

export default function Modal({ title, onClose, size = 'md', children }) {
  useEffect(() => {
    const closeOnEscape = (event) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };
    document.addEventListener('keydown', closeOnEscape);
    return () => document.removeEventListener('keydown', closeOnEscape);
  }, [onClose]);

  return (
    <div
      className="fixed inset-0 z-40 flex items-start justify-center overflow-y-auto bg-black/50 p-4"
      onMouseDown={(event) => event.target === event.currentTarget && onClose()}
    >
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-title"
        className={`mt-8 w-full ${SIZES[size]} rounded-xl bg-white p-5 shadow-lg sm:p-6`}
      >
        <div className="mb-4 flex items-start justify-between gap-4">
          <h2 id="modal-title" className="text-lg font-semibold">
            {title}
          </h2>
          <button type="button" aria-label="Fechar janela" onClick={onClose} className="rounded px-2 text-xl leading-none">
            ×
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}
