import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import DataState from '../components/DataState.jsx';

describe('DataState', () => {
  it('mostra o carregamento', () => {
    render(<DataState loading>conteúdo</DataState>);
    expect(screen.getByText('Carregando…')).toBeInTheDocument();
  });

  it('mostra a mensagem de erro da API', () => {
    render(<DataState error="Jogador não encontrado.">conteúdo</DataState>);
    expect(screen.getByText('Jogador não encontrado.')).toBeInTheDocument();
  });

  it('mostra a lista vazia', () => {
    render(
      <DataState empty emptyMessage="Nenhum jogador encontrado.">
        conteúdo
      </DataState>,
    );
    expect(screen.getByText('Nenhum jogador encontrado.')).toBeInTheDocument();
  });

  it('mostra o conteúdo quando há dados', () => {
    render(<DataState>conteúdo</DataState>);
    expect(screen.getByText('conteúdo')).toBeInTheDocument();
  });
});
