#!/bin/bash
# Popula o banco via API. Rode apos iniciar o backend.

BASE="http://localhost:8081"

echo "Populando banco de dados..."

# Categorias
curl -s -X POST "$BASE/categoria-alimento" -H "Content-Type: application/json" -d '{"nome":"Frutas"}' > /dev/null
curl -s -X POST "$BASE/categoria-alimento" -H "Content-Type: application/json" -d '{"nome":"Verduras"}' > /dev/null
curl -s -X POST "$BASE/categoria-alimento" -H "Content-Type: application/json" -d '{"nome":"Laticínios"}' > /dev/null
curl -s -X POST "$BASE/categoria-alimento" -H "Content-Type: application/json" -d '{"nome":"Carnes"}' > /dev/null
curl -s -X POST "$BASE/categoria-alimento" -H "Content-Type: application/json" -d '{"nome":"Grãos e Cereais"}' > /dev/null
curl -s -X POST "$BASE/categoria-alimento" -H "Content-Type: application/json" -d '{"nome":"Bebidas"}' > /dev/null
curl -s -X POST "$BASE/categoria-alimento" -H "Content-Type: application/json" -d '{"nome":"Congelados"}' > /dev/null
curl -s -X POST "$BASE/categoria-alimento" -H "Content-Type: application/json" -d '{"nome":"Temperos"}' > /dev/null
echo "  8 categorias"

