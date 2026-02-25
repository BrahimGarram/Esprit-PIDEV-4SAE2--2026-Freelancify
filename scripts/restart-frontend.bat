@echo off
echo ========================================
echo Restarting Frontend with Fixed Port
echo ========================================
echo.
echo The frontend has been updated to connect to port 8082
echo (was incorrectly connecting to 8083)
echo.
echo Press Ctrl+C in the frontend terminal to stop it
echo Then run this script to restart with the fix
echo.
pause

cd frontend
echo Starting Angular development server...
echo.
npm start
