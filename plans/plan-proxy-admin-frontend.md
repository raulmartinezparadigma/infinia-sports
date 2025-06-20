# Plan: Proxy para admin-frontend

## Objetivo
Asegurar que el frontend de administración (`admin-frontend`) redirige automáticamente las peticiones API al backend en el puerto correcto (8080), evitando errores de conexión y 404 por puerto incorrecto.

## Pasos realizados
1. Se detectó que el backend corre en el puerto 8080 y el frontend admin intentaba hacer peticiones al puerto 3001.
2. Se revisó `admin-frontend/package.json` y se añadió la línea:
   ```json
   "proxy": "http://localhost:8080"
   ```
   justo antes de la sección `browserslist`.
3. Se recomendó reiniciar el servidor de admin-frontend para aplicar el cambio.

## Resultado esperado
- Las peticiones a `/api/...` desde el admin-frontend se redirigen correctamente al backend en 8080.
- El login de administrador y otras funciones API deben funcionar sin errores de conexión o 404.

## Seguimiento
- Si aparecen nuevos errores, revisar consola y logs para depuración adicional.
- Documentado en memory-bank y plan el 20/06/2025.
