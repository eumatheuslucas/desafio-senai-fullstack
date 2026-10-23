import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getClub } from '../../api/clubs.js';
import DataState from '../../components/DataState.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import { useFetch } from '../../hooks/useFetch.js';
import { formatMoney } from '../../lib/format.js';
import { useClubs } from './ClubsContext.jsx';
import HistoryTab from './HistoryTab.jsx';
import LineupTab from './LineupTab.jsx';
import SquadTab from './SquadTab.jsx';

const TABS = [
  { id: 'squad', label: 'Elenco' },
  { id: 'lineup', label: 'Escalação' },
  { id: 'history', label: 'Histórico' },
];

const MAX_SQUAD_SIZE = 12;

export default function ClubPage() {
  const { id } = useParams();
  const [tab, setTab] = useState('squad');
  const { reload: reloadSidebar } = useClubs();
  const { data: club, loading, error, reload } = useFetch(() => getClub(id), [id]);

  useEffect(() => setTab('squad'), [id]);

  const afterSale = () => {
    reload();
    reloadSidebar();
  };

  return (
    <DataState loading={loading && !club} error={error} onRetry={reload}>
      {club && (
        <>
          <PageHeader title={club.name} description={`Sigla ${club.acronym}`}>
            <Link to="/players" className="rounded-lg bg-primary px-3 py-2 text-sm font-medium text-white hover:bg-primary-hover">
              Comprar jogadores
            </Link>
          </PageHeader>

          <dl className="mb-6 grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
            {[
              ['Caixa', `${formatMoney(club.cash)} moedas`],
              ['Valor do elenco', `${formatMoney(club.squad_value)} moedas`],
              ['Patrimônio', `${formatMoney(club.net_worth)} moedas`],
              ['Elenco', `${club.players.length} de ${MAX_SQUAD_SIZE} jogadores`],
            ].map(([label, value]) => (
              <div key={label} className="rounded-xl border border-line p-3">
                <dt className="text-xs text-muted-fg">{label}</dt>
                <dd className="mt-1 text-lg font-semibold">{value}</dd>
              </div>
            ))}
          </dl>

          <div role="tablist" aria-label="Seções do clube" className="mb-4 flex gap-1 border-b border-line">
            {TABS.map((item) => (
              <button
                key={item.id}
                type="button"
                role="tab"
                aria-selected={tab === item.id}
                onClick={() => setTab(item.id)}
                className={`-mb-px border-b-2 px-4 py-2 text-sm ${tab === item.id ? 'border-primary font-medium text-primary' : 'border-transparent text-muted-fg hover:text-ink'}`}
              >
                {item.label}
              </button>
            ))}
          </div>

          <div role="tabpanel">
            {tab === 'squad' && <SquadTab club={club} onChanged={afterSale} />}
            {tab === 'lineup' && <LineupTab club={club} />}
            {tab === 'history' && <HistoryTab clubId={club.id} />}
          </div>
        </>
      )}
    </DataState>
  );
}
