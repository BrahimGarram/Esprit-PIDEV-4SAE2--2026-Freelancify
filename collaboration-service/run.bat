@echo off
echo ========================================
echo Starting Collaboration Service
echo ========================================
echo.
echo Port: 8082
echo Database: freelance_db
echo.

cd /d "%~dp0"

echo Cleaning and building...
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed! Please check the errors above.
    pause
    exit /b 1
)

echo.
echo Starting the service...
echo.
call mvn spring-boot:run

pause
