#!/bin/bash
# Inicia backend (Spring Boot) e frontend (Python HTTP Server) simultaneamente

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "🚀 Iniciando StockIt..."
echo ""

# Inicia o frontend na porta 3000
cd "$SCRIPT_DIR/frontend_simple"
python3 -m http.server 3000 &
FRONTEND_PID=$!
echo "✅ Frontend rodando em http://localhost:3000 (PID: $FRONTEND_PID)"

# Inicia o backend na porta 8081
cd "$SCRIPT_DIR/backend/stockit-backend"
echo "⏳ Iniciando backend na porta 8081..."
./mvnw spring-boot:run &
BACKEND_PID=$!

echo ""
echo "📋 Para parar tudo: kill $FRONTEND_PID $BACKEND_PID"
echo "   Ou pressione Ctrl+C"
echo ""

# Captura Ctrl+C e mata os dois processos
trap "echo ''; echo '🛑 Parando...'; kill $FRONTEND_PID $BACKEND_PID 2>/dev/null; exit" INT TERM

# Espera ambos
wait
