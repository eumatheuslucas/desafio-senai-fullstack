const moneyFormat = new Intl.NumberFormat('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

const dateTimeFormat = new Intl.DateTimeFormat('pt-BR', {
  timeZone: 'America/Sao_Paulo',
  day: '2-digit',
  month: '2-digit',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit',
});

export const EMPTY_VALUE = '–';

export function formatMoney(value) {
  return value === null || value === undefined ? EMPTY_VALUE : moneyFormat.format(value);
}

export function formatSigned(value) {
  if (value === null || value === undefined) {
    return EMPTY_VALUE;
  }
  const sign = value > 0 ? '+' : value < 0 ? '-' : '';
  return `${sign}${moneyFormat.format(Math.abs(value))}`;
}

export function formatDateTime(isoText) {
  return isoText ? dateTimeFormat.format(new Date(isoText)) : EMPTY_VALUE;
}

export function formatDays(days) {
  return `${new Intl.NumberFormat('pt-BR').format(days)} ${days === 1 ? 'dia' : 'dias'}`;
}

export function toApiDateTime(localValue) {
  return `${localValue}-03:00`;
}
