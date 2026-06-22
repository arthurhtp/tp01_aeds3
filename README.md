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
│   │   ├── busca/             → Casamento de padroes (KMP, BoyerMoore)        [Fase V]
│   │   ├── seguranca/        → Criptografia (XORCipher)                       [Fase V]
│   │   └── controller/         → Controllers REST (CRUD, visualizacao, ordenacao, busca-padrao)
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
│       ├── visualizacao.js     → Hash, Codificacao, Arvore B+, Ordenacao
│       ├── compressao.js       → Compressao Huffman / LZW
│       └── busca.js           → Pesquisa por padrao (KMP / BM)               [Fase V]
│
├── start.sh                    → Inicia backend + frontend
```

---

## Fase V — Casamento de padrões e Criptografia

### Como usar a busca por padrão (KMP / Boyer–Moore)

1. Abra o frontend em http://localhost:3000 e clique na entidade **Alimento**.
2. Clique na aba **Buscar padrao**.
3. Escolha o algoritmo (**KMP** ou **Boyer–Moore**), digite o padrão (string) e clique em **Pesquisar**.
4. O sistema retorna os alimentos cujo **nome** contém o padrão, com o tempo de execução e destaque das ocorrências. A busca não diferencia maiúsculas/minúsculas.

Também é possível consultar a API diretamente:

```
GET http://localhost:8081/busca-padrao/alimentos?padrao=arr&algoritmo=kmp
GET http://localhost:8081/busca-padrao/alimentos?padrao=arr&algoritmo=bm
```

### Criptografia (XOR)

O campo **`nome` da entidade `Ambiente`** é tratado como sensível e gravado **cifrado** no arquivo
`data/Ambiente/Ambiente.dat` usando a cifra XOR (`stockit.seguranca.XORCipher`). A cifragem é
transparente: ocorre na serialização (`Ambiente.toByteArray`) e a decifragem na leitura
(`Ambiente.fromByteArray`), de modo que a API e a interface sempre exibem o texto claro,
mas o conteúdo bruto do arquivo permanece ilegível.

Na listagem de **Ambiente**, a coluna **Nome** mostra o texto real e há um botão
**criptografar / descriptografar** por linha, que alterna a exibição entre o nome claro e o
nome cifrado em hexadecimal.

> Observação: ambientes cadastrados antes da Fase V (gravados em texto claro) ficam ilegíveis após a
> introdução da cifra. Os dados de `Ambiente` foram reiniciados; basta recadastrar os ambientes.

> O formulário técnico da Fase V está em [`FASE5/formulario_tecnico.txt`](FASE5/formulario_tecnico.txt).
