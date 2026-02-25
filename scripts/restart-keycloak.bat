@echo off
echo ========================================
echo Restarting Keycloak Clean
echo ========================================

echo.
echo Step 1: Stopping existing Keycloak containers...
docker ps -a --filter "ancestor=quay.io/keycloak/keycloak:23.0.7" --format "{{.ID}}" > temp_containers.txt
for /f %%i in (temp_containers.txt) do (
    echo Stopping container %%i...
    docker stop %%i
    echo Removing container %%i...
    docker rm %%i
)
del temp_containers.txt

echo.
echo Step 2: Starting fresh Keycloak instance...
echo Keycloak will be available at: http://localhost:9090
echo Admin credentials: admin / admin
echo.
docker run -p 9090:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:23.0.7 start-dev

pause
