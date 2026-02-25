@echo off
echo ========================================
echo Opening Keycloak Admin Console
echo ========================================
echo.
echo Keycloak URLs:
echo - Main: http://localhost:9090
echo - Admin: http://localhost:9090/admin
echo - Master Realm: http://localhost:9090/realms/master
echo.
echo Login credentials:
echo Username: admin
echo Password: admin
echo.
echo Opening in browser...
start http://localhost:9090
timeout /t 2 /nobreak >nul
start http://localhost:9090/admin
echo.
echo If page is blank, try:
echo 1. Clear browser cache (Ctrl+Shift+Delete)
echo 2. Try incognito/private mode
echo 3. Wait a few more seconds for Keycloak to fully load
echo.
pause
