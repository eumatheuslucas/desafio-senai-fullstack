import { createContext, useContext, useMemo } from 'react';
import { listClubs } from '../../api/clubs.js';
import { useFetch } from '../../hooks/useFetch.js';

const ClubsContext = createContext(null);

export function ClubsProvider({ children }) {
  const { data, loading, error, reload } = useFetch(() => listClubs('name'), []);
  const value = useMemo(() => ({ clubs: data ?? [], loading, error, reload }), [data, loading, error, reload]);
  return <ClubsContext.Provider value={value}>{children}</ClubsContext.Provider>;
}

export function useClubs() {
  return useContext(ClubsContext);
}
