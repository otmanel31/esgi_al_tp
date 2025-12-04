@echo off
SETLOCAL EnableDelayedExpansion

echo ========================================
echo  MICROSERVICES QUARKUS - BUILD ^& RUN
echo ========================================
echo.

COLOR 0A

REM Le script est dans corrige/, donc ROOT_DIR = corrige/
SET ROOT_DIR=%~dp0
SET CORRIGE_DIR=%ROOT_DIR%

echo [INFO] Repertoire corrige: %CORRIGE_DIR%
echo.

REM ====================================
REM 1. VERIFICATION MAVEN
REM ====================================
echo [STEP 1/4] Verification Maven...
call mvn -v >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven non installe
    echo [INFO] Telecharger: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
echo [OK] Maven version:
call mvn -v | findstr "Apache Maven"
echo.

REM ====================================
REM 2. COMPILATION TOUS SERVICES
REM ====================================
echo [STEP 2/4] Compilation services Quarkus...
echo.

SET SERVICES=sensor-management-service timeseries-service data-ingestion-service command-service

FOR %%S IN (%SERVICES%) DO (
    echo [BUILD] %%S...
    cd "%CORRIGE_DIR%%%S"

    REM Compilation sans tests (plus rapide)
    call mvn clean package -DskipTests -q

    IF !ERRORLEVEL! NEQ 0 (
        echo [ERROR] Echec compilation %%S
        cd "%CORRIGE_DIR%"
        pause
        exit /b 1
    )
    echo [OK] %%S compile
    echo.
    cd "%CORRIGE_DIR%"
)

REM ====================================
REM 3. DEMARRAGE SERVICES QUARKUS
REM ====================================
echo [STEP 3/4] Demarrage services...
echo.
echo [INFO] Commande Quarkus: mvn quarkus:dev
echo [INFO] Attente 15s entre chaque service
echo.

REM Sensor Management (8081)
echo [START] Sensor Management (http://localhost:8081)
START "Sensor Management" cmd /k "cd /d %CORRIGE_DIR%sensor-management-service && echo [QUARKUS] Demarrage Sensor Management... && mvn quarkus:dev"
timeout /t 15 /nobreak >nul

REM TimeSeries (8082)
echo [START] TimeSeries (http://localhost:8082)
START "TimeSeries Service" cmd /k "cd /d %CORRIGE_DIR%timeseries-service && echo [QUARKUS] Demarrage TimeSeries... && mvn quarkus:dev"
timeout /t 15 /nobreak >nul

REM Data Ingestion (8083)
echo [START] Data Ingestion (http://localhost:8083)
START "Data Ingestion" cmd /k "cd /d %CORRIGE_DIR%data-ingestion-service && echo [QUARKUS] Demarrage Data Ingestion... && mvn quarkus:dev"
timeout /t 15 /nobreak >nul

REM Command (8084)
echo [START] Command (http://localhost:8084)
START "Command Service" cmd /k "cd /d %CORRIGE_DIR%command-service && echo [QUARKUS] Demarrage Command... && mvn quarkus:dev"
timeout /t 15 /nobreak >nul

echo.
echo [OK] Tous les services en cours de demarrage
echo.

REM ====================================
REM 4. ATTENTE + VERIFICATION
REM ====================================
echo [STEP 4/4] Attente demarrage (45 secondes)...
timeout /t 45 /nobreak

echo.
echo Verification health endpoints Quarkus (/q/health)...
echo.

REM Health checks Quarkus
curl -s http://localhost:8081/q/health 2>nul | findstr "UP" >nul
IF %ERRORLEVEL% EQU 0 (
    echo [OK] Sensor Management: UP
) ELSE (
    echo [WARN] Sensor Management: DOWN ou demarrage en cours
)

curl -s http://localhost:8082/q/health 2>nul | findstr "UP" >nul
IF %ERRORLEVEL% EQU 0 (
    echo [OK] TimeSeries: UP
) ELSE (
    echo [WARN] TimeSeries: DOWN ou demarrage en cours
)

curl -s http://localhost:8083/q/health 2>nul | findstr "UP" >nul
IF %ERRORLEVEL% EQU 0 (
    echo [OK] Data Ingestion: UP
) ELSE (
    echo [WARN] Data Ingestion: DOWN ou demarrage en cours
)

curl -s http://localhost:8084/q/health 2>nul | findstr "UP" >nul
IF %ERRORLEVEL% EQU 0 (
    echo [OK] Command: UP
) ELSE (
    echo [WARN] Command: DOWN ou demarrage en cours
)

echo.
echo ========================================
echo  SERVICES QUARKUS DEMARRES
echo ========================================
echo.
echo URLs Services:
echo   - Sensor Management: http://localhost:8081
echo   - TimeSeries:        http://localhost:8082
echo   - Data Ingestion:    http://localhost:8083
echo   - Command:           http://localhost:8084
echo.
echo URLs Dev UI Quarkus:
echo   - http://localhost:8081/q/dev
echo   - http://localhost:8082/q/dev
echo   - http://localhost:8083/q/dev
echo   - http://localhost:8084/q/dev
echo.
echo [INFO] Quarkus Live Reload: modifier code = recompilation auto
echo [INFO] Arreter: Ctrl+C dans chaque fenetre OU stop-all.bat
echo.
pause