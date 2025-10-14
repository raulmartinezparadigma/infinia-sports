@echo off
REM Script para monitorear métricas de Resilience4j en tiempo real
REM Autor: Cascade AI
REM Fecha: 2025-10-14

setlocal enabledelayedexpansion

echo ============================================
echo  Monitor de Resilience4j - Actuator
echo ============================================
echo.

REM Verificar que curl está disponible
where curl >nul 2>&1
if errorlevel 1 (
    echo ERROR: curl no esta instalado o no esta en el PATH
    echo.
    echo Instala curl o usa PowerShell:
    echo   Invoke-RestMethod http://localhost:8080/actuator/circuitbreakers
    pause
    exit /b 1
)

REM Verificar que el backend está ejecutándose
curl -s http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ERROR: El backend no esta ejecutandose en http://localhost:8080
    echo.
    echo Inicia el backend primero:
    echo   cd backend
    echo   mvn spring-boot:run
    pause
    exit /b 1
)

:MENU
cls
echo ============================================
echo  Monitor de Resilience4j - Actuator
echo ============================================
echo.
echo Selecciona una opcion:
echo.
echo  1. Estado de Circuit Breakers
echo  2. Eventos de Circuit Breaker
echo  3. Estado de Retries
echo  4. Eventos de Retry (mongoService)
echo  5. Health Check General
echo  6. Metricas de mongoService (completo)
echo  7. Monitoreo Continuo (cada 5 segundos)
echo  8. Salir
echo.
set /p OPCION="Opcion: "

if "%OPCION%"=="1" goto CB_STATE
if "%OPCION%"=="2" goto CB_EVENTS
if "%OPCION%"=="3" goto RETRY_STATE
if "%OPCION%"=="4" goto RETRY_EVENTS
if "%OPCION%"=="5" goto HEALTH
if "%OPCION%"=="6" goto MONGO_METRICS
if "%OPCION%"=="7" goto CONTINUOUS
if "%OPCION%"=="8" goto END
goto MENU

:CB_STATE
cls
echo ============================================
echo  Estado de Circuit Breakers
echo ============================================
echo.
curl -s http://localhost:8080/actuator/circuitbreakers
echo.
echo.
pause
goto MENU

:CB_EVENTS
cls
echo ============================================
echo  Eventos de Circuit Breaker (ultimos 20)
echo ============================================
echo.
curl -s http://localhost:8080/actuator/circuitbreakerevents
echo.
echo.
pause
goto MENU

:RETRY_STATE
cls
echo ============================================
echo  Estado de Retries
echo ============================================
echo.
curl -s http://localhost:8080/actuator/retries
echo.
echo.
pause
goto MENU

:RETRY_EVENTS
cls
echo ============================================
echo  Eventos de Retry - mongoService
echo ============================================
echo.
curl -s http://localhost:8080/actuator/retryevents/mongoService
echo.
echo.
pause
goto MENU

:HEALTH
cls
echo ============================================
echo  Health Check General
echo ============================================
echo.
curl -s http://localhost:8080/actuator/health
echo.
echo.
pause
goto MENU

:MONGO_METRICS
cls
echo ============================================
echo  Metricas Completas de mongoService
echo ============================================
echo.
echo --- Circuit Breaker State ---
curl -s http://localhost:8080/actuator/circuitbreakers/mongoService
echo.
echo.
echo --- Recent Circuit Breaker Events ---
curl -s "http://localhost:8080/actuator/circuitbreakerevents/mongoService?size=5"
echo.
echo.
echo --- Retry State ---
curl -s http://localhost:8080/actuator/retries/mongoService
echo.
echo.
echo --- Recent Retry Events ---
curl -s "http://localhost:8080/actuator/retryevents/mongoService?size=5"
echo.
echo.
pause
goto MENU

:CONTINUOUS
cls
echo ============================================
echo  Monitoreo Continuo (Ctrl+C para detener)
echo ============================================
echo.
echo Actualizando cada 5 segundos...
echo.

:LOOP
cls
echo ============================================
echo  Monitoreo Continuo - %date% %time%
echo ============================================
echo.

echo --- Circuit Breaker: mongoService ---
curl -s http://localhost:8080/actuator/circuitbreakers/mongoService 2>nul | findstr /C:"state" /C:"failureRate" /C:"numberOfSuccessfulCalls" /C:"numberOfFailedCalls"
if errorlevel 1 (
    echo [ERROR] No se pudo obtener datos del Circuit Breaker
)
echo.

echo --- Retry: mongoService ---
curl -s http://localhost:8080/actuator/retries/mongoService 2>nul | findstr /C:"successfulCalls" /C:"failedCalls" /C:"retryEvents"
if errorlevel 1 (
    echo [ERROR] No se pudo obtener datos de Retry
)
echo.

echo --- Health Status ---
curl -s http://localhost:8080/actuator/health 2>nul | findstr /C:"status" /C:"UP" /C:"DOWN"
if errorlevel 1 (
    echo [ERROR] No se pudo obtener Health Status
)
echo.

echo Proxima actualizacion en 5 segundos...
echo Presiona Ctrl+C para detener
timeout /t 5 /nobreak >nul
goto LOOP

:END
echo.
echo Saliendo del monitor...
exit /b 0
