#!/bin/bash

# Script para ejecutar tests del frontend con Jest
# Autor: Equipo Infinia Sports
# Fecha: 11/06/2025

# Colores para mensajes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para mostrar el menú
show_menu() {
  echo -e "${BLUE}=== Tests Frontend Infinia Sports ===${NC}"
  echo -e "${YELLOW}1.${NC} Ejecutar todos los tests"
  echo -e "${YELLOW}2.${NC} Ejecutar tests con cobertura"
  echo -e "${YELLOW}3.${NC} Ejecutar tests en modo watch"
  echo -e "${YELLOW}4.${NC} Ejecutar tests de componentes específicos"
  echo -e "${YELLOW}0.${NC} Salir"
  echo -n -e "${YELLOW}Selecciona una opción:${NC} "
}

# Función para ejecutar todos los tests
run_all_tests() {
  echo -e "${BLUE}Ejecutando todos los tests...${NC}"
  npm test
}

# Función para ejecutar tests con cobertura
run_coverage_tests() {
  echo -e "${BLUE}Ejecutando tests con cobertura...${NC}"
  npm run test:coverage
  echo -e "${GREEN}Reporte de cobertura generado en /coverage${NC}"
}

# Función para ejecutar tests en modo watch
run_watch_tests() {
  echo -e "${BLUE}Ejecutando tests en modo watch...${NC}"
  npm run test:watch
}

# Función para ejecutar tests de componentes específicos
run_specific_tests() {
  echo -e "${BLUE}Ejecutando tests de componentes específicos${NC}"
  echo -n -e "${YELLOW}Introduce el nombre del componente (ej: ProductCard):${NC} "
  read component_name
  
  if [ -z "$component_name" ]; then
    echo -e "${RED}Nombre de componente no válido${NC}"
    return
  fi
  
  # Buscar archivos de test que coincidan con el patrón
  test_files=$(find src -name "*${component_name}*.test.*")
  
  if [ -z "$test_files" ]; then
    echo -e "${RED}No se encontraron tests para el componente ${component_name}${NC}"
    return
  fi
  
  echo -e "${BLUE}Ejecutando tests para:${NC}"
  echo "$test_files"
  npx jest "$test_files"
}

# Menú principal
while true; do
  show_menu
  read option
  
  case $option in
    1) run_all_tests ;;
    2) run_coverage_tests ;;
    3) run_watch_tests ;;
    4) run_specific_tests ;;
    0) echo -e "${GREEN}¡Hasta pronto!${NC}"; exit 0 ;;
    *) echo -e "${RED}Opción no válida${NC}" ;;
  esac
  
  echo
  echo -e "${YELLOW}Presiona Enter para continuar...${NC}"
  read
  clear
done
