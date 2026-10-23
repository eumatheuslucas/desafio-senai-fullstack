import DataTable from '../../components/DataTable.jsx';
import { ATTRIBUTE_LABELS, RESULT_LABELS } from '../../lib/constants.js';

const describePlayer = (side) =>
  `${side.player_name} (${side.position}) · ${ATTRIBUTE_LABELS[side.attribute]} ${side.value}`;

export default function MatchResult({ match }) {
  const clubName = (clubId) => (clubId === match.home.club_id ? match.home.club_name : match.away.club_name);

  const columns = [
    { header: 'Duelo', align: 'right', render: (duel) => duel.order },
    { header: 'Clube que ataca', render: (duel) => clubName(duel.attacking_club_id) },
    { header: 'Atacante', render: (duel) => describePlayer(duel.attacker) },
    { header: 'Defensor', render: (duel) => describePlayer(duel.defender) },
    {
      header: 'Gol',
      render: (duel) => <span className={duel.goal ? 'font-medium text-success' : 'text-muted-fg'}>{duel.goal ? 'Sim' : 'Não'}</span>,
    },
  ];

  return (
    <section aria-label="Resultado da partida" className="flex flex-col gap-3">
      <p className="text-center text-xl font-semibold">
        {match.home.club_name} {match.home.goals} × {match.away.goals} {match.away.club_name}
      </p>
      <p className="text-center text-sm text-muted-fg">
        {RESULT_LABELS[match.result]} · formações {match.home.formation} e {match.away.formation}
      </p>
      <DataTable caption="Duelos da partida" columns={columns} rows={match.duels} rowKey={(duel) => duel.order} />
    </section>
  );
}
