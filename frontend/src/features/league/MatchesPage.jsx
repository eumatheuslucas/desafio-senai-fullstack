import { useState } from 'react';
import { listMatches, playMatch } from '../../api/league.js';
import Button from '../../components/Button.jsx';
import DataState from '../../components/DataState.jsx';
import DataTable from '../../components/DataTable.jsx';
import { SelectInput } from '../../components/Field.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import { useAction } from '../../hooks/useAction.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useForm } from '../../hooks/useForm.js';
import { formatDateTime } from '../../lib/format.js';
import { checkChoice, collectErrors } from '../../lib/validators.js';
import { useClubs } from '../clubs/ClubsContext.jsx';
import MatchDetailModal from './MatchDetailModal.jsx';
import MatchResult from './MatchResult.jsx';

const validate = (values) =>
  collectErrors({
    home: checkChoice(values.home, 'o mandante'),
    away: checkChoice(values.away, 'o visitante'),
  });

export default function MatchesPage() {
  const { clubs } = useClubs();
  const { run, pending } = useAction();
  const [played, setPlayed] = useState(null);
  const [clubFilter, setClubFilter] = useState('');
  const [openMatchId, setOpenMatchId] = useState(null);
  const form = useForm({ home: '', away: '' }, validate);
  const history = useFetch(() => listMatches(clubFilter), [clubFilter]);

  const play = (values) =>
    run(() => playMatch({ home_club_id: Number(values.home), away_club_id: Number(values.away) }), {
      success: 'Partida jogada.',
      onSuccess: (match) => {
        setPlayed(match);
        history.reload();
      },
    });

  const clubOptions = (
    <>
      <option value="">Selecione</option>
      {clubs.map((club) => (
        <option key={club.id} value={club.id}>
          {club.name}
        </option>
      ))}
    </>
  );

  const columns = [
    { header: 'Data e hora', render: (match) => formatDateTime(match.played_at) },
    { header: 'Mandante', render: (match) => match.home.club_name },
    {
      header: 'Placar',
      align: 'right',
      render: (match) => <span className="font-medium">{match.home.goals} × {match.away.goals}</span>,
    },
    { header: 'Visitante', render: (match) => match.away.club_name },
    {
      header: 'Ações',
      render: (match) => (
        <Button variant="ghost" onClick={() => setOpenMatchId(match.id)}>
          Ver duelos
        </Button>
      ),
    },
  ];

  return (
    <>
      <PageHeader title="Partidas" description="Escolha dois clubes com escalação completa. O placar sai dos 6 duelos, sem sorteio." />

      <form noValidate onSubmit={form.submit(play)} className="mb-6 grid items-start gap-3 sm:grid-cols-3">
        <SelectInput
          id="match-home"
          label="Mandante"
          value={form.values.home}
          error={form.errors.home}
          onChange={(event) => form.setField('home', event.target.value)}
        >
          {clubOptions}
        </SelectInput>
        <SelectInput
          id="match-away"
          label="Visitante"
          value={form.values.away}
          error={form.errors.away}
          onChange={(event) => form.setField('away', event.target.value)}
        >
          {clubOptions}
        </SelectInput>
        <div className="sm:self-end">
          <Button type="submit" variant="primary" loading={pending}>
            Jogar partida
          </Button>
        </div>
      </form>

      {played && (
        <div className="mb-8">
          <MatchResult match={played} />
        </div>
      )}

      <h2 className="mb-3 text-lg font-semibold">Histórico de partidas</h2>
      <div className="mb-4 max-w-xs">
        <SelectInput id="match-club-filter" label="Filtrar por clube" value={clubFilter} onChange={(event) => setClubFilter(event.target.value)}>
          <option value="">Todos os clubes</option>
          {clubs.map((club) => (
            <option key={club.id} value={club.id}>
              {club.name}
            </option>
          ))}
        </SelectInput>
      </div>
      <DataState
        loading={history.loading && !history.data}
        error={history.error}
        onRetry={history.reload}
        empty={history.data && history.data.length === 0}
        emptyMessage="Nenhuma partida jogada ainda. Escolha dois clubes e jogue a primeira."
      >
        {history.data && <DataTable caption="Histórico de partidas" columns={columns} rows={history.data} rowKey={(match) => match.id} />}
      </DataState>

      {openMatchId && <MatchDetailModal matchId={openMatchId} onClose={() => setOpenMatchId(null)} />}
    </>
  );
}
