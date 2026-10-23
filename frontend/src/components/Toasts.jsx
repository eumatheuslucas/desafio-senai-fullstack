import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import Alert from './Alert.jsx';

const ToastContext = createContext(null);
const TOAST_DURATION_MS = 7000;
let nextToastId = 1;

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const dismiss = useCallback((id) => setToasts((list) => list.filter((toast) => toast.id !== id)), []);

  const push = useCallback(
    (type, message) => {
      const id = nextToastId++;
      setToasts((list) => [...list, { id, type, message }]);
      setTimeout(() => dismiss(id), TOAST_DURATION_MS);
    },
    [dismiss],
  );

  const api = useMemo(
    () => ({ success: (message) => push('success', message), error: (message) => push('error', message) }),
    [push],
  );

  return (
    <ToastContext.Provider value={api}>
      {children}
      <div className="fixed right-4 top-4 z-[60] flex w-96 max-w-[calc(100vw-2rem)] flex-col gap-2" aria-live="polite">
        {toasts.map((toast) => (
          <Alert key={toast.id} type={toast.type} onClose={() => dismiss(toast.id)}>
            {toast.message}
          </Alert>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  return useContext(ToastContext);
}
