# Configuration Keycloak pour Freelancify

## 1. Démarrer Keycloak (Docker)

Keycloak doit écouter sur le port **8080**. Aucune autre application ne doit utiliser ce port (sinon le backend recevra des erreurs 404). Si **Oracle XE (TNSLSNR)** utilise 8080, vous pouvez libérer le port en exécutant le script **en tant qu’administrateur** :  
`scripts\liberer-port-8080-admin.bat` (double-clic) ou `scripts\liberer-port-8080.ps1` dans une PowerShell ouverte en admin.

```bash
docker run -p 8080:8080 ^
  -e KEYCLOAK_ADMIN=admin ^
  -e KEYCLOAK_ADMIN_PASSWORD=admin ^
  -e KC_HOSTNAME=127.0.0.1 ^
  -e KC_HOSTNAME_STRICT=false ^
  -e KC_HTTP_ENABLED=true ^
  quay.io/keycloak/keycloak:23.0.7 start-dev
```
(Ne pas utiliser KC_HOSTNAME_ADMIN avec une URL complete pour eviter un double port dans les redirections.)

Sous Linux/Mac, remplacer `^` par `\` en fin de ligne.

- **Console d’administration** : **http://127.0.0.1:8080/admin** (utiliser 127.0.0.1 pour éviter les soucis IPv6)  
- Identifiants : `admin` / `admin`  
- Attendre le message dans les logs : `Listening on: http://0.0.0.0:8080` (démarrage ~10–15 s).

**Si l’admin affiche « Unexpected Application Error / Network response was not OK »** :  
1. Relancer Keycloak avec la commande ci‑dessus (`KC_HOSTNAME`, `KC_HOSTNAME_STRICT`, `KC_HTTP_ENABLED`).  
2. Ouvrir l’admin en **http://127.0.0.1:8080/admin** (obligatoire avec la config ci‑dessus).  
3. Vider le cache du navigateur ou utiliser la navigation privée.

**Si le backend reçoit une 404 HTML** sur l’endpoint token : un autre programme (souvent **Oracle TNS Listener**) utilise encore le port 8080. Exécuter **en administrateur** `scripts\liberer-port-8080-admin.bat`, puis redémarrer le backend.

---

## 2. Créer le realm `projetpidev`

Sans ce realm, le backend renverra **« Keycloak is unavailable »** ou des erreurs 404 lors de l’inscription.

1. Ouvrir http://127.0.0.1:8080/admin et se connecter (`admin` / `admin`).
2. Menu de gauche : cliquer sur le dropdown **Master** (en haut).
3. **Create realm**.
4. **Realm name** : `projetpidev`.
5. **Create**.

---

## 3. Créer le client `freelance-client`

1. Dans le realm **projetpidev**, menu **Clients** → **Create client**.
2. **Client type** : OpenID Connect.  
3. **Client ID** : `freelance-client` → **Next**.
4. **Client authentication** : ON.  
5. **Authorization** : OFF.  
6. **Authentication flow** : cocher **Standard flow** et **Direct access grants** → **Next**.
7. **Valid redirect URIs** : `http://localhost:4200/*` (ajuster si votre front tourne ailleurs).  
8. **Web origins** : `http://localhost:4200` → **Save**.
9. Onglet **Credentials** : copier le **Client secret** et le mettre dans la config du frontend (endpoint token, etc.) si besoin.

---

## 4. Créer les rôles

1. Dans **projetpidev**, menu **Realm roles** → **Create role**.
2. Créer trois rôles (un par un) : **USER**, **FREELANCER**, **ADMIN**.

Ces noms doivent correspondre à ceux utilisés par l’application.

---

## 5. Vérifications si l’app affiche « Keycloak is unavailable »

| Cause possible | Action |
|----------------|--------|
| Keycloak pas encore démarré | Attendre le message `Listening on: http://0.0.0.0:8080` dans les logs Docker. |
| Realm `projetpidev` absent | Créer le realm comme en §2. |
| Autre app sur le port 8080 | Arrêter l’autre service ou changer le port de Keycloak (et la config du backend/frontend). |
| Backend dans Docker | Si le backend tourne dans un conteneur, utiliser `http://host.docker.internal:8080` dans `application.yml` (keycloak.admin.url). |

### 5.1 Erreur 404 avec une page HTML (« Not found »)

Si les logs backend indiquent **404** et que le corps de la réponse est une **page HTML** (avec `<!DOCTYPE` ou `IETF`), ce n’est **pas** Keycloak qui répond sur le port 8080, mais un autre serveur (Apache, XAMPP, nginx, etc.).

**À faire :**

1. **Vérifier qui utilise le port 8080** (PowerShell) :
   ```powershell
   netstat -ano | findstr :8080
   ```
   Si **plusieurs PIDs** apparaissent (ex. 7464, 5576, 21096), plusieurs applications se partagent le port. Identifier chaque processus :
   ```powershell
   tasklist /FI "PID eq 7464"
   tasklist /FI "PID eq 5576"
   tasklist /FI "PID eq 21096"
   ```
   En général : **java.exe** = Keycloak (Docker). **httpd.exe**, **nginx.exe**, **Apache** = autre serveur à arrêter ou à configurer sur un autre port. Garder **un seul** processus sur 8080 (Keycloak).

2. **Tester l’endpoint token depuis la machine** (PowerShell) :
   ```powershell
   curl -Method POST -Uri "http://localhost:8080/realms/master/protocol/openid-connect/token" -Body "grant_type=password&client_id=admin-cli&username=admin&password=admin" -ContentType "application/x-www-form-urlencoded"
   ```
   - Si la réponse est du **JSON** avec `access_token` → Keycloak est bien sur 8080 ; redémarrer le backend et réessayer.
   - Si la réponse est du **HTML** ou une erreur → une autre application écoute sur 8080 : l’arrêter puis relancer **uniquement** Keycloak en Docker sur 8080.

3. **Ne lancer Keycloak qu’après** avoir libéré le port 8080 (arrêter XAMPP/Apache ou tout service configuré sur 8080).

---

## 6. Messages courants dans les logs Keycloak

- **`REFRESH_TOKEN_ERROR", error="invalid_token"`**  
  Token de rafraîchissement invalide ou expiré. L’utilisateur doit se reconnecter.

- **`LOGIN_ERROR", error="user_not_found", username="adamm"`**  
  L’utilisateur `adamm` n’existe pas dans le realm. Normal avant inscription ou si le compte n’a pas été créé dans Keycloak.

Une fois le realm `projetpidev`, le client `freelance-client` et les rôles créés, relancer une inscription ou une connexion depuis l’application.
