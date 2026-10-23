import { useEffect, useState } from 'react';
import { getLineup, saveLineup } from '../../api/clubs.js';
import Alert from '../../components/Alert.jsx';
import Button from '../../components/Button.jsx';
import ConditionBadge from '../../components/ConditionBadge.jsx';
import DataState from '../../components/DataState.jsx';
import DataTable from '../../components/DataTable.jsx';
import { SelectInput } from '../../components/Field.jsx';
import { useAction } from '../../hooks/useAction.js';
import { useFetch } from '../../hooks/useFetch.js';
import { FORMATIONS } from '../../lib/constants.js';

const DEFAULT_FORMATION = '2-1-1';

function slotPlan(formation) {
  const { defense, midfield, attack } = FORMATIONS[formation];
  const slots = [{ id: 'gol-1', label: 'Goleiro', positions: ['GOL'] }];
  const add = (prefix, label, amount, positions) => {
    for (let index = 1; index <= amount; index += 1) {
      slots.push({ id: `${prefix}-${index}`, label: `${label} ${index}`, positions });
    }
  };
  add('def', 'Defesa', defense, ['ZAG', 'LAT']);
  add('mei', 'Meio', midfield, ['MEI']);
  add('ata', 'Ataque', attack, ['ATA']);
  return slots;
}

function fillSlots(slots, players) {
  const remaining = [...players];
  const chosen = {};
  slots.forEach((slot) => {
    const index = remaining.findIndex((player) => slot.positions.includes(player.position));
    if (index >= 0) {
      chosen[slot.id] = String(remaining[index].id);
      remaining.splice(index, 1);
    }
  });
  return chosen;
}

export default function LineupTab({ club }) {
  const { data: lineup, loading, error, reload } = useFetch(() => getLineup(club.id), [club.id]);
  const { run, pending } = useAction();
  const [formation, setFormation] = useState(DEFAULT_FORMATION);
  const [chosen, setChosen] = useState({});
  const [errors, setErrors] = useState({});
  const slots = slotPlan(formation);

  useEffect(() => {
    if (lineup) {
      const saved = lineup.formation ?? DEFAULT_FORMATION;
      setFormation(saved);
      setChosen(fillSlots(slotPlan(saved), lineup.players));
      setErrors({});
    }
  }, [lineup]);

  const changeFormation = (next) => {
    const picked = club.players.filter((player) => Object.values(chosen).includes(String(player.id)));
    setFormation(next);
    setChosen(fillSlots(slotPlan(next), picked));
    setErrors({});
  };

  const choose = (slotId, value) => {
    setChosen((current) => ({ ...current, [slotId]: value }));
    setErrors((current) => ({ ...current, [slotId]: undefined }));
  };

  const optionsFor = (slot) =>
    club.players.filter(
      (player) =>
        slot.positions.includes(player.position) &&
        player.condition !== 'DOENTE' &&
        (chosen[slot.id] === String(player.id) || !Object.values(chosen).includes(String(player.id))),
    );

  const save = (event) => {
    event.preventDefault();
    const missing = Object.fromEntries(
      slots.filter((slot) => !chosen[slot.id]).map((slot) => [slot.id, `Escolha o jogador de ${slot.label.toLowerCase()}.`]),
    );
    setErrors(missing);
    if (Object.keys(missing).length > 0) {
      return;
    }
    run(() => saveLineup(club.id, { formation, player_ids: slots.map((slot) => Number(chosen[slot.id])) }), {
      success: 'Escalação salva.',
      onSuccess: reload,
    });
  };

  const starterColumns = [
    { header: 'Titular', render: (player) => <span className="font-medium">{player.name}</span> },
    { header: 'Posição', render: (player) => player.position },
    { header: 'Condição', render: (player) => <ConditionBadge condition={player.condition} /> },
  ];

  return (
    <DataState loading={loading && !lineup} error={error} onRetry={reload}>
      {lineup && (
        <div className="grid gap-6 lg:grid-cols-2">
          <form noValidate onSubmit={save} className="flex flex-col gap-3">
            {!lineup.complete && (
              <Alert type="warning">Escalação incompleta: o clube não pode jogar até escalar 5 titulares.</Alert>
            )}
            <SelectInput id="lineup-formation" label="Formação" value={formation} onChange={(event) => changeFormation(event.target.value)}>
              {Object.keys(FORMATIONS).map((option) => (
                <option key={option} value={option}>
                  {option}
                </option>
              ))}
            </SelectInput>
            {slots.map((slot) => (
              <SelectInput
                key={slot.id}
                id={`lineup-${slot.id}`}
                label={slot.label}
                value={chosen[slot.id] ?? ''}
                error={errors[slot.id]}
                onChange={(event) => choose(slot.id, event.target.value)}
              >
                <option value="">Selecione</option>
                {optionsFor(slot).map((player) => (
                  <option key={player.id} value={player.id}>
                    {player.name} ({player.position})
                  </option>
                ))}
              </SelectInput>
            ))}
            <div>
              <Button type="submit" variant="primary" loading={pending}>
                Salvar escalação
              </Button>
            </div>
          </form>

          <section aria-label="Titulares atuais">
            <h3 className="mb-2 text-sm font-medium">Titulares atuais {lineup.formation ? `(${lineup.formation})` : ''}</h3>
            <DataState empty={lineup.players.length === 0} emptyMessage="Nenhum titular escalado ainda.">
              <DataTable caption="Titulares" columns={starterColumns} rows={lineup.players} rowKey={(player) => player.id} />
            </DataState>
          </section>
        </div>
      )}
    </DataState>
  );
}
