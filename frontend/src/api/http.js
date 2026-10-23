const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1';

export class ApiError extends Error {
  constructor(status, message) {
    super(message);
    this.status = status;
  }
}

function buildUrl(path, params = {}) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      query.set(key, String(value));
    }
  });
  const text = query.toString();
  return `${BASE_URL}${path}${text ? `?${text}` : ''}`;
}

async function send(path, { method = 'GET', body, params } = {}) {
  let response;
  try {
    response = await fetch(buildUrl(path, params), {
      method,
      headers: body === undefined ? undefined : { 'Content-Type': 'application/json' },
      body: body === undefined ? undefined : JSON.stringify(body),
    });
  } catch {
    throw new ApiError(0, 'Não foi possível conectar à API. Confira se o backend está no ar e tente de novo.');
  }
  if (!response.ok) {
    let message = 'A API devolveu um erro inesperado.';
    try {
      const data = await response.json();
      message = data.message || message;
    } catch {
      /* corpo sem JSON: mantém a mensagem padrão */
    }
    throw new ApiError(response.status, message);
  }
  return response;
}

export async function request(path, options) {
  const response = await send(path, options);
  return response.status === 204 ? null : response.json();
}

export async function download(path, params, filename) {
  const response = await send(path, { params });
  const url = URL.createObjectURL(await response.blob());
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.click();
  URL.revokeObjectURL(url);
}