# Alimentos
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Maçã","rotulos":["orgânico","nacional"],"idCategoriaAlimento":1}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Banana","rotulos":["prata","madura"],"idCategoriaAlimento":1}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Morango","rotulos":["orgânico"],"idCategoriaAlimento":1}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Laranja","rotulos":["pera","suco"],"idCategoriaAlimento":1}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Uva","rotulos":["sem semente","roxa"],"idCategoriaAlimento":1}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Alface","rotulos":["crespa","hidropônico"],"idCategoriaAlimento":2}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Tomate","rotulos":["italiano","salada"],"idCategoriaAlimento":2}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Cenoura","rotulos":["orgânico"],"idCategoriaAlimento":2}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Brócolis","rotulos":["fresco"],"idCategoriaAlimento":2}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Leite Integral","rotulos":["1L","pasteurizado"],"idCategoriaAlimento":3}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Queijo Minas","rotulos":["frescal","light"],"idCategoriaAlimento":3}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Iogurte Natural","rotulos":["sem açúcar","probiótico"],"idCategoriaAlimento":3}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Manteiga","rotulos":["com sal","200g"],"idCategoriaAlimento":3}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Peito de Frango","rotulos":["resfriado","bandeja"],"idCategoriaAlimento":4}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Carne Moída","rotulos":["patinho","magra"],"idCategoriaAlimento":4}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Filé de Tilápia","rotulos":["congelado","sem espinha"],"idCategoriaAlimento":4}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Linguiça Toscana","rotulos":["suína","churrasco"],"idCategoriaAlimento":4}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Arroz Branco","rotulos":["tipo 1","5kg"],"idCategoriaAlimento":5}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Feijão Carioca","rotulos":["novo","1kg"],"idCategoriaAlimento":5}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Macarrão Espaguete","rotulos":["500g","grano duro"],"idCategoriaAlimento":5}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Suco de Laranja","rotulos":["integral","1L"],"idCategoriaAlimento":6}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Água Mineral","rotulos":["sem gás","500ml"],"idCategoriaAlimento":6}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Café em Pó","rotulos":["torrado","500g"],"idCategoriaAlimento":6}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Pizza Congelada","rotulos":["mussarela","grande"],"idCategoriaAlimento":7}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Sorvete de Chocolate","rotulos":["2L","premium"],"idCategoriaAlimento":7}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Sal Refinado","rotulos":["iodado","1kg"],"idCategoriaAlimento":8}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Azeite de Oliva","rotulos":["extra virgem","500ml"],"idCategoriaAlimento":8}' > /dev/null
curl -s -X POST "$BASE/alimentos" -H "Content-Type: application/json" -d '{"nome":"Pimenta do Reino","rotulos":["moída","50g"],"idCategoriaAlimento":8}' > /dev/null
echo "  28 alimentos"

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
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":6,"ambienteId":1,"quantidade":1,"dataCadastro":"2025-05-01","dataVencimento":"2025-05-05"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":7,"ambienteId":1,"quantidade":4,"dataCadastro":"2025-05-03","dataVencimento":"2025-05-12"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":10,"ambienteId":1,"quantidade":2,"dataCadastro":"2025-05-01","dataVencimento":"2025-05-20"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":11,"ambienteId":1,"quantidade":1,"dataCadastro":"2025-05-02","dataVencimento":"2025-05-25"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":12,"ambienteId":1,"quantidade":3,"dataCadastro":"2025-05-01","dataVencimento":"2025-05-18"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":13,"ambienteId":1,"quantidade":1,"dataCadastro":"2025-05-04","dataVencimento":"2025-06-01"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":21,"ambienteId":1,"quantidade":2,"dataCadastro":"2025-05-01","dataVencimento":"2025-06-15"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":14,"ambienteId":2,"quantidade":3,"dataCadastro":"2025-04-20","dataVencimento":"2025-07-20"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":15,"ambienteId":2,"quantidade":2,"dataCadastro":"2025-04-22","dataVencimento":"2025-07-22"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":16,"ambienteId":2,"quantidade":4,"dataCadastro":"2025-04-25","dataVencimento":"2025-08-25"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":17,"ambienteId":2,"quantidade":2,"dataCadastro":"2025-05-01","dataVencimento":"2025-07-01"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":24,"ambienteId":2,"quantidade":1,"dataCadastro":"2025-05-03","dataVencimento":"2025-12-03"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":25,"ambienteId":2,"quantidade":1,"dataCadastro":"2025-05-03","dataVencimento":"2025-11-03"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":18,"ambienteId":3,"quantidade":1,"dataCadastro":"2025-04-10","dataVencimento":"2026-04-10"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":19,"ambienteId":3,"quantidade":2,"dataCadastro":"2025-04-10","dataVencimento":"2026-04-10"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":20,"ambienteId":3,"quantidade":3,"dataCadastro":"2025-04-15","dataVencimento":"2026-01-15"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":23,"ambienteId":3,"quantidade":2,"dataCadastro":"2025-05-01","dataVencimento":"2026-05-01"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":26,"ambienteId":3,"quantidade":1,"dataCadastro":"2025-04-01","dataVencimento":"2027-04-01"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":27,"ambienteId":3,"quantidade":1,"dataCadastro":"2025-04-01","dataVencimento":"2026-10-01"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":28,"ambienteId":3,"quantidade":1,"dataCadastro":"2025-04-01","dataVencimento":"2026-04-01"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":22,"ambienteId":4,"quantidade":6,"dataCadastro":"2025-05-05","dataVencimento":"2025-11-05"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":10,"ambienteId":4,"quantidade":1,"dataCadastro":"2025-05-05","dataVencimento":"2025-05-20"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":12,"ambienteId":4,"quantidade":2,"dataCadastro":"2025-05-05","dataVencimento":"2025-05-19"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":3,"ambienteId":5,"quantidade":10,"dataCadastro":"2025-04-15","dataVencimento":"2025-10-15"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":5,"ambienteId":5,"quantidade":5,"dataCadastro":"2025-04-20","dataVencimento":"2025-09-20"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":9,"ambienteId":5,"quantidade":3,"dataCadastro":"2025-05-01","dataVencimento":"2025-08-01"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":14,"ambienteId":5,"quantidade":5,"dataCadastro":"2025-04-28","dataVencimento":"2025-10-28"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":18,"ambienteId":6,"quantidade":2,"dataCadastro":"2025-03-01","dataVencimento":"2026-03-01"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":19,"ambienteId":6,"quantidade":3,"dataCadastro":"2025-03-01","dataVencimento":"2026-03-01"}' > /dev/null
curl -s -X POST "$BASE/itens_ambiente" -H "Content-Type: application/json" -d '{"alimentoId":26,"ambienteId":6,"quantidade":2,"dataCadastro":"2025-03-15","dataVencimento":"2027-03-15"}' > /dev/null
echo "  32 itens (N:N)"

echo ""
echo "Pronto. 8 categorias | 28 alimentos | 6 ambientes | 32 itens"
