# Trabalho Prático -- AEDS III

## CRUD com Arquivos Sequenciais

**Disciplina:** Algoritmos e Estruturas de Dados III\
**Professor:** _Walisson Ferreira de Carvalho_\
**Instituição:** _PUC MINAS_

---

## 👥 Integrantes do grupo

- Arthur Henrique Tristão Pinto
- Davi Godoi Grilo
- Augusto Bizzaria

---

# StockIT – Projeto AEDS3

## Requisitos

Antes de executar o projeto, é necessário ter instalado:

- **Java JDK** (versão 21 ou superior)
- **Python 3** (para servir o frontend)
- **Git** (opcional, para clonar o repositório)

---

## Como executar o projeto

### 1. Iniciar backend e frontend

Na pasta raiz do projeto (tp01_aeds3):

```bash
./start.sh
```

Este comando irá iniciar:

- **Backend**: aplicação Spring Boot (porta 8081)
- **Frontend**: servidor HTTP Python (porta 3000)

---

### 2. Acessar o sistema

- Frontend: http://localhost:3000
- Backend: http://localhost:8081

## Estrutura do Projeto

```
tp01_aeds3/
│
├── backend/stockit-backend/
│   ├── src/main/java/stockit/
│   │   ├── model/              → Entidades (Alimento, Ambiente, CategoriaAlimento, ItemAmbiente)
│   │   ├── dao/                → Persistencia (Arquivo, HashExtensivel, ArvoreBMais, IntercalacaoBalanceada)
│   │   └── controller/         → Controllers REST (CRUD, visualizacao, ordenacao)
│   └── data/                   → Arquivos binarios (.dat, .dir, .bkt, .bplus)
│
├── frontend_simple/
│   ├── index.html
│   ├── style.css
│   └── js/
│       ├── api.js              → Client HTTP
│       ├── app.js              → Navegacao, tabs, CRUD generico
│       ├── categoria.js        → CategoriaAlimento
│       ├── alimento.js         → Alimento + N:N bidirecional
│       ├── ambiente.js         → Ambiente + N:N bidirecional + CRUD ItemAmbiente
│       └── visualizacao.js     → Hash, Codificacao, Arvore B+, Ordenacao
│
├── start.sh                    → Inicia backend + frontend
```
