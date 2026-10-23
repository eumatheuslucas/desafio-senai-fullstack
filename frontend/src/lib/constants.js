export const POSITIONS = ['GOL', 'ZAG', 'LAT', 'MEI', 'ATA'];

export const CONDITION_LABELS = {
  APTO: 'Apto',
  MAL_CONDICIONADO: 'Mal condicionado',
  DOENTE: 'Doente',
};

export const FORMATIONS = {
  '3-1': { defense: 3, midfield: 0, attack: 1 },
  '2-2': { defense: 2, midfield: 0, attack: 2 },
  '2-1-1': { defense: 2, midfield: 1, attack: 1 },
  '1-2-1': { defense: 1, midfield: 2, attack: 1 },
};

export const PLAYER_SORTS = [
  { value: 'name', label: 'Nome' },
  { value: 'value_desc', label: 'Valor de mercado (maior)' },
  { value: 'value_asc', label: 'Valor de mercado (menor)' },
  { value: 'age_asc', label: 'Idade (menor)' },
  { value: 'age_desc', label: 'Idade (maior)' },
  { value: 'position', label: 'Posição' },
];

export const CLUB_SORTS = [
  { value: 'name', label: 'Nome' },
  { value: 'net_worth_desc', label: 'Patrimônio (maior)' },
  { value: 'net_worth_asc', label: 'Patrimônio (menor)' },
];

export const ATTRIBUTE_LABELS = {
  shooting: 'finalização',
  passing: 'passe',
  dribbling: 'drible',
  defending: 'defesa',
};

export const RESULT_LABELS = {
  MANDANTE: 'Vitória do mandante',
  VISITANTE: 'Vitória do visitante',
  EMPATE: 'Empate',
};

export const PAGE_SIZE = 10;
