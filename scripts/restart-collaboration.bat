@echo off
echo ========================================
echo Restarting Collaboration Service
echo ========================================
echo.

cd /d "%~dp0\..\collaboration-service"

echo Stopping any running instances...
for /f "tokens=5" %%a in ('netstat -aon ^| find ":8082" ^| find "LISTENING"') do (
    echo Killing process %%a on port 8082
    taskkill /F /PID %%a 2>nul
)

timeout /t 2 /nobreak >nul

echo.
echo Starting Collaboration Service...
echo.
start "Collaboration Service" cmd /k "mvn spring-boot:run"

echo.
echo Collaboration Service is starting on port 8082
echo Check the new window for logs
echo.
pause
