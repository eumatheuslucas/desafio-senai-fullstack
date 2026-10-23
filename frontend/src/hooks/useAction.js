import { useCallback, useRef, useState } from 'react';
import { useToast } from '../components/Toasts.jsx';

export function useAction() {
  const toast = useToast();
  const busy = useRef(false);
  const [pending, setPending] = useState(false);

  const run = useCallback(
    async (action, { success, onSuccess } = {}) => {
      if (busy.current) {
        return false;
      }
      busy.current = true;
      setPending(true);
      try {
        const result = await action();
        if (success) {
          toast.success(typeof success === 'function' ? success(result) : success);
        }
        if (onSuccess) {
          onSuccess(result);
        }
        return true;
      } catch (error) {
        toast.error(error.message);
        return false;
      } finally {
        busy.current = false;
        setPending(false);
      }
    },
    [toast],
  );

  return { run, pending };
}
