import { useState } from 'react';
import { buyPlayer } from '../../api/players.js';
import Button from '../../components/Button.jsx';
import { SelectInput, TextInput } from '../../components/Field.jsx';
import Modal from '../../components/Modal.jsx';
import ResultList from '../../components/ResultList.jsx';
import { useAction } from '../../hooks/useAction.js';
import { useForm } from '../../hooks/useForm.js';
import { formatDateTime, formatMoney, toApiDateTime } from '../../lib/format.js';
import { checkChoice, collectErrors } from '../../lib/validators.js';
import { useClubs } from '../clubs/ClubsContext.jsx';

const validate = (values) =>
  collectErrors({
    clubId: checkChoice(values.clubId, 'o clube comprador'),
    loyaltyUntil: checkChoice(values.loyaltyUntil, 'o fim da fidelidade'),
  });

export default function BuyModal({ player, onClose, onBought }) {
  const { clubs } = useClubs();
  const { run, pending } = useAction();
  const [receipt, setReceipt] = useState(null);
  const form = useForm({ clubId: '', loyaltyUntil: '' }, validate);

  const buy = (values) =>
    run(
      () =>
        buyPlayer(player.id, {
          club_id: Number(values.clubId),
          loyalty_until: toApiDateTime(values.loyaltyUntil),
        }),
      {
        success: 'Compra concluída.',
        onSuccess: (result) => {
          setReceipt(result);
          onBought();
        },
      },
    );

  if (receipt) {
    return (
      <Modal title="Compra concluída" onClose={onClose}>
        <ResultList
          items={[
            ['Valor', formatMoney(receipt.value)],
            ['Taxa', formatMoney(receipt.fee)],
            ['Custo total', formatMoney(receipt.value + receipt.fee)],
            ['Fim da fidelidade', formatDateTime(receipt.contract.loyalty_until)],
            ['Novo caixa', formatMoney(receipt.club_cash)],
          ]}
        />
        <div className="mt-6 flex justify-end">
          <Button variant="primary" onClick={onClose}>
            Fechar
          </Button>
        </div>
      </Modal>
    );
  }

  return (
    <Modal title={`Comprar ${player.name}`} onClose={onClose}>
      <form noValidate onSubmit={form.submit(buy)} className="flex flex-col gap-3">
        <p className="text-sm text-muted-fg">
          Valor de mercado atual: {formatMoney(player.market_value)} moedas. A API calcula a taxa e confere o limite do caixa.
        </p>
        <SelectInput
          id="buy-club"
          label="Clube comprador"
          value={form.values.clubId}
          error={form.errors.clubId}
          onChange={(event) => form.setField('clubId', event.target.value)}
        >
          <option value="">Selecione</option>
          {clubs.map((club) => (
            <option key={club.id} value={club.id}>
              {club.name}
            </option>
          ))}
        </SelectInput>
        <TextInput
          id="buy-loyalty"
          label="Fim da fidelidade"
          type="datetime-local"
          hint="Horário de Brasília (UTC-3)."
          value={form.values.loyaltyUntil}
          error={form.errors.loyaltyUntil}
          onChange={(event) => form.setField('loyaltyUntil', event.target.value)}
        />
        <div className="mt-2 flex justify-end gap-2">
          <Button onClick={onClose}>Cancelar</Button>
          <Button type="submit" variant="primary" loading={pending}>
            Confirmar compra
          </Button>
        </div>
      </form>
    </Modal>
  );
}
