import { Navigate, Route, Routes } from 'react-router-dom';
import ClubPage from './features/clubs/ClubPage.jsx';
import ClubsPage from './features/clubs/ClubsPage.jsx';
import MatchesPage from './features/league/MatchesPage.jsx';
import StandingsPage from './features/league/StandingsPage.jsx';
import PlayersPage from './features/players/PlayersPage.jsx';
import Layout from './layout/Layout.jsx';

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<Navigate to="/players" replace />} />
        <Route path="/players" element={<PlayersPage />} />
        <Route path="/clubs" element={<ClubsPage />} />
        <Route path="/clubs/:id" element={<ClubPage />} />
        <Route path="/matches" element={<MatchesPage />} />
        <Route path="/standings" element={<StandingsPage />} />
        <Route path="*" element={<p className="py-10 text-center text-sm text-muted-fg">Página não encontrada.</p>} />
      </Route>
    </Routes>
  );
}
