import Button from './Button.jsx';

export default function Pagination({ page, totalPages, totalItems, onChange }) {
  return (
    <div className="flex flex-wrap items-center justify-between gap-2 pt-3 text-sm">
      <p className="text-muted-fg">
        {totalItems} {totalItems === 1 ? 'jogador encontrado' : 'jogadores encontrados'}
      </p>
      <div className="flex items-center gap-2">
        <Button disabled={page <= 1} onClick={() => onChange(page - 1)}>
          Página anterior
        </Button>
        <span>
          Página {page} de {Math.max(totalPages, 1)}
        </span>
        <Button disabled={page >= totalPages} onClick={() => onChange(page + 1)}>
          Próxima página
        </Button>
      </div>
    </div>
  );
}
