@echo off
echo ========================================
echo Creating Keycloak Users via REST API
echo ========================================
echo.
echo This script will create 10 sample users in Keycloak
echo All users will have password: password123
echo.
echo Make sure:
echo 1. Keycloak is running on port 9090
echo 2. Backend service is running on port 8081
echo.
pause

echo.
echo Calling API endpoint: POST http://localhost:8081/api/admin/seed-users
echo.

curl -X POST http://localhost:8081/api/admin/seed-users -H "Content-Type: application/json"

echo.
echo.
echo ========================================
echo Done!
echo ========================================
echo.
echo Check the output above to see if users were created successfully.
echo You can now login to Keycloak Admin Console to verify the users.
echo.
pause
