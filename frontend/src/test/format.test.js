import { describe, expect, it } from 'vitest';
import { formatMoney, formatSigned, toApiDateTime } from '../lib/format.js';

describe('format', () => {
  it('formata moeda com duas casas e vírgula', () => {
    expect(formatMoney(1744.14)).toBe('1.744,14');
    expect(formatMoney(37)).toBe('37,00');
    expect(formatMoney(null)).toBe('–');
  });

  it('mostra o sinal da variação do caixa', () => {
    expect(formatSigned(35.7)).toBe('+35,70');
    expect(formatSigned(-45.55)).toBe('-45,55');
  });

  it('envia datas no horário de Brasília', () => {
    expect(toApiDateTime('2027-03-02T10:15')).toBe('2027-03-02T10:15-03:00');
  });
});
