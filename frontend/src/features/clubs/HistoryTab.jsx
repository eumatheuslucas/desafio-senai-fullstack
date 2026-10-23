import { useState } from 'react';
import { exportTransactions, listTransactions } from '../../api/clubs.js';
import Button from '../../components/Button.jsx';
import DataState from '../../components/DataState.jsx';
import DataTable from '../../components/DataTable.jsx';
import { SelectInput } from '../../components/Field.jsx';
import { useAction } from '../../hooks/useAction.js';
import { useFetch } from '../../hooks/useFetch.js';
import { formatDateTime, formatMoney, formatSigned } from '../../lib/format.js';

const TYPE_LABELS = { COMPRA: 'Compra', VENDA: 'Venda' };

const SUMMARY_ITEMS = [
  ['total_spent', 'Total pago em compras', formatMoney],
  ['total_received', 'Total recebido em vendas', formatMoney],
  ['total_fees', 'Taxas e comissões', formatMoney],
  ['total_penalties', 'Multas', formatMoney],
  ['total_profit', 'Lucro total', formatSigned],
];

const cashClass = (value) => (value >= 0 ? 'text-success' : 'text-danger');

const COLUMNS = [
  { header: 'Data e hora', render: (item) => formatDateTime(item.created_at) },
  { header: 'Tipo', render: (item) => TYPE_LABELS[item.type] },
  { header: 'Jogador', render: (item) => <span className="font-medium">{item.player_name}</span> },
  { header: 'Valor', align: 'right', render: (item) => formatMoney(item.value) },
  { header: 'Taxa ou comissão', align: 'right', render: (item) => formatMoney(item.fee) },
  { header: 'Multa', align: 'right', render: (item) => formatMoney(item.penalty) },
  { header: 'Lucro', align: 'right', render: (item) => formatSigned(item.profit) },
  {
    header: 'Variação do caixa',
    align: 'right',
    render: (item) => <span className={cashClass(item.cash_change)}>{formatSigned(item.cash_change)}</span>,
  },
];

export default function HistoryTab({ clubId }) {
  const [types, setTypes] = useState([]);
  const [playerId, setPlayerId] = useState('');
  const { run, pending } = useAction();
  const params = { type: types.join(','), player_id: playerId };

  const all = useFetch(() => listTransactions(clubId, {}), [clubId]);
  const filtered = useFetch(() => listTransactions(clubId, params), [clubId, types, playerId]);

  const players = [...new Map((all.data?.data ?? []).map((item) => [item.player_id, item.player_name])).entries()];

  const toggleType = (type) =>
    setTypes((current) => (current.includes(type) ? current.filter((item) => item !== type) : [...current, type]));

  const exportCsv = () => run(() => exportTransactions(clubId, params), { success: 'Arquivo CSV baixado.' });

  return (
    <>
      <div className="mb-4 flex flex-wrap items-end gap-4">
        <fieldset className="flex items-center gap-4 text-sm">
          <legend className="mb-1 font-medium">Tipo</legend>
          {Object.entries(TYPE_LABELS).map(([value, label]) => (
            <label key={value} className="flex items-center gap-2">
              <input type="checkbox" checked={types.includes(value)} onChange={() => toggleType(value)} />
              {label}
            </label>
          ))}
        </fieldset>
        <div className="w-56">
          <SelectInput id="history-player" label="Jogador" value={playerId} onChange={(event) => setPlayerId(event.target.value)}>
            <option value="">Todos</option>
            {players.map(([id, name]) => (
              <option key={id} value={id}>
                {name}
              </option>
            ))}
          </SelectInput>
        </div>
        <Button loading={pending} onClick={exportCsv}>
          Exportar CSV
        </Button>
      </div>

      <DataState
        loading={filtered.loading && !filtered.data}
        error={filtered.error}
        onRetry={filtered.reload}
        empty={filtered.data && filtered.data.data.length === 0}
        emptyMessage="Nenhuma transação encontrada. Compras e vendas aparecem aqui."
      >
        {filtered.data && (
          <>
            <DataTable caption="Histórico de transações" columns={COLUMNS} rows={filtered.data.data} rowKey={(item) => item.id} />
            <dl className="mt-4 grid gap-3 sm:grid-cols-3 lg:grid-cols-5">
              {SUMMARY_ITEMS.map(([key, label, format]) => (
                <div key={key} className="rounded-xl border border-line p-3">
                  <dt className="text-xs text-muted-fg">{label}</dt>
                  <dd className="mt-1 text-lg font-semibold">{format(filtered.data.summary[key])}</dd>
                </div>
              ))}
            </dl>
          </>
        )}
      </DataState>
    </>
  );
}
