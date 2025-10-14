@echo off
REM Script para ejecutar pruebas de Resilience4j con Gatling
REM Autor: Cascade AI
REM Fecha: 2025-10-14

echo ============================================
echo  Pruebas de Resilience4j con Gatling
echo ============================================
echo.

REM Verificar que estamos en el directorio correcto
if not exist "pom.xml" (
    echo ERROR: Ejecuta este script desde el directorio gatling-tests
    pause
    exit /b 1
)

echo [1/4] Verificando que el backend este ejecutandose...
curl -s http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    echo.
    echo ERROR: El backend no esta ejecutandose en http://localhost:8080
    echo.
    echo Por favor, inicia el backend antes de ejecutar las pruebas:
    echo   cd backend
    echo   mvn spring-boot:run
    echo.
    pause
    exit /b 1
)
echo    - Backend OK (puerto 8080)

echo.
echo [2/4] Verificando MongoDB...
REM Nota: Esta verificación es opcional, el test funcionará con o sin MongoDB
echo    - MongoDB: Se verificara durante el test

echo.
echo [3/4] Abriendo dashboard de monitoreo en el navegador...
echo    - Actuator Circuit Breakers: http://localhost:8080/actuator/circuitbreakers
echo    - Actuator Health: http://localhost:8080/actuator/health
start http://localhost:8080/actuator/circuitbreakers

echo.
echo [4/4] Ejecutando simulacion de Resilience4j...
echo.
echo Parametros de la prueba:
echo   - Duracion maxima: 60 segundos
echo   - Usuarios concurrentes: hasta 60
echo   - Endpoints objetivo: GET /api/cart (protegido con @CircuitBreaker)
echo.
echo Observa:
echo   1. Los logs del backend en tiempo real
echo   2. El dashboard de Circuit Breakers (se abrio en el navegador)
echo   3. El reporte HTML de Gatling al finalizar
echo.

call mvn gatling:test -Dgatling.simulationClass=com.infinia.sports.performance.Resilience4jStressSimulation

if errorlevel 1 (
    echo.
    echo ERROR: La simulacion fallo. Revisa los logs arriba.
    pause
    exit /b 1
)

echo.
echo ============================================
echo  Prueba completada con exito
echo ============================================
echo.
echo Revisa el reporte HTML en:
for /f "delims=" %%i in ('dir /b /od target\gatling\resilience4jstresssimulation-* 2^>nul') do set LATEST_REPORT=%%i
if defined LATEST_REPORT (
    echo   target\gatling\%LATEST_REPORT%\index.html
    echo.
    echo Abriendo reporte...
    start target\gatling\%LATEST_REPORT%\index.html
) else (
    echo   target\gatling\[ultimo-directorio]\index.html
)

echo.
echo Para ver metricas de Circuit Breaker:
echo   curl http://localhost:8080/actuator/circuitbreakers
echo.
echo Para ver eventos de Circuit Breaker:
echo   curl http://localhost:8080/actuator/circuitbreakerevents
echo.
pause
