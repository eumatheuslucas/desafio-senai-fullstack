export function checkText(value, label, min, max) {
  const text = (value ?? '').trim().replace(/\s+/g, ' ');
  if (!text) {
    return `Informe ${label}.`;
  }
  if (text.length < min || text.length > max) {
    return `Use de ${min} a ${max} caracteres.`;
  }
  return null;
}

export function checkInt(value, label, min, max) {
  if (value === '' || value === null || value === undefined) {
    return `Informe ${label}.`;
  }
  const number = Number(value);
  if (!Number.isInteger(number) || number < min || number > max) {
    return `Use um número inteiro de ${min} a ${max}.`;
  }
  return null;
}

export function checkChoice(value, label) {
  return value ? null : `Escolha ${label}.`;
}

export function collectErrors(checks) {
  return Object.fromEntries(Object.entries(checks).filter(([, message]) => message));
}
