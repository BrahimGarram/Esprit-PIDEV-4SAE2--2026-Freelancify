@echo off
echo ========================================
echo Starting All Services
echo ========================================
echo.

echo Step 1: Checking MySQL/WAMP...
net start wampmysqld64 2>nul
if %ERRORLEVEL% EQU 0 (
    echo ✓ MySQL started
) else (
    echo ✓ MySQL already running
)

timeout /t 3 /nobreak >nul

echo.
echo Step 2: Testing MySQL connection...
netstat -ano | findstr :3306 >nul
if %ERRORLEVEL% EQU 0 (
    echo ✓ MySQL is listening on port 3306
) else (
    echo ✗ MySQL is NOT running on port 3306
    echo Please start WAMP manually
    pause
    exit /b 1
)

echo.
echo Step 3: Checking Keycloak...
netstat -ano | findstr :9090 >nul
if %ERRORLEVEL% EQU 0 (
    echo ✓ Keycloak is running on port 9090
) else (
    echo ⚠ Keycloak is NOT running
    echo Starting Keycloak...
    start "Keycloak" cmd /k "cd /d C:\keycloak-26.0.7\bin && kc.bat start-dev --http-port=9090"
    echo Waiting for Keycloak to start...
    timeout /t 15 /nobreak >nul
)

echo.
echo Step 4: Starting Backend (User Service)...
start "Backend - User Service" cmd /k "cd /d %~dp0backend && mvn spring-boot:run"
echo Waiting for backend to start...
timeout /t 20 /nobreak >nul

echo.
echo Step 5: Starting Collaboration Service...
start "Collaboration Service" cmd /k "cd /d %~dp0collaboration-service && mvn spring-boot:run"
echo Waiting for collaboration service to start...
timeout /t 20 /nobreak >nul

echo.
echo Step 6: Starting Frontend...
start "Frontend - Angular" cmd /k "cd /d %~dp0frontend && npm start"

echo.
echo ========================================
echo All Services Starting!
echo ========================================
echo.
echo Services:
echo - MySQL: localhost:3306
echo - Keycloak: http://localhost:9090
echo - Backend: http://localhost:8081
echo - Collaboration: http://localhost:8082
echo - Frontend: http://localhost:4200
echo.
echo Check the individual console windows for status
echo.
pause
