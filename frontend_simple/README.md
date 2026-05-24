# StockIt - Frontend Simples

Frontend minimalista em HTML/CSS/JS para testar o backend do TP01 AEDS3.

## Como usar

1. **Inicie o backend** (porta 8081):
   ```bash
   cd ../backend/stockit-backend
   ./mvnw spring-boot:run
   ```

2. **Abra o frontend** no navegador:
   - Basta abrir o arquivo `index.html` diretamente no navegador
   - Ou use um servidor local: `python3 -m http.server 3000` e acesse `http://localhost:3000`

## Funcionalidades

Para cada entidade (CategoriaAlimento, Alimento, Ambiente, ItemAmbiente):

- **CREATE** - Formulário para criar novo registro
- **READ** - Listar todos ou buscar por ID
- **UPDATE** - Carregar registro por ID e editar
- **DELETE** - Excluir registro por ID
- **VER HASH** - Visualizar a estrutura do Hash Extensível (diretório + buckets)
- **VER CODIFICAÇÃO** - Ver como os dados estão codificados em bytes (hex + interpretação)
- **DADOS SALVOS** - Hex dump dos arquivos .dat, .dir e .bkt em disco

## Estrutura

```
frontend_simple/
├── index.html   → Página principal
├── style.css    → Estilos (tema escuro)
├── api.js       → Client HTTP para o backend
├── app.js       → Lógica da aplicação
└── README.md    → Este arquivo
```
