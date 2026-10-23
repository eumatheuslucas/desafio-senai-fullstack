# Passe – Estudo de caso SENAI/PE (Desenvolvedor Fullstack I)

API REST da liga de futebol fictícia (jogadores, clubes, mercado, escalação, partidas e classificação).

## 1. Pré-requisitos

- Java 21
- Maven 3.9
- Docker 24 com Docker Compose v2 (só para o MySQL 8.0)
- Node.js 22 com npm (frontend – ver seção "Funcionalidades não implementadas")

## 2. Banco de dados

O banco é MySQL 8.0, criado vazio por container. As tabelas são criadas por migration versionada
(Flyway, `backend/src/main/resources/db/migration`) quando o backend inicia.

```bash
# criar (na raiz do repositório)
docker compose up -d db

# recriar do zero (apaga o volume e cria de novo; reinicie o backend depois)
docker compose down -v
docker compose up -d db
```

O nome do clube é único no banco pela collation `utf8mb4_0900_as_ci` (ignora maiúsculas e minúsculas, mas
considera acentos), conforme a RN10.

## 3. Backend

```bash
# na raiz do repositório
docker compose up -d db

# na pasta backend
cd backend
cp .env.example .env
mvn spring-boot:run
```

- Origem da API: `http://localhost:8080` (o prefixo `/api/v1` já faz parte dos caminhos)
- Documentação interativa (Swagger): `http://localhost:8080/swagger-ui.html`
- Testes automatizados (terminam sozinhos): `cd backend && mvn test`

## 3b. Frontend

```bash
# em outro terminal, a partir da raiz do repositório
cd frontend
cp .env.example .env
npm ci
npm run dev
```

- URL da interface: `http://localhost:5173` (a API precisa estar no ar em `http://localhost:8080`)
- Testes do frontend (terminam sozinhos): `cd frontend && npm test`
- O arquivo `frontend/package-lock.json` é gerado por `npm install` e deve ser versionado.

## 4. Variáveis de ambiente (`backend/.env.example`)

| Variável | Uso |
|---|---|
| `DB_URL` | URL JDBC do MySQL |
| `DB_USER` / `DB_PASSWORD` | Credenciais do banco |
| `CORS_ALLOWED_ORIGIN` | Única origem liberada no CORS (o frontend) |
| `PORT` | Porta da API (padrão 8080) |

Frontend (`frontend/.env.example`): `VITE_API_URL` (padrão `http://localhost:8080/api/v1`).

## 5. Estrutura e bibliotecas

```
backend/src/main/java/br/gov/senai/passe/
  config/    relógio (-03:00), CORS, prefixo /api/v1, log de requisições
  shared/    erros e tratamento central, validação de entrada, utilitários (dinheiro, datas, texto)
  player/    jogadores: cadastro, edição, condição, exclusão lógica, listagem, valor base e de mercado
  club/      clubes, elenco, valor do elenco e patrimônio
  contract/  contratos e regras de fidelidade (dias restantes, multa)
  market/    compra, venda, histórico e exportação CSV
  lineup/    escalação
  match/     partidas (duelos), histórico e classificação

frontend/src/
  api/       chamadas à API (única camada que usa fetch)
  components/ componentes compartilhados (tabela, campo, modal, estados de lista, alertas)
  hooks/     useFetch, useAction (evita envio duplo), useForm, useDebounce
  features/  players, clubs, league (páginas e modais)
  lib/       formatação, constantes e validações de formulário
```

| Biblioteca | Motivo |
|---|---|
| Spring Web | API REST |
| Spring Data JPA (Hibernate) | Acesso ao banco |
| MySQL Connector/J | Driver do MySQL |
| Flyway (core e mysql) | Migrations versionadas |
| springdoc-openapi | Documentação Swagger/OpenAPI |
| Lombok | Menos código repetitivo nas entidades e injeções |
| JUnit 5 (spring-boot-starter-test) | Testes automatizados |
| React 18 e Vite | Interface e servidor de desenvolvimento |
| React Router | Navegação entre telas |
| Tailwind CSS (com PostCSS e Autoprefixer) | Estilo |
| Vitest, Testing Library e jsdom | Testes do frontend |

## 6. Decisões e premissas

- Datas guardadas em `DATETIME(3)` no horário de Brasília (-03:00), sem conversão de fuso.
- A validação do corpo é feita à mão, para recusar o que o enunciado manda recusar: texto no lugar de
  número, decimal em campo inteiro, enum em minúsculas e corpo que não é objeto JSON.
- Ids inválidos na URL (não numéricos, zero, com zeros à esquerda ou acima de 2.147.483.647) respondem 404.
- Listagem de jogadores: o valor de mercado não é coluna, então filtros, ordenação e paginação são
  aplicados em memória depois de calculá-lo.
- Compra e venda travam jogador e clube (lock pessimista) e rodam numa única transação.
- Ordem das verificações: formato (400), existência (404) e regra de negócio (409).
- Método não previsto num caminho existente responde 405.

## 7. Funcionalidades não implementadas

- Lint e formatação (backend e frontend): não configurados.
- Docker Compose da aplicação inteira: o compose atual sobe só o banco.

## 8. Uso de inteligência artificial

Ferramenta: Claude (Anthropic).
Onde: backend (`backend/`), frontend (`frontend/`), `docker-compose.yml` e este README.
Como e por quê: o código foi gerado por IA a partir do enunciado, para ganhar tempo no prazo curto.
O que aceitei, alterei ou descartei: **preencher com o que você realmente revisou e mudou antes de entregar.**
