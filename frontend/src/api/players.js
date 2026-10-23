import { request } from './http.js';

export const listPlayers = (params) => request('/players', { params });
export const createPlayer = (body) => request('/players', { method: 'POST', body });
export const updatePlayer = (id, body) => request(`/players/${id}`, { method: 'PUT', body });
export const changeCondition = (id, condition) =>
  request(`/players/${id}/condition`, { method: 'PATCH', body: { condition } });
export const deletePlayer = (id) => request(`/players/${id}`, { method: 'DELETE' });
export const buyPlayer = (id, body) => request(`/players/${id}/buy`, { method: 'POST', body });
export const sellPlayer = (id) => request(`/players/${id}/sell`, { method: 'POST' });
