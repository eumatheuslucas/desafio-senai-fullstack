import { getMatch } from '../../api/league.js';
import DataState from '../../components/DataState.jsx';
import Modal from '../../components/Modal.jsx';
import { useFetch } from '../../hooks/useFetch.js';
import MatchResult from './MatchResult.jsx';

export default function MatchDetailModal({ matchId, onClose }) {
  const { data, loading, error, reload } = useFetch(() => getMatch(matchId), [matchId]);
  return (
    <Modal title="Duelos da partida" size="xl" onClose={onClose}>
      <DataState loading={loading} error={error} onRetry={reload}>
        {data && <MatchResult match={data} />}
      </DataState>
    </Modal>
  );
}
