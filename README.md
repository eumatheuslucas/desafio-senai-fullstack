# Passe – Estudo de Caso SENAI/PE (Desenvolvedor Fullstack I)

O **Passe** é uma solução Fullstack completa desenvolvida para gestão de uma liga de futebol profissional, cobrindo o ciclo completo de atletas, contratos, valoração de mercado, gerenciamento de elencos, escalações táticas, simulação de partidas e classificação em tempo real.

---

## 🚀 Diferenciais de Engenharia e Arquitetura

- **Validação Rigorosa em Camadas (`JsonBodyReader`):** Implementação de leitor customizado de JSON para rejeição estrita de entradas inválidas no nível HTTP (recusa de decimais em campos inteiros, enums fora do padrão case-sensitive, conversões automáticas indesejadas e estruturas divergentes).
- **Consistência de Transação e Lock Pessimista:** Operações de compra e venda de jogadores com concorrência segura, garantindo isolamento total em banco de dados e atualização atômica de saldo, contratos e histórico.
- **Tratamento Global e Uniforme de Exceções:** Padrão centralizado de respostas de erro (`ApiException`, `GlobalExceptionHandler`), garantindo retornos HTTP 400, 404, 405 e 409 com corpos padronizados e informativos.
- **Normalização de Dados e Regras Especiais:** Suporte a buscas com insensibilidade a acentos/caixa alta (`TextNormalizer`, collation `utf8mb4_0900_as_ci`), além de ordenação dinamicamente calculada em memória para métricas financeiras.
- **Padronização Temporal Unificada:** Fuso horário travado estritamente em Brasília (`America/Sao_Paulo`, GMT-3) para auditoria e histórico de transações com precisão de milissegundos.
- **Interface Reativa com Estado Robusto:** Frontend em React + Tailwind CSS com controle de submissão duplicada (`useAction`), estado desacoplado, busca com *debounce* e atualização imediata de tabelas e simulações.

---

## 1. Pré-requisitos

Para execução local ou containerizada da aplicação, certifique-se de possuir instalado:

- **Java Development Kit (JDK):** Versão 21 ou superior
- **Apache Maven:** Versão 3.9+
- **Node.js:** Versão 22+ com **npm**
- **Docker & Docker Compose:** Docker 24+ com Docker Compose v2 (para o serviço de banco de dados MySQL 8.0)

---

## 2. Banco de Dados

A aplicação utiliza o **MySQL 8.0** configurado para manipulação textual avançada (Collation `utf8mb4_0900_as_ci` para unicitização e agrupamento insensível a acentos/caixa).

A criação e evolução do esquema são gerenciadas automaticamente pelo **Flyway** (`backend/src/main/resources/db/migration`).

### Subir o Banco de Dados via Docker Compose:
```bash
# Na raiz do repositório
docker compose up -d db
```

### Resetar o Banco de Dados (opcional):
```bash
docker compose down -v
docker compose up -d db
```

---

## 3. Instalação e Execução

### 3.1. Backend (Spring Boot 3 / Java 21)

```bash
# A partir da raiz do projeto
cd backend

# Copiar arquivo de variáveis de ambiente
cp .env.example .env

# Executar a aplicação
mvn spring-boot:run
```

- **Origem da API:** `http://localhost:8080` (prefixo base `/api/v1`)
- **Swagger / OpenAPI:** `http://localhost:8080/swagger-ui.html`
- **Suíte de Testes Automatizada:** `mvn test`

### 3.2. Frontend (React 18 / Vite / Tailwind CSS)

```bash
# Em outro terminal, a partir da raiz do projeto
cd frontend

# Copiar arquivo de variáveis de ambiente
cp .env.example .env

# Instalar dependências e iniciar servidor de desenvolvimento
npm ci
npm run dev
```

- **Interface Web:** `http://localhost:5173`
- **Suíte de Testes Frontend:** `npm test`

---

## 4. Variáveis de Ambiente

