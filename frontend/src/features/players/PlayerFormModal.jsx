import { useState } from 'react';
import { createPlayer, updatePlayer } from '../../api/players.js';
import Button from '../../components/Button.jsx';
import { SelectInput, TextInput } from '../../components/Field.jsx';
import Modal from '../../components/Modal.jsx';
import ResultList from '../../components/ResultList.jsx';
import { useAction } from '../../hooks/useAction.js';
import { useForm } from '../../hooks/useForm.js';
import { POSITIONS } from '../../lib/constants.js';
import { formatMoney } from '../../lib/format.js';
import { checkChoice, checkInt, checkText, collectErrors } from '../../lib/validators.js';

const ATTRIBUTES = [
  ['pace', 'Velocidade'],
  ['shooting', 'Finalização'],
  ['passing', 'Passe'],
  ['dribbling', 'Drible'],
  ['defending', 'Defesa'],
  ['physical', 'Físico'],
];

const EMPTY_FORM = {
  name: '', position: '', agility: '', age: '', potential: '',
  pace: '', shooting: '', passing: '', dribbling: '', defending: '', physical: '',
};

function validateBasics(values) {
  return {
    name: checkText(values.name, 'o nome', 3, 100),
    position: checkChoice(values.position, 'a posição'),
    agility: checkInt(values.agility, 'a agilidade', 1, 100),
    age: checkInt(values.age, 'a idade', 16, 45),
  };
}

function validateCreate(values) {
  const attributes = Object.fromEntries(
    ATTRIBUTES.map(([key, label]) => [key, checkInt(values[key], `a ${label.toLowerCase()}`, 1, 99)]),
  );
  return collectErrors({
    ...validateBasics(values),
    ...attributes,
    potential: checkInt(values.potential, 'o potencial', 1, 99),
  });
}

const validateEdit = (player) => (values) =>
  collectErrors({
    ...validateBasics(values),
    age:
      checkInt(values.age, 'a idade', 16, 45) ??
      (Number(values.age) < player.age ? `A idade não pode ser menor que a atual (${player.age}).` : null),
  });

export default function PlayerFormModal({ player, onClose, onSaved }) {
  const isEdit = Boolean(player);
  const { run, pending } = useAction();
  const [created, setCreated] = useState(null);
  const form = useForm(
    isEdit
      ? { name: player.name, position: player.position, agility: String(player.agility), age: String(player.age) }
      : EMPTY_FORM,
    isEdit ? validateEdit(player) : validateCreate,
  );
  const { values, errors, setField } = form;

  const bind = (name) => ({
    value: values[name],
    error: errors[name],
    onChange: (event) => setField(name, event.target.value),
  });

  const save = () => {
    const body = {
      name: values.name.trim(),
      position: values.position,
      agility: Number(values.agility),
      age: Number(values.age),
    };
    if (isEdit) {
      return run(() => updatePlayer(player.id, body), {
        success: 'Alterações salvas.',
        onSuccess: () => {
          onSaved();
          onClose();
        },
      });
    }
    const fullBody = {
      ...body,
      ...Object.fromEntries(ATTRIBUTES.map(([key]) => [key, Number(values[key])])),
      potential: Number(values.potential),
    };
    return run(() => createPlayer(fullBody), {
      success: 'Jogador cadastrado.',
      onSuccess: (result) => {
        setCreated(result);
        onSaved();
      },
    });
  };

  if (created) {
    return (
      <Modal title="Jogador cadastrado" onClose={onClose}>
        <ResultList
          items={[
            ['Nome', created.name],
            ['Posição', created.position],
            ['Overall', created.overall],
            ['Valor base', formatMoney(created.base_value)],
            ['Valor de mercado', formatMoney(created.market_value)],
          ]}
        />
        <div className="mt-6 flex justify-end gap-2">
          <Button
            onClick={() => {
              setCreated(null);
              form.reset();
            }}
          >
            Cadastrar outro jogador
          </Button>
          <Button variant="primary" onClick={onClose}>
            Fechar
          </Button>
        </div>
      </Modal>
    );
  }

  return (
    <Modal title={isEdit ? 'Editar jogador' : 'Cadastrar jogador'} onClose={onClose}>
      <form noValidate onSubmit={form.submit(save)} className="grid gap-3 sm:grid-cols-2">
        <div className="sm:col-span-2">
          <TextInput id="player-name" label="Nome" placeholder="Ex.: Meia Lua" {...bind('name')} />
        </div>
        <SelectInput
          id="player-position"
          label="Posição"
          disabled={isEdit && player.club_id !== null}
          hint={isEdit && player.club_id !== null ? 'Jogador com clube não muda de posição.' : undefined}
          {...bind('position')}
        >
          <option value="">Selecione</option>
          {POSITIONS.map((position) => (
            <option key={position} value={position}>
              {position}
            </option>
          ))}
        </SelectInput>
        <TextInput id="player-age" label="Idade" type="number" placeholder="Ex.: 25" {...bind('age')} />
        <TextInput id="player-agility" label="Agilidade" type="number" placeholder="Ex.: 85" {...bind('agility')} />
        {!isEdit && (
          <>
            {ATTRIBUTES.map(([key, label]) => (
              <TextInput key={key} id={`player-${key}`} label={label} type="number" placeholder="1 a 99" {...bind(key)} />
            ))}
            <TextInput
              id="player-potential"
              label="Potencial"
              type="number"
              placeholder="1 a 99"
              hint="Não pode ser menor que o maior atributo."
              {...bind('potential')}
            />
          </>
        )}
        <div className="mt-2 flex justify-end gap-2 sm:col-span-2">
          <Button onClick={onClose}>Cancelar</Button>
          <Button type="submit" variant="primary" loading={pending}>
            {isEdit ? 'Salvar alterações' : 'Cadastrar jogador'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
