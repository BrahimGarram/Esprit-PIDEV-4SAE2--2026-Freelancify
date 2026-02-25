@echo off
echo Demarrage de Keycloak...
echo.
echo Une fois demarre, ouvrir dans le navigateur (copier-coller) :
echo   http://127.0.0.1:8080/admin
echo Identifiants : admin / admin
echo.
docker run -p 8080:8080 ^
  -e KEYCLOAK_ADMIN=admin ^
  -e KEYCLOAK_ADMIN_PASSWORD=admin ^
  -e KC_HOSTNAME=127.0.0.1 ^
  -e KC_HOSTNAME_STRICT=false ^
  -e KC_HTTP_ENABLED=true ^
  quay.io/keycloak/keycloak:23.0.7 start-dev
