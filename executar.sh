#!/bin/bash

echo ""
echo "╔════════════════════════════════════════════════════╗"
echo "║    Sistema de Pedidos - Arquitetura Serverless  ║"
echo "╚════════════════════════════════════════════════════╝"
echo ""

# Confere se o Java está instalado
if ! command -v java &> /dev/null; then
    echo "Java não está instalado!"
    echo ""
    echo "Para instalar:"
    echo "  sudo apt install openjdk-17-jdk -y"
    exit 1
fi

echo "✓ Java instalado: $(java -version 2>&1 | head -n 1 | cut -d'"' -f2)"

# Confere se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "Maven não está instalado!"
    echo ""
    echo "Para instalar:"
    echo "  sudo apt install maven -y"
    exit 1
fi

echo "Maven instalado: $(mvn -version | head -n 1 | awk '{print $3}')"
echo ""

echo "Compilando o projeto..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "Erro na compilação"
    exit 1
fi

echo "Compilação concluída"
echo ""

# Inicia o servidor local
mvn exec:java -Dexec.mainClass="com.pedidos.ServidorLocal" -q
