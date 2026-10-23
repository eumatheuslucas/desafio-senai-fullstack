import { download, request } from './http.js';

export const listClubs = (sort) => request('/clubs', { params: { sort } });
export const getClub = (id) => request(`/clubs/${id}`);
export const createClub = (body) => request('/clubs', { method: 'POST', body });
export const getLineup = (id) => request(`/clubs/${id}/lineup`);
export const saveLineup = (id, body) => request(`/clubs/${id}/lineup`, { method: 'PUT', body });
export const listTransactions = (id, params) => request(`/clubs/${id}/transactions`, { params });
export const exportTransactions = (id, params) =>
  download(`/clubs/${id}/transactions/export`, params, `transacoes-clube-${id}.csv`);
