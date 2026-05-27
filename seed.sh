#!/bin/bash
# Popula o banco via API. Rode apos iniciar o backend.

BASE="http://localhost:8081"

echo "Populando banco de dados..."

# Categorias
curl -s -X POST "$BASE/categoria-alimento" -H "Content-Type: application/json" -d '{"nome":"Frutas"}' > /dev/null
curl -s -X POST "$BASE/categoria-alimento" -H "Content-Type: application/json" -d '{"nome":"Verduras"}' > /dev/null
curl -s -X POST "$BASE/categoria-alimento" -H "Content-Type: application/json" -d '{"nome":"Laticínios"}' > /dev/null
curl -s -X POST "$BASE/categoria-alimento" -H "Content-Type: application/json" -d '{"nome":"Carnes"}' > /dev/null
echo "  4 categorias"

# Alimentos
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Maçã","rotulos":["orgânico","nacional"],"idCategoriaAlimento":1}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Banana","rotulos":["prata","madura"],"idCategoriaAlimento":1}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Morango","rotulos":["orgânico"],"idCategoriaAlimento":1}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Laranja","rotulos":["pera","suco"],"idCategoriaAlimento":1}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Alface","rotulos":["crespa","hidropônico"],"idCategoriaAlimento":2}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Tomate","rotulos":["italiano","salada"],"idCategoriaAlimento":2}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Cenoura","rotulos":["orgânico"],"idCategoriaAlimento":2}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Brócolis","rotulos":["fresco"],"idCategoriaAlimento":2}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Leite Integral","rotulos":["1L","pasteurizado"],"idCategoriaAlimento":3}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Queijo Minas","rotulos":["frescal","light"],"idCategoriaAlimento":3}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Iogurte Natural","rotulos":["sem açúcar","probiótico"],"idCategoriaAlimento":3}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Peito de Frango","rotulos":["resfriado","bandeja"],"idCategoriaAlimento":4}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Carne Moída","rotulos":["patinho","magra"],"idCategoriaAlimento":4}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Filé de Tilápia","rotulos":["congelado","sem espinha"],"idCategoriaAlimento":4}' > /dev/null
echo "  14 alimentos"

# Ambientes
curl -s -X POST "$BASE/ambientes" -H "Content-Type: application/json" -d '{"nome":"Geladeira Principal","tipo":0}' > /dev/null
curl -s -X POST "$BASE/ambientes" -H "Content-Type: application/json" -d '{"nome":"Freezer Vertical","tipo":1}' > /dev/null
curl -s -X POST "$BASE/ambientes" -H "Content-Type: application/json" -d '{"nome":"Despensa Cozinha","tipo":2}' > /dev/null
curl -s -X POST "$BASE/ambientes" -H "Content-Type: application/json" -d '{"nome":"Geladeira Escritório","tipo":0}' > /dev/null
curl -s -X POST "$BASE/ambientes" -H "Content-Type: application/json" -d '{"nome":"Freezer Horizontal","tipo":1}' > /dev/null
curl -s -X POST "$BASE/ambientes" -H "Content-Type: application/json" -d '{"nome":"Despensa Garagem","tipo":2}' > /dev/null
echo "  6 ambientes"

# Itens (N:N entre Alimento e Ambiente)
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":1,"ambienteId":1,"quantidade":5,"dataCadastro":"2025-05-01","dataVencimento":"2025-05-15"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":2,"ambienteId":1,"quantidade":8,"dataCadastro":"2025-05-02","dataVencimento":"2025-05-10"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":5,"ambienteId":1,"quantidade":1,"dataCadastro":"2025-05-01","dataVencimento":"2025-05-05"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":6,"ambienteId":1,"quantidade":4,"dataCadastro":"2025-05-03","dataVencimento":"2025-05-12"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":9,"ambienteId":1,"quantidade":2,"dataCadastro":"2025-05-01","dataVencimento":"2025-05-20"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":10,"ambienteId":1,"quantidade":1,"dataCadastro":"2025-05-02","dataVencimento":"2025-05-25"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":12,"ambienteId":2,"quantidade":3,"dataCadastro":"2025-04-20","dataVencimento":"2025-07-20"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":13,"ambienteId":2,"quantidade":2,"dataCadastro":"2025-04-22","dataVencimento":"2025-07-22"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":14,"ambienteId":2,"quantidade":4,"dataCadastro":"2025-04-25","dataVencimento":"2025-08-25"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":3,"ambienteId":3,"quantidade":6,"dataCadastro":"2025-05-05","dataVencimento":"2025-11-05"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":4,"ambienteId":3,"quantidade":3,"dataCadastro":"2025-05-05","dataVencimento":"2025-09-20"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":7,"ambienteId":3,"quantidade":2,"dataCadastro":"2025-05-01","dataVencimento":"2025-05-18"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":8,"ambienteId":4,"quantidade":1,"dataCadastro":"2025-05-04","dataVencimento":"2025-06-01"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":11,"ambienteId":4,"quantidade":2,"dataCadastro":"2025-05-01","dataVencimento":"2025-06-15"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":1,"ambienteId":5,"quantidade":10,"dataCadastro":"2025-04-15","dataVencimento":"2025-10-15"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":9,"ambienteId":5,"quantidade":3,"dataCadastro":"2025-05-01","dataVencimento":"2025-08-01"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":12,"ambienteId":6,"quantidade":2,"dataCadastro":"2025-03-01","dataVencimento":"2026-03-01"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":14,"ambienteId":6,"quantidade":3,"dataCadastro":"2025-03-01","dataVencimento":"2026-03-01"}' > /dev/null
echo "  18 itens"

echo ""
echo "Pronto. 4 categorias | 14 alimentos | 6 ambientes | 18 itens"
