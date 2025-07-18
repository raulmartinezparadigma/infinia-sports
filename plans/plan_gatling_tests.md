# Plan de Implementación de Pruebas de Carga con Gatling

## 1. Objetivo

El objetivo principal de estas pruebas de carga es evaluar el rendimiento y la estabilidad de la aplicación web "Infinia Sports" bajo condiciones de carga realistas. Se busca:
- Medir los tiempos de respuesta de los endpoints clave.
- Identificar cuellos de botella en el backend.
- Determinar el número máximo de usuarios concurrentes que la aplicación puede soportar.
- Asegurar que la experiencia de usuario no se degrade bajo estrés.

## 2. Alcance

Las pruebas se centrarán en los siguientes flujos de usuario críticos:

- **Flujo 1: Usuario Navegante Anónimo:**
    - Accede a la página principal.
    - Navega al catálogo de productos.
    - Realiza una búsqueda de productos.
    - Visualiza los detalles de un producto.

- **Flujo 2: Usuario Comprador Autenticado:**
    - Se autentica en la aplicación (login).
    - Navega por el catálogo.
    - Añade uno o varios productos al carrito.
    - Visualiza el carrito.
    - Procede al checkout (simulando hasta el paso final).

## 3. Metodología

### 3.1. Perfil de Carga

Se simulará un perfil de carga progresivo para observar el comportamiento del sistema a medida que aumenta el estrés.

- **Rampa de Carga (Ramp-up):** Se incrementará el número de usuarios de 1 a 100 usuarios concurrentes en un período de 2 minutos.
- **Carga Sostenida (Sustain):** Se mantendrá la carga de 100 usuarios concurrentes durante 5 minutos.
- **Distribución de Escenarios:** 70% de los usuarios seguirán el "Flujo 1" (Navegante) y 30% seguirán el "Flujo 2" (Comprador).

### 3.2. Criterios de Aceptación (Assertions)

Una simulación se considerará exitosa si cumple con los siguientes criterios:

- **Tasa de Errores:** Menor al 1% en todas las peticiones.
- **Tiempo de Respuesta (Percentil 95):**
    - Peticiones de lectura (GET, ej. ver catálogo): Menor a 500 ms.
    - Peticiones de escritura (POST/PUT, ej. añadir al carrito): Menor a 800 ms.

## 4. Plan de Implementación por Pasos

1.  **Paso 1: Configuración y Simulación Básica (Verificación)**
    - Revisar la configuración actual del proyecto Gatling.
    - Crear una simulación muy simple que haga una única petición GET al endpoint `/api/products`.
    - Ejecutarla con 1 solo usuario para verificar que la comunicación con el backend funciona y Gatling genera el informe.

2.  **Paso 2: Desarrollar el Escenario del "Usuario Navegante"**
    - Crear un nuevo fichero de simulación para este escenario.
    - Implementar las peticiones HTTP para:
        - `GET /` (Página principal)
        - `GET /api/products` (Catálogo)
        - `GET /api/products?query={termino}` (Búsqueda)
        - `GET /api/products/{productId}` (Detalle de producto, usando un "feeder" para obtener IDs de producto dinámicos).

3.  **Paso 3: Desarrollar el Escenario del "Usuario Comprador"**
    - Crear un fichero de simulación para este escenario.
    - Implementar el flujo de autenticación:
        - `POST /api/auth/login` con credenciales de prueba.
        - Extraer el token de sesión o la cookie si es necesario para las siguientes peticiones.
    - Implementar las acciones de compra, usando el contexto de sesión:
        - `POST /api/cart/items` (Añadir al carrito).
        - `GET /api/cart` (Ver carrito).

4.  **Paso 4: Crear la Simulación de Carga Completa**
    - Crear una simulación final que integre los dos escenarios anteriores.
    - Configurar el perfil de carga definido (rampa de 100 usuarios en 2 min, sostenido por 5 min).
    - Aplicar la distribución de 70/30 para los escenarios.
    - Añadir las aserciones (tasa de error y tiempos de respuesta) en la configuración de la simulación.

5.  **Paso 5: Ejecución y Análisis**
    - Ejecutar la simulación completa contra el entorno de pruebas.
    - Analizar el informe HTML generado por Gatling, prestando especial atención a los gráficos de tiempo de respuesta, usuarios activos y peticiones fallidas.
    - Iterar sobre el plan si se encuentran cuellos de botella o errores.
