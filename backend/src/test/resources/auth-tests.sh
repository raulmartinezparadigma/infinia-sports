#!/bin/bash

# Script para probar los endpoints de autenticación
# Autor: Infinia Sports Team

# Colores para formatear la salida
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# URL base de la API
BASE_URL="http://localhost:8080/api/auth"

# Función para mostrar mensajes de información
info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

# Función para mostrar mensajes de éxito
success() {
    echo -e "${GREEN}[ÉXITO]${NC} $1"
}

# Función para mostrar mensajes de error
error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Función para comprobar si jq está instalado
check_jq() {
    if ! command -v jq &> /dev/null; then
        error "jq no está instalado. Por favor, instálalo para formatear la salida JSON."
        exit 1
    fi
}

# Comprobar si jq está instalado
check_jq

# Crear un usuario de prueba
register_user() {
    info "Registrando usuario de prueba..."
    
    RESPONSE=$(curl -s -X POST "$BASE_URL/register" \
        -H "Content-Type: application/json" \
        -d '{
            "username": "testuser",
            "password": "password123",
            "email": "test@example.com",
            "firstName": "Test",
            "lastName": "User"
        }')
    
    # Comprobar si la respuesta contiene un token
    if echo "$RESPONSE" | jq -e '.token' > /dev/null; then
        TOKEN=$(echo "$RESPONSE" | jq -r '.token')
        success "Usuario registrado correctamente. Token: ${TOKEN:0:20}..."
        echo "$RESPONSE" | jq '.'
        return 0
    else
        error "Error al registrar usuario:"
        echo "$RESPONSE" | jq '.'
        return 1
    fi
}

# Iniciar sesión con un usuario existente
login_user() {
    info "Iniciando sesión con usuario existente..."
    
    RESPONSE=$(curl -s -X POST "$BASE_URL/login" \
        -H "Content-Type: application/json" \
        -d '{
            "username": "testuser",
            "password": "password123"
        }')
    
    # Comprobar si la respuesta contiene un token
    if echo "$RESPONSE" | jq -e '.token' > /dev/null; then
        TOKEN=$(echo "$RESPONSE" | jq -r '.token')
        success "Inicio de sesión correcto. Token: ${TOKEN:0:20}..."
        echo "$RESPONSE" | jq '.'
        return 0
    else
        error "Error al iniciar sesión:"
        echo "$RESPONSE" | jq '.'
        return 1
    fi
}

# Intentar iniciar sesión con credenciales incorrectas
login_with_invalid_credentials() {
    info "Intentando iniciar sesión con credenciales incorrectas..."
    
    RESPONSE=$(curl -s -X POST "$BASE_URL/login" \
        -H "Content-Type: application/json" \
        -d '{
            "username": "testuser",
            "password": "wrongpassword"
        }')
    
    # Comprobar si la respuesta contiene un mensaje de error
    if echo "$RESPONSE" | jq -e '.message' > /dev/null; then
        success "Prueba de credenciales incorrectas exitosa. Se recibió un error como se esperaba:"
        echo "$RESPONSE" | jq '.'
        return 0
    else
        error "La prueba de credenciales incorrectas falló. Respuesta inesperada:"
        echo "$RESPONSE" | jq '.'
        return 1
    fi
}

# Intentar registrar un usuario con datos inválidos
register_with_invalid_data() {
    info "Intentando registrar un usuario con datos inválidos..."
    
    RESPONSE=$(curl -s -X POST "$BASE_URL/register" \
        -H "Content-Type: application/json" \
        -d '{
            "username": "",
            "password": "123",
            "email": "invalid-email"
        }')
    
    # Comprobar si la respuesta contiene errores de validación
    if [[ $(echo "$RESPONSE" | jq 'keys | length') -gt 0 ]]; then
        success "Prueba de datos inválidos exitosa. Se recibieron errores de validación como se esperaba:"
        echo "$RESPONSE" | jq '.'
        return 0
    else
        error "La prueba de datos inválidos falló. Respuesta inesperada:"
        echo "$RESPONSE" | jq '.'
        return 1
    fi
}

# Ejecutar todas las pruebas
run_all_tests() {
    info "Iniciando pruebas de autenticación..."
    
    # Registrar usuario
    register_user
    REGISTER_RESULT=$?
    
    # Iniciar sesión
    login_user
    LOGIN_RESULT=$?
    
    # Probar credenciales incorrectas
    login_with_invalid_credentials
    INVALID_LOGIN_RESULT=$?
    
    # Probar datos de registro inválidos
    register_with_invalid_data
    INVALID_REGISTER_RESULT=$?
    
    # Mostrar resumen de resultados
    echo ""
    info "Resumen de pruebas:"
    
    if [ $REGISTER_RESULT -eq 0 ]; then
        success "✅ Registro de usuario: EXITOSO"
    else
        error "❌ Registro de usuario: FALLIDO"
    fi
    
    if [ $LOGIN_RESULT -eq 0 ]; then
        success "✅ Inicio de sesión: EXITOSO"
    else
        error "❌ Inicio de sesión: FALLIDO"
    fi
    
    if [ $INVALID_LOGIN_RESULT -eq 0 ]; then
        success "✅ Prueba de credenciales incorrectas: EXITOSO"
    else
        error "❌ Prueba de credenciales incorrectas: FALLIDO"
    fi
    
    if [ $INVALID_REGISTER_RESULT -eq 0 ]; then
        success "✅ Prueba de datos de registro inválidos: EXITOSO"
    else
        error "❌ Prueba de datos de registro inválidos: FALLIDO"
    fi
}

# Ejecutar todas las pruebas
run_all_tests
