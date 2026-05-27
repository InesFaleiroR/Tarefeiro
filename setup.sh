#!/bin/bash
echo "============================================================"
echo "  O TAREFEIRO - Setup Inicial (macOS / Linux)"
echo "============================================================"

echo ""
echo "[1/3] A verificar Java 21..."
if ! java -version 2>&1 | grep -q "21"; then
    echo "ERRO: Java 21 não encontrado. Instala em https://adoptium.net"
    exit 1
fi
echo "    Java OK"

echo ""
echo "[2/3] A descarregar todas as dependências Maven..."
echo "    (Pode demorar 2-5 minutos na primeira vez - precisa de internet)"
chmod +x ./mvnw
./mvnw dependency:go-offline -q
if [ $? -ne 0 ]; then
    echo "ERRO: Falhou o download das dependências."
    exit 1
fi
echo "    Dependências OK"

echo ""
echo "[3/3] A compilar o projeto..."
./mvnw clean compile -DskipTests -q
if [ $? -ne 0 ]; then
    echo "ERRO: Compilação falhou."
    exit 1
fi
echo "    Compilação OK"

echo ""
echo "============================================================"
echo "  SUCESSO! Para correr a app:"
echo "    ./mvnw spring-boot:run"
echo "  Depois abre:  http://localhost:8080"
echo "  Login:        admin@tarefeiro.pt / admin123"
echo "============================================================"
