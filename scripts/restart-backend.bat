@echo off
echo ========================================
echo Restarting Backend Service
echo ========================================
echo.
echo This will restart the user-service backend to apply security changes
echo.

echo Step 1: Finding backend process...
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq java.exe" /FO LIST ^| findstr /C:"PID:"') do (
    for /f "tokens=*" %%j in ('wmic process where "ProcessId=%%i" get CommandLine /format:list ^| findstr "UserServiceApplication"') do (
        echo Found backend process: %%i
        echo Stopping process...
        taskkill /PID %%i /F
        timeout /t 2 /nobreak >nul
    )
)

echo.
echo Step 2: Starting backend service...
echo.
cd backend
start "Backend Service" cmd /k "mvnw spring-boot:run"

echo.
echo ========================================
echo Backend is starting...
echo ========================================
echo.
echo Wait for the backend to fully start (you'll see "Started UserServiceApplication")
echo Then you can create users by running: create-keycloak-users.bat
echo.
pause
