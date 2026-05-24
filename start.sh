#!/bin/bash
# Inicia backend e frontend simultaneamente

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "Iniciando StockIt..."

cd "$SCRIPT_DIR/frontend_simple"
python3 -m http.server 3000 &
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
