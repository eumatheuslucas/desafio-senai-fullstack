import { getStandings } from '../../api/league.js';
import DataState from '../../components/DataState.jsx';
import DataTable from '../../components/DataTable.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import { useFetch } from '../../hooks/useFetch.js';
import { formatSigned } from '../../lib/format.js';

const COLUMNS = [
  { header: 'Posição', align: 'right', render: (row) => row.position },
  { header: 'Clube', render: (row) => <span className="font-medium">{row.club_name}</span> },
  { header: 'Jogos', align: 'right', render: (row) => row.played },
  { header: 'Vitórias', align: 'right', render: (row) => row.wins },
  { header: 'Empates', align: 'right', render: (row) => row.draws },
  { header: 'Derrotas', align: 'right', render: (row) => row.losses },
  { header: 'Gols marcados', align: 'right', render: (row) => row.goals_for },
  { header: 'Gols sofridos', align: 'right', render: (row) => row.goals_against },
  { header: 'Saldo', align: 'right', render: (row) => formatSigned(row.goal_difference) },
  { header: 'Pontos', align: 'right', render: (row) => <span className="font-semibold">{row.points}</span> },
];

export default function StandingsPage() {
  const { data, loading, error, reload } = useFetch(getStandings, []);
  return (
    <>
      <PageHeader title="Classificação" description="Vitória vale 3 pontos, empate 1 e derrota 0." />
      <DataState
        loading={loading}
        error={error}
        onRetry={reload}
        empty={data && data.length === 0}
        emptyMessage="Nenhum clube na liga ainda. Crie clubes para ver a classificação."
      >
        {data && <DataTable caption="Classificação" columns={COLUMNS} rows={data} rowKey={(row) => row.club_id} />}
      </DataState>
    </>
  );
}
