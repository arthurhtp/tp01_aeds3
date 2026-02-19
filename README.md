# Trabalho Prático 3 -- AEDS III

## CRUD com Arquivos Sequenciais (Array de Bytes)

**Disciplina:** Algoritmos e Estruturas de Dados III\
**Professor:** *Walisson Ferreira de Carvalho*
**Instituição:** *PUC MINAS*

------------------------------------------------------------------------

## 👥 Integrantes da dupla

-   Arthur Henrique Tristão Pinto\
-   Davi Godoi Grilo
-   Lucas Grossi

------------------------------------------------------------------------

## 📌 Descrição do Trabalho

Este trabalho tem como objetivo o desenvolvimento de uma aplicação
**Front-end + Back-end** utilizando persistência de dados em **arquivos
binários sequenciais**, manipulados diretamente como **array de bytes**,
sem a utilização de sistemas gerenciadores de banco de dados (SGBDs).

A aplicação implementa as quatro operações fundamentais de um sistema de
dados (**CRUD -- Create, Read, Update e Delete**) com armazenamento
físico em arquivo `.db`, seguindo o modelo tradicional de registros
variáveis com controle de exclusão lógica por **lápide**.

------------------------------------------------------------------------

## 🎯 Objetivos

-   Implementar persistência de dados utilizando **arquivo binário
    sequencial**.
-   Manipular registros através de **serialização manual para
    byte\[\]**.
-   Implementar **CRUD completo**.
-   Utilizar **exclusão lógica (lápide)**.
-   Manter controle de identificadores por meio de **cabeçalho no
    arquivo**.
-   Desenvolver uma interface web simples (HTML/CSS/JS ou framework)
    para interação com o sistema.
-   Aplicar arquitetura organizada em camadas (**MVC + DAO**).

------------------------------------------------------------------------

## 🧠 Conceitos Aplicados

-   Estrutura de Arquivos\
-   Registros de tamanho variável\
-   Serialização manual (`toByteArray()` / `fromByteArray()`)\
-   Exclusão lógica\
-   Varredura sequencial\
-   Organização em camadas\
-   Integração Front-end e Back-end

------------------------------------------------------------------------

## 🗂 Estrutura do Arquivo Binário

### 🔹 Cabeçalho

    int ultimoId

Responsável por armazenar o último identificador utilizado no sistema.

### 🔹 Estrutura de cada Registro

    byte lapide
    int tamanho
    byte[] payload

-   `' '` → Registro ativo\
-   `'*'` → Registro excluído logicamente\
-   `tamanho` → Quantidade de bytes do objeto serializado\
-   `payload` → Dados do objeto convertidos para array de bytes

------------------------------------------------------------------------

## 🔁 Operações CRUD

### ✔ Create

-   Gera novo ID automaticamente.
-   Serializa o objeto para byte\[\].
-   Grava o registro no final do arquivo.

### ✔ Read

-   Realiza busca sequencial pelo ID.
-   Ignora registros marcados como excluídos.

### ✔ Update

-   Se o novo registro possuir tamanho menor ou igual ao original,
    sobrescreve no mesmo local.
-   Caso contrário, marca o registro antigo com lápide e grava o novo no
    final do arquivo.

### ✔ Delete

-   Marca o registro como excluído utilizando lápide (`'*'`).

------------------------------------------------------------------------

## 🏗 Arquitetura do Projeto

### 📂 Model

Representação das entidades do sistema.

### 📂 DAO

Responsável pela manipulação direta do arquivo binário.

### 📂 Controller

Gerencia regras de negócio e comunicação com o front-end.

### 📂 View (Front-end)

Interface web para interação com o usuário.

------------------------------------------------------------------------

## 🌐 Tecnologias Utilizadas

-   Linguagem de Programação: *(ex: Java)*\
-   Manipulação de Arquivos: `RandomAccessFile`\
-   Front-end: *(HTML/CSS/JavaScript ou framework utilizado)*\
-   Comunicação: API REST

------------------------------------------------------------------------

## 📈 Justificativa Técnica

A utilização de arquivos sequenciais com registros em array de bytes
permite:

-   Compreensão aprofundada da persistência física de dados.
-   Controle manual da estrutura dos registros em disco.
-   Simulação do funcionamento interno de sistemas gerenciadores de
    banco de dados.
-   Aplicação prática dos conceitos de organização de arquivos estudados
    em AEDS III.

------------------------------------------------------------------------

## 🚀 Execução do Projeto

*(Inserir instruções específicas após implementação.)*

Exemplo:

``` bash
# Executar backend
./run.sh

# Acessar frontend
http://localhost:8080
```

------------------------------------------------------------------------

## 📚 Conclusão

Este trabalho consolida os conhecimentos de organização de arquivos,
manipulação binária e arquitetura de sistemas, aplicando conceitos
fundamentais de Estruturas de Dados em um sistema completo com interface
web e persistência própria.
