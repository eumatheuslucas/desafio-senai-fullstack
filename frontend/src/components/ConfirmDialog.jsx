import Button from './Button.jsx';
import Modal from './Modal.jsx';

export default function ConfirmDialog({ title, message, confirmLabel, destructive = false, pending, onConfirm, onCancel }) {
  return (
    <Modal title={title} onClose={onCancel}>
      <p className="text-sm text-muted-fg">{message}</p>
      <div className="mt-6 flex justify-end gap-2">
        <Button onClick={onCancel}>Cancelar</Button>
        <Button variant={destructive ? 'destructive' : 'primary'} loading={pending} onClick={onConfirm}>
          {confirmLabel}
        </Button>
      </div>
    </Modal>
  );
}