### Backend (`backend/.env.example`)
| Variável | Descrição | Valor Padrão |
| :--- | :--- | :--- |
| `DB_URL` | URL JDBC de conexão com MySQL | `jdbc:mysql://localhost:3306/passe` |
| `DB_USER` | Usuário do Banco de Dados | `root` |
| `DB_PASSWORD` | Senha do Banco de Dados | `root` |
| `CORS_ALLOWED_ORIGIN` | Origem liberada para integração CORS | `http://localhost:5173` |
| `PORT` | Porta de execução do serviço REST | `8080` |

### Frontend (`frontend/.env.example`)
| Variável | Descrição | Valor Padrão |
| :--- | :--- | :--- |
| `VITE_API_URL` | Endpoint base da API Backend | `http://localhost:8080/api/v1` |

---

## 5. Estrutura do Projeto e Principais Bibliotecas

```text
passe/
├── backend/                  # Módulo Spring Boot 3
│   └── src/main/java/br/gov/senai/passe/
│       ├── config/           # Configurações gerais (CORS, Fuso Horário, Logs)
│       ├── shared/           # Tratamento de erros, validações customizadas e utilitários
│       ├── player/           # Módulo de Jogadores (métrica de valuation e ciclo de vida)
│       ├── club/             # Módulo de Clubes e Elencos
│       ├── contract/         # Módulo de Contratos e Fidelidade
│       ├── market/           # Módulo de Mercado (Compra, Venda e Histórico/CSV)
│       ├── lineup/           # Módulo de Escalação Tática
│       └── match/            # Engine de Partidas e Tabela de Classificação
├── frontend/                 # Módulo React 18
│   └── src/
│       ├── api/              # Cliente HTTP e integração com rotas backend
│       ├── components/       # Componentes reutilizáveis de UI
│       ├── features/         # Módulos de tela (Clubes, Jogadores, Liga)
│       └── hooks/            # Custom Hooks (Debounce, Submissão, Form)
└── docker-compose.yml        # Orquestração do Banco de Dados MySQL 8
```

### Bibliotecas Principais
- **Backend:** Spring Boot 3, Spring Data JPA, Hibernate, MySQL Connector/J, Flyway Core, Springdoc OpenAPI, Lombok, JUnit 5.
- **Frontend:** React 18, Vite, React Router, Tailwind CSS, PostCSS, Autoprefixer, Vitest, Testing Library.

---

## 6. Decisões Arquiteturais e Premissas Técnicas

1. **Atribuição Temporal:** Registro de datas em formato `DATETIME(3)` no fuso de Brasília (`America/Sao_Paulo`, GMT-3), evitando distorções de conversão.
2. **Validação de Payload Personalizada:** Tratamento direto para interceptar incompatibilidades de tipos em requisições antes do processamento de negócio.
3. **Mapeamento REST de Erros:** Respostas ajustadas para parâmetros inválidos ou fora de faixa (ex: identificadores no caminho com valor zero ou estourando limites de inteiros retornam `404 Not Found`).
4. **Isolamento Concorrente:** Mecanismo de bloqueio pessimista (*pessimistic locking*) na execução de transações financeiras e alteração de elenco.
5. **Cálculo Financeiro Dinâmico:** Atualização e cálculo de valor de mercado aplicado dinamicamente com suporte a ordenação e paginação segura.

---

## 7. Escopo de Entrega e Suporte de Infraestrutura

- **Containerização do Banco:** O ambiente utiliza Docker Compose focado em prover o banco relacional MySQL com persistência e isolamento.
- **Execução Híbrida e Independente:** Módulos de aplicação configurados para inicialização direta pelos CLI nativos (`mvn` e `npm`), garantindo rapidez no ciclo de desenvolvimento e teste.

---

## 8. Uso de Ferramentas de Inteligência Artificial

- **Ferramenta Utilizada:** Anthropic Claude.
- **Aplicação:** Auxílio na estruturação de padrões de arquitetura, geração de boilerplate de testes automatizados e aceleração na construção das rotas da API REST e componentes de interface.
- **Validação Humana:** Todo o código gerado foi auditado, refatorado, testado e validado manualmente para garantir conformidade integral com as regras de negócio do edital.