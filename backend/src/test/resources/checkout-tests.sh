#!/bin/bash
# Script para probar los endpoints del módulo de checkout

# URL base (ajustar según configuración)
BASE_URL="http://localhost:8080"

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir separadores
print_separator() {
  echo -e "${BLUE}----------------------------------------${NC}"
}

# Función para imprimir títulos
print_title() {
  print_separator
  echo -e "${GREEN}$1${NC}"
  print_separator
}

# Generar un ID de sesión único para las pruebas
SESSION_ID=$(uuidgen)
echo -e "${GREEN}ID de sesión para pruebas:${NC} $SESSION_ID"

# Variable para almacenar el ID del carrito
CART_ID=""

# Variable para almacenar el ID del producto en el carrito
ITEM_ID=""

# Variable para almacenar el ID del pedido
ORDER_ID=""

# 1. Añadir producto al carrito
print_title "1. Añadir producto al carrito"
RESPONSE=$(curl -s -X POST "$BASE_URL/carrito/items" \
  -H "Content-Type: application/json" \
  -H "User-ID: test-user" \
  -H "Cookie: SESSION_ID=$SESSION_ID" \
  -d '{
    "productId": "PROD-001",
    "productName": "Balón de fútbol profesional",
    "quantity": 2,
    "unitPrice": 49.99,
    "attributes": {
      "color": "blanco",
      "tamaño": "5"
    }
  }')

echo "$RESPONSE" | jq .
CART_ID=$(echo "$RESPONSE" | jq -r '.id')
ITEM_ID=$(echo "$RESPONSE" | jq -r '.items[0].id')

echo -e "${GREEN}ID del carrito:${NC} $CART_ID"
echo -e "${GREEN}ID del producto en el carrito:${NC} $ITEM_ID"

# 2. Obtener contenido del carrito
print_title "2. Obtener contenido del carrito"
curl -s -X GET "$BASE_URL/carrito" \
  -H "User-ID: test-user" \
  -H "Cookie: SESSION_ID=$SESSION_ID" | jq .

# 3. Añadir otro producto al carrito
print_title "3. Añadir otro producto al carrito"
curl -s -X POST "$BASE_URL/carrito/items" \
  -H "Content-Type: application/json" \
  -H "User-ID: test-user" \
  -H "Cookie: SESSION_ID=$SESSION_ID" \
  -d '{
    "productId": "PROD-002",
    "productName": "Camiseta deportiva",
    "quantity": 1,
    "unitPrice": 29.99,
    "attributes": {
      "color": "azul",
      "talla": "M"
    }
  }' | jq .

# 4. Obtener carrito actualizado
print_title "4. Obtener carrito actualizado"
curl -s -X GET "$BASE_URL/carrito" \
  -H "User-ID: test-user" \
  -H "Cookie: SESSION_ID=$SESSION_ID" | jq .

# 5. Eliminar un producto del carrito
print_title "5. Eliminar un producto del carrito"
curl -s -X DELETE "$BASE_URL/carrito/items/$ITEM_ID" \
  -H "User-ID: test-user" \
  -H "Cookie: SESSION_ID=$SESSION_ID" | jq .

# 6. Obtener carrito después de eliminar
print_title "6. Obtener carrito después de eliminar"
curl -s -X GET "$BASE_URL/carrito" \
  -H "User-ID: test-user" \
  -H "Cookie: SESSION_ID=$SESSION_ID" | jq .

# 7. Guardar dirección de envío
print_title "7. Guardar dirección de envío"
curl -s -X POST "$BASE_URL/checkout/direccion?cartId=$CART_ID&sameAsBillingAddress=true" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "addressLine1": "Calle Principal 123",
    "city": "Madrid",
    "postalCode": "28001",
    "country": "España",
    "phoneNumber": "+34600000000"
  }' | jq .

# 8. Confirmar pedido
print_title "8. Confirmar pedido"
RESPONSE=$(curl -s -X POST "$BASE_URL/checkout/confirmar" \
  -H "Content-Type: application/json" \
  -d "{
    \"cartId\": \"$CART_ID\",
    \"email\": \"juan.perez@example.com\",
    \"shippingAddress\": {
      \"firstName\": \"Juan\",
      \"lastName\": \"Pérez\",
      \"addressLine1\": \"Calle Principal 123\",
      \"city\": \"Madrid\",
      \"postalCode\": \"28001\",
      \"country\": \"España\",
      \"phoneNumber\": \"+34600000000\"
    },
    \"sameAsBillingAddress\": true,
    \"shippingMethod\": \"STANDARD\",
    \"paymentMethod\": \"CREDIT_CARD\"
  }")

echo "$RESPONSE" | jq .
ORDER_ID=$(echo "$RESPONSE" | jq -r '.orderId')
echo -e "${GREEN}ID del pedido:${NC} $ORDER_ID"

# 9. Obtener información del pedido
print_title "9. Obtener información del pedido"
curl -s -X GET "$BASE_URL/pedidos/$ORDER_ID" | jq .

print_title "Pruebas completadas"
