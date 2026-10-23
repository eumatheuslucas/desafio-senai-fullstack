import { request } from './http.js';

export const playMatch = (body) => request('/matches', { method: 'POST', body });
export const listMatches = (clubId) => request('/matches', { params: { club_id: clubId } });
export const getMatch = (id) => request(`/matches/${id}`);
export const getStandings = () => request('/standings');
