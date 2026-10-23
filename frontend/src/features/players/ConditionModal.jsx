import { useState } from 'react';
import { changeCondition } from '../../api/players.js';
import Button from '../../components/Button.jsx';
import { SelectInput } from '../../components/Field.jsx';
import Modal from '../../components/Modal.jsx';
import { useAction } from '../../hooks/useAction.js';
import { CONDITION_LABELS } from '../../lib/constants.js';
import { formatMoney } from '../../lib/format.js';

export default function ConditionModal({ player, onClose, onSaved }) {
  const [condition, setCondition] = useState(player.condition);
  const { run, pending } = useAction();
  const isSick = player.condition === 'DOENTE';

  const save = (event) => {
    event.preventDefault();
    run(() => changeCondition(player.id, condition), {
      success: (updated) => `Condição atualizada. Novo valor de mercado: ${formatMoney(updated.market_value)}.`,
      onSuccess: () => {
        onSaved();
        onClose();
      },
    });
  };

  return (
    <Modal title={`Alterar condição de ${player.name}`} onClose={onClose}>
      <form onSubmit={save} className="flex flex-col gap-4">
        <SelectInput
          id="condition-select"
          label="Condição física"
          value={condition}
          onChange={(event) => setCondition(event.target.value)}
          hint={isSick ? 'Jogador doente passa antes por “Mal condicionado”; ele não volta direto para “Apto”.' : undefined}
        >
          {Object.entries(CONDITION_LABELS).map(([value, label]) => (
            <option key={value} value={value} disabled={isSick && value === 'APTO'}>
              {label}
            </option>
          ))}
        </SelectInput>
        <div className="flex justify-end gap-2">
          <Button onClick={onClose}>Cancelar</Button>
          <Button type="submit" variant="primary" loading={pending}>
            Salvar condição
          </Button>
        </div>
      </form>
    </Modal>
  );
}
