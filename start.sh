#!/bin/bash
# Inicia backend e frontend simultaneamente

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "Iniciando StockIt..."

cd "$SCRIPT_DIR/frontend_simple"
# Usa 'python' (no Windows, 'python3' costuma ser o atalho falso da Microsoft Store,
# que retorna exit 0 mas nao executa; por isso checamos a string de versao real)
if python3 --version 2>&1 | grep -qi '^Python 3\.'; then PY=python3; else PY=python; fi
"$PY" -m http.server 3000 &
FRONTEND_PID=$!
echo "Frontend: http://localhost:3000"

cd "$SCRIPT_DIR/backend/stockit-backend"
echo "Backend: http://localhost:8081"
./mvnw spring-boot:run &
BACKEND_PID=$!

echo ""
echo "Para parar: kill $FRONTEND_PID $BACKEND_PID (ou Ctrl+C)"

trap "kill $FRONTEND_PID $BACKEND_PID 2>/dev/null; exit" INT TERM
wait
