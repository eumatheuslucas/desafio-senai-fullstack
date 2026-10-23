import { useState } from 'react';
import { Link } from 'react-router-dom';
import { listClubs } from '../../api/clubs.js';
import Button from '../../components/Button.jsx';
import DataState from '../../components/DataState.jsx';
import DataTable from '../../components/DataTable.jsx';
import { SelectInput } from '../../components/Field.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import { useFetch } from '../../hooks/useFetch.js';
import { CLUB_SORTS } from '../../lib/constants.js';
import { formatMoney } from '../../lib/format.js';
import ClubFormModal from './ClubFormModal.jsx';
import { useClubs } from './ClubsContext.jsx';

const COLUMNS = [
  {
    header: 'Clube',
    render: (club) => (
      <Link to={`/clubs/${club.id}`} className="font-medium text-primary hover:underline">
        {club.name}
      </Link>
    ),
  },
  { header: 'Sigla', render: (club) => club.acronym },
  { header: 'Caixa', align: 'right', render: (club) => formatMoney(club.cash) },
  { header: 'Valor do elenco', align: 'right', render: (club) => formatMoney(club.squad_value) },
  { header: 'Patrimônio', align: 'right', render: (club) => formatMoney(club.net_worth) },
];

export default function ClubsPage() {
  const [sort, setSort] = useState('name');
  const [creating, setCreating] = useState(false);
  const { reload: reloadSidebar } = useClubs();
  const { data, loading, error, reload } = useFetch(() => listClubs(sort), [sort]);

  const afterCreate = () => {
    reload();
    reloadSidebar();
  };

  return (
    <>
      <PageHeader title="Clubes" description="Caixa, valor do elenco e patrimônio de cada clube.">
        <Button variant="primary" onClick={() => setCreating(true)}>
          Criar clube
        </Button>
      </PageHeader>

      <div className="mb-4 max-w-xs">
        <SelectInput id="club-sort" label="Ordenar por" value={sort} onChange={(event) => setSort(event.target.value)}>
          {CLUB_SORTS.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </SelectInput>
      </div>

      <DataState
        loading={loading && !data}
        error={error}
        onRetry={reload}
        empty={data && data.length === 0}
        emptyMessage="Nenhum clube cadastrado. Crie o primeiro clube da liga."
      >
        {data && <DataTable caption="Clubes" columns={COLUMNS} rows={data} rowKey={(club) => club.id} />}
      </DataState>

      {creating && <ClubFormModal onClose={() => setCreating(false)} onCreated={afterCreate} />}
    </>
  );
}
