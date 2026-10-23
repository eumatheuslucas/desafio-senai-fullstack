import { SelectInput, TextInput } from '../../components/Field.jsx';
import { CONDITION_LABELS, PLAYER_SORTS, POSITIONS } from '../../lib/constants.js';

export default function PlayerFilters({ filters, clubs, onChange }) {
  const bind = (name) => ({
    value: filters[name],
    onChange: (event) => onChange({ [name]: event.target.value }),
  });

  return (
    <section aria-label="Filtros de jogadores" className="mb-4 grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
      <TextInput id="filter-search" label="Buscar por nome" placeholder="Ex.: Meia Lua" {...bind('search')} />
      <SelectInput id="filter-position" label="Posição" {...bind('position')}>
        <option value="">Todas</option>
        {POSITIONS.map((position) => (
          <option key={position} value={position}>
            {position}
          </option>
        ))}
      </SelectInput>
      <SelectInput id="filter-condition" label="Condição" {...bind('condition')}>
        <option value="">Todas</option>
        {Object.entries(CONDITION_LABELS).map(([value, label]) => (
          <option key={value} value={value}>
            {label}
          </option>
        ))}
      </SelectInput>
      <SelectInput
        id="filter-club"
        label="Clube"
        hint={filters.free ? 'Desmarque “Só livres” para filtrar por clube.' : undefined}
        disabled={filters.free}
        {...bind('clubId')}
      >
        <option value="">Todos</option>
        {clubs.map((club) => (
          <option key={club.id} value={club.id}>
            {club.name}
          </option>
        ))}
      </SelectInput>
      <TextInput id="filter-min-age" label="Idade mínima" type="number" min="16" max="45" {...bind('minAge')} />
      <TextInput id="filter-max-age" label="Idade máxima" type="number" min="16" max="45" {...bind('maxAge')} />
      <TextInput id="filter-min-value" label="Valor mínimo (moedas)" inputMode="decimal" placeholder="Ex.: 10,50" {...bind('minValue')} />
      <TextInput id="filter-max-value" label="Valor máximo (moedas)" inputMode="decimal" placeholder="Ex.: 80" {...bind('maxValue')} />
      <SelectInput id="filter-sort" label="Ordenar por" {...bind('sort')}>
        {PLAYER_SORTS.map((sort) => (
          <option key={sort.value} value={sort.value}>
            {sort.label}
          </option>
        ))}
      </SelectInput>
      <label className="flex items-center gap-2 self-end pb-2 text-sm">
        <input
          type="checkbox"
          checked={filters.free}
          disabled={Boolean(filters.clubId)}
          onChange={(event) => onChange({ free: event.target.checked })}
        />
        Só livres
        {filters.clubId && <span className="text-xs text-muted-fg">(limpe o clube para usar)</span>}
      </label>
    </section>
  );
}
