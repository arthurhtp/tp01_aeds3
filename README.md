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

> Observação: ambientes cadastrados antes da Fase V (gravados em texto claro) ficam ilegíveis após a
> introdução da cifra. Os dados de `Ambiente` foram reiniciados; basta recadastrar os ambientes.

---

## Formulário técnico (Fase V)

**1. Qual campo textual foi escolhido para os algoritmos de casamento de padrões? Por quê?**
O campo `nome` da tabela **Alimento**. É o campo textual mais relevante para o usuário, com maior
variedade de valores e o caso de uso mais natural de pesquisa ("encontrar alimentos cujo nome contém
um padrão").

**2. Funcionamento do KMP implementado** (`stockit.busca.KMP`)
Pré-processa o padrão construindo a tabela de falha (LPS — *Longest Proper Prefix which is also
Suffix*). Durante a comparação, ao ocorrer divergência, o ponteiro do texto **nunca** retrocede: usa-se
a tabela LPS para reposicionar apenas o ponteiro do padrão, reaproveitando o trecho já casado.
Complexidade O(n + m). A comparação é case-insensitive.

**3. Funcionamento do Boyer–Moore implementado** (`stockit.busca.BoyerMoore`)
Compara o padrão com o texto **da direita para a esquerda** e desliza o padrão usando duas heurísticas,
escolhendo sempre o maior salto: **bad character** (obrigatória) — alinha a última ocorrência do
caractere divergente no padrão; e **good suffix** (opcional, implementada) — reaproveita o sufixo que já
casou. Sublinear no caso médio. Também case-insensitive.

**4. Como os algoritmos foram integrados ao sistema**
`AlimentoDAO.buscarPorPadraoNome(padrao, algoritmo)` percorre os registros do arquivo e filtra pelo
nome usando KMP ou BM. O `BuscaPadraoController` expõe `GET /busca-padrao/alimentos` (parâmetros
`padrao` e `algoritmo`). No frontend, a aba **Buscar padrao** (em Alimento) oferece o menu de pesquisa,
a escolha do algoritmo, o campo de padrão e a tabela de resultados.

**5. Dificuldades encontradas**
A principal foi o pré-processamento da heurística de *good suffix* do Boyer–Moore (cálculo das bordas e
dos dois casos de deslocamento). Também foi necessário usar `HashMap` na tabela de *bad character* para
suportar caracteres acentados/Unicode, em vez do vetor ASCII de 256 posições.

**6. Qual campo foi utilizado na criptografia?**
O campo `nome` da entidade **Ambiente** (considerado sensível por revelar o local de armazenamento).

**7. Qual foi o método utilizado na criptografia?**
Cifra simétrica **XOR** (Vernam com chave repetida ciclicamente). A mesma operação cifra e decifra, pois
`(D ^ K) ^ K == D`. Implementada em `stockit.seguranca.XORCipher`.
