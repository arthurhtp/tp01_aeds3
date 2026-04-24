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

- **Node.js** (versão 18 ou superior)
- **NPM** (instalado junto com Node.js)
- **Java JDK** (versão 17 ou superior)
- **Apache Maven**
- **Git** (opcional, para clonar o repositório)

---

## Como executar o projeto

### 1. Instalar dependências do Node

Na pasta raiz do projeto(tp01_aeds3):

```bash
npm install
```

---

Na pasta de UI(frontend):

```bash
npm install
```

---

### 2. Executar o projeto

Na pasta raiz do projeto execute:

```bash
npm run dev
```

Este comando irá iniciar:

- **Backend**: aplicação Spring Boot
- **Frontend**: aplicação React (Vite)

---

### 3. Acessar o sistema

Após iniciar o projeto, o frontend estará disponível em:

```
http://localhost:PORT QUE O VITE MOSTRAR
```

O backend estará rodando na porta padrão do **Spring Boot (8081)**.

## Estrutura do Projeto

```
tp01_aeds3/
│
├── backend/stockit-backend/
│   ├── src/main/java/stockit/
│   │   ├── model/              → Entidades (Alimento, Registro, etc.)
│   │   ├── dao/                → Acesso a dados (HashExtensivel, DAOs)
│   │   ├── controller/         → Controllers REST (Spring Boot)
│   │   └── StockitBackendApplication.java
│   ├── src/test/java/stockit/  → Testes Unitários
│   ├── data/                   → Arquivos binários (.dat, .dir, .bkt)
│   └── pom.xml                 → Dependências Maven
│
├── frontend/StockIT/
│   ├── src/
│   │   ├── assets/images/      → Imagens
│   │   ├── components/         → Componentes React
│   │   ├── pages/              → Páginas da Aplicação
│   │   ├── services/           → Integração com API (Axios/Fetch)
│   │   └── types/              → Tipagens TypeScript
│   └── package.json            → Configurações npm (Vite)
│
└── docs/                       → Documentação do Projeto (PDFs)
```
