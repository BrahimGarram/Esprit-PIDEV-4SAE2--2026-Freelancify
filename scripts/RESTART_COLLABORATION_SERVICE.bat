@echo off
echo =====================================================
echo Restarting Collaboration Service
echo =====================================================

cd collaboration-service

echo.
echo Stopping any running instances...
taskkill /F /FI "WINDOWTITLE eq collaboration-service*" 2>nul

echo.
echo Starting collaboration service on port 8082...
start "collaboration-service" cmd /k "mvnw.cmd spring-boot:run"

echo.
echo Service is starting...
echo Check the new window for startup logs
echo Wait for "Started CollaborationServiceApplication" message
echo.
pause
