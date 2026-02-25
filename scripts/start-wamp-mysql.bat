@echo off
echo ========================================
echo Starting WAMP/MySQL Server
echo ========================================
echo.

echo Checking if WAMP is installed...
if exist "C:\wamp64\wampmanager.exe" (
    echo Found WAMP at C:\wamp64
    echo.
    echo Starting WAMP services...
    net start wampmysqld64
    net start wampapache64
    
    echo.
    echo WAMP services started!
    echo MySQL should be running on port 3306
    echo.
    echo You can also start WAMP manually:
    echo 1. Click the WAMP icon in system tray
    echo 2. Select "Start All Services"
    echo.
) else (
    echo WAMP not found at C:\wamp64
    echo.
    echo Please start WAMP manually:
    echo 1. Find WAMP icon in system tray (bottom right)
    echo 2. Click it and select "Start All Services"
    echo 3. Wait for icon to turn green
    echo.
)

echo Waiting 5 seconds for MySQL to start...
timeout /t 5 /nobreak >nul

echo.
echo Testing MySQL connection...
netstat -ano | findstr :3306

echo.
echo If you see port 3306 above, MySQL is running!
echo Now you can start the backend service.
echo.
pause
