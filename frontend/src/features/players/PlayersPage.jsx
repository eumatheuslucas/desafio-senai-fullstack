import { useState } from 'react';
import { deletePlayer, listPlayers } from '../../api/players.js';
import Button from '../../components/Button.jsx';
import ConditionBadge from '../../components/ConditionBadge.jsx';
import ConfirmDialog from '../../components/ConfirmDialog.jsx';
import DataState from '../../components/DataState.jsx';
import DataTable from '../../components/DataTable.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import Pagination from '../../components/Pagination.jsx';
import { useAction } from '../../hooks/useAction.js';
import { useDebounce } from '../../hooks/useDebounce.js';
import { useFetch } from '../../hooks/useFetch.js';
import { PAGE_SIZE } from '../../lib/constants.js';
import { formatMoney } from '../../lib/format.js';
import { useClubs } from '../clubs/ClubsContext.jsx';
import BuyModal from './BuyModal.jsx';
import ConditionModal from './ConditionModal.jsx';
import PlayerFilters from './PlayerFilters.jsx';
import PlayerFormModal from './PlayerFormModal.jsx';

const INITIAL_FILTERS = {
  search: '', position: '', condition: '', free: false, clubId: '',
  minAge: '', maxAge: '', minValue: '', maxValue: '', sort: 'name',
};

const MIN_SEARCH_LENGTH = 2;

function toParams(filters, page) {
  const search = filters.search.trim();
  return {
    search: search.length >= MIN_SEARCH_LENGTH ? search : '',
    position: filters.position,
    condition: filters.condition,
    free: filters.free ? 'true' : '',
    club_id: filters.clubId,
    min_age: filters.minAge,
    max_age: filters.maxAge,
    min_value: filters.minValue.replace(',', '.'),
    max_value: filters.maxValue.replace(',', '.'),
    sort: filters.sort,
    page,
    limit: PAGE_SIZE,
  };
}

export default function PlayersPage() {
  const { clubs, reload: reloadClubs } = useClubs();
  const [filters, setFilters] = useState(INITIAL_FILTERS);
  const [page, setPage] = useState(1);
  const [dialog, setDialog] = useState(null);
  const debouncedFilters = useDebounce(filters);
  const { run, pending } = useAction();

  const { data, loading, error, reload } = useFetch(
    () => listPlayers(toParams(debouncedFilters, page)),
    [debouncedFilters, page],
  );

  const changeFilters = (partial) => {
    setFilters((current) => ({ ...current, ...partial }));
    setPage(1);
  };

  const closeDialog = () => setDialog(null);
  const afterChange = () => {
    reload();
    reloadClubs();
  };

  const confirmDelete = () =>
    run(() => deletePlayer(dialog.player.id), {
      success: 'Jogador excluído.',
      onSuccess: () => {
        closeDialog();
        reload();
      },
    });

  const clubLabel = (clubId) => (clubId === null ? 'Livre' : (clubs.find((club) => club.id === clubId)?.name ?? `Clube ${clubId}`));

  const columns = [
    { header: 'Nome', render: (player) => <span className="font-medium">{player.name}</span> },
    { header: 'Posição', render: (player) => player.position },
    { header: 'Overall', align: 'right', render: (player) => player.overall },
    { header: 'Agilidade', align: 'right', render: (player) => player.agility },
    { header: 'Idade', align: 'right', render: (player) => player.age },
    { header: 'Condição', render: (player) => <ConditionBadge condition={player.condition} /> },
    { header: 'Valor base', align: 'right', render: (player) => formatMoney(player.base_value) },
    { header: 'Valor de mercado', align: 'right', render: (player) => formatMoney(player.market_value) },
    { header: 'Clube', render: (player) => clubLabel(player.club_id) },
    {
      header: 'Ações',
      render: (player) => (
        <div className="flex gap-1">
          <Button variant="ghost" onClick={() => setDialog({ type: 'edit', player })}>Editar</Button>
          <Button variant="ghost" onClick={() => setDialog({ type: 'condition', player })}>Alterar condição</Button>
          {player.club_id === null && (
            <>
              <Button variant="primary" onClick={() => setDialog({ type: 'buy', player })}>Comprar</Button>
              <Button variant="ghost" onClick={() => setDialog({ type: 'delete', player })}>Excluir</Button>
            </>
          )}
        </div>
      ),
    },
  ];

  return (
    <>
      <PageHeader title="Jogadores" description="Cadastre jogadores e acompanhe o valor de mercado de cada um.">
        <Button variant="primary" onClick={() => setDialog({ type: 'create' })}>
          Cadastrar jogador
        </Button>
      </PageHeader>

      <PlayerFilters filters={filters} clubs={clubs} onChange={changeFilters} />

      <DataState
        loading={loading && !data}
        error={error}
        onRetry={reload}
        empty={data && data.data.length === 0}
        emptyMessage="Nenhum jogador encontrado. Ajuste os filtros ou cadastre um jogador."
      >
        {data && (
          <>
            <DataTable caption="Jogadores" columns={columns} rows={data.data} rowKey={(player) => player.id} />
            <Pagination
              page={data.meta.page}
              totalPages={data.meta.total_pages}
              totalItems={data.meta.total_items}
              onChange={setPage}
            />
          </>
        )}
      </DataState>

      {dialog?.type === 'create' && <PlayerFormModal onClose={closeDialog} onSaved={reload} />}
      {dialog?.type === 'edit' && <PlayerFormModal player={dialog.player} onClose={closeDialog} onSaved={reload} />}
      {dialog?.type === 'condition' && <ConditionModal player={dialog.player} onClose={closeDialog} onSaved={afterChange} />}
      {dialog?.type === 'buy' && <BuyModal player={dialog.player} onClose={closeDialog} onBought={afterChange} />}
      {dialog?.type === 'delete' && (
        <ConfirmDialog
          title="Excluir jogador"
          message={`Excluir ${dialog.player.name}? O histórico de transações e de partidas é mantido.`}
          confirmLabel="Excluir jogador"
          destructive
          pending={pending}
          onConfirm={confirmDelete}
          onCancel={closeDialog}
        />
      )}
    </>
  );
}
