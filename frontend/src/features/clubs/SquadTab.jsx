import { useState } from 'react';
import { sellPlayer } from '../../api/players.js';
import Button from '../../components/Button.jsx';
import ConditionBadge from '../../components/ConditionBadge.jsx';
import ConfirmDialog from '../../components/ConfirmDialog.jsx';
import DataState from '../../components/DataState.jsx';
import DataTable from '../../components/DataTable.jsx';
import Modal from '../../components/Modal.jsx';
import ResultList from '../../components/ResultList.jsx';
import { useAction } from '../../hooks/useAction.js';
import { formatDateTime, formatDays, formatMoney, formatSigned } from '../../lib/format.js';

export default function SquadTab({ club, onChanged }) {
  const [target, setTarget] = useState(null);
  const [receipt, setReceipt] = useState(null);
  const { run, pending } = useAction();

  const confirmSale = () =>
    run(() => sellPlayer(target.id), {
      success: 'Venda concluída.',
      onSuccess: (result) => {
        setReceipt(result);
        setTarget(null);
        onChanged();
      },
    });

  const columns = [
    { header: 'Jogador', render: (player) => <span className="font-medium">{player.name}</span> },
    { header: 'Posição', render: (player) => player.position },
    { header: 'Condição', render: (player) => <ConditionBadge condition={player.condition} /> },
    { header: 'Valor de mercado', align: 'right', render: (player) => formatMoney(player.market_value) },
    { header: 'Fim da fidelidade', render: (player) => formatDateTime(player.contract.loyalty_until) },
    { header: 'Dias restantes', align: 'right', render: (player) => formatDays(player.contract.remaining_days) },
    { header: 'Multa se vendido agora', align: 'right', render: (player) => `${player.contract.penalty_rate}%` },
    {
      header: 'Ações',
      render: (player) => (
        <Button variant="outline" onClick={() => setTarget(player)}>
          Vender jogador
        </Button>
      ),
    },
  ];

  return (
    <>
      <DataState empty={club.players.length === 0} emptyMessage="O elenco está vazio. Compre jogadores livres na tela Jogadores.">
        <DataTable caption="Elenco" columns={columns} rows={club.players} rowKey={(player) => player.id} />
      </DataState>

      {target && (
        <ConfirmDialog
          title="Vender jogador"
          message={`Vender ${target.name} pelo valor de mercado atual (${formatMoney(target.market_value)} moedas)? Antes do fim da fidelidade há multa.`}
          confirmLabel="Vender jogador"
          pending={pending}
          onConfirm={confirmSale}
          onCancel={() => setTarget(null)}
        />
      )}

      {receipt && (
        <Modal title="Venda concluída" onClose={() => setReceipt(null)}>
          <ResultList
            items={[
              ['Valor de compra', formatMoney(receipt.purchase_value)],
              ['Valor de venda', formatMoney(receipt.value)],
              ['Dias restantes', formatDays(receipt.remaining_days)],
              ['Multa', formatMoney(receipt.penalty)],
              ['Lucro', formatSigned(receipt.profit)],
              ['Comissão', formatMoney(receipt.fee)],
              ['Crédito no caixa', formatMoney(receipt.cash_change)],
              ['Novo caixa', formatMoney(receipt.club_cash)],
            ]}
          />
          <div className="mt-6 flex justify-end">
            <Button variant="primary" onClick={() => setReceipt(null)}>
              Fechar
            </Button>
          </div>
        </Modal>
      )}
    </>
  );
}
