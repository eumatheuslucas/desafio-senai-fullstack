import { NavLink, Outlet } from 'react-router-dom';
import { useClubs } from '../features/clubs/ClubsContext.jsx';

const MAIN_LINKS = [
  { to: '/players', label: 'Jogadores' },
  { to: '/clubs', label: 'Clubes', end: true },
  { to: '/matches', label: 'Partidas' },
  { to: '/standings', label: 'Classificação' },
];

const linkClass = ({ isActive }) =>
  `block whitespace-nowrap rounded-lg px-3 py-2 text-sm ${isActive ? 'bg-white font-medium shadow-sm' : 'hover:bg-muted'}`;

export default function Layout() {
  const { clubs, loading, error } = useClubs();
  return (
    <div className="min-h-screen md:flex">
      <aside className="border-b border-line bg-muted-subtle p-3 md:w-60 md:shrink-0 md:border-b-0 md:border-r">
        <p className="px-3 pb-2 text-lg font-semibold">
          <span className="mr-2 inline-flex h-7 w-7 items-center justify-center rounded-lg bg-primary text-white">P</span>
          Passe
        </p>
        <nav aria-label="Principal" className="flex gap-1 overflow-x-auto md:flex-col">
          {MAIN_LINKS.map((link) => (
            <NavLink key={link.to} to={link.to} end={link.end} className={linkClass}>
              {link.label}
            </NavLink>
          ))}
        </nav>
        <nav aria-label="Clubes" className="mt-3 hidden md:block">
          <p className="px-3 pb-1 text-xs text-muted-fg">Clubes da liga</p>
          {loading && <p className="px-3 text-sm text-muted-fg">Carregando…</p>}
          {error && <p className="px-3 text-sm text-danger">{error}</p>}
          {!loading && !error && clubs.length === 0 && (
            <p className="px-3 text-sm text-muted-fg">Nenhum clube cadastrado.</p>
          )}
          {clubs.map((club) => (
            <NavLink key={club.id} to={`/clubs/${club.id}`} className={linkClass}>
              {club.name}
            </NavLink>
          ))}
        </nav>
        <p className="mt-4 hidden px-3 text-xs text-muted-fg md:block">Horário de Brasília (UTC-3)</p>
      </aside>
      <main className="min-w-0 flex-1 p-4 sm:p-6">
        <Outlet />
      </main>
    </div>
  );
}
