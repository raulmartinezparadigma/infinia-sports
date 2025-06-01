#!/bin/bash
# Script para arrancar backend (Spring Boot) y frontend (React) de Infinia Sports
# Ejecutar desde Git Bash en Windows

# Arrancar backend en segundo plano
cd ./backend
echo "Iniciando backend (Spring Boot, puerto 8080)..."
mvn spring-boot:run -Dspring-boot.run.profiles=dev &
BACKEND_PID=$!
cd ..

# Arrancar frontend en primer plano
cd ./frontend-temp
echo "Iniciando frontend (React, puerto 3000)..."
npm start

# Al cerrar el frontend, detener el backend automáticamente
kill $BACKEND_PID
