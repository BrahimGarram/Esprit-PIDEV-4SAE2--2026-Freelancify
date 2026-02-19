# Keycloak and signup troubleshooting

## Signup returns 400 with "Keycloak is unavailable"

User registration calls Keycloak on **port 8080** to create the user. If signup fails with a message like *"Keycloak is unavailable. Ensure Keycloak is running on port 8080..."*, do the following.

### 1. Ensure only Keycloak uses port 8080

- Stop any other application (Apache, another server, or another Keycloak) that might be listening on port 8080.
- On Windows, check what is using the port:  
  `netstat -ano | findstr :8080`

### 2. Start Keycloak

Example with Docker:

```bash
docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:23 start-dev
```

Or use your existing Keycloak installation and make sure it is bound to port 8080.

### 3. Check that Keycloak is responding

- Open: `http://localhost:8080/realms/master`
- You should get a JSON realm description, not an HTML error page.

If you get **404** or an HTML page, the process on 8080 is not Keycloak (or Keycloak is not fully started). Fix the process on 8080 or start Keycloak as above.

### 4. Configuration

- Admin URL is set in `application.yml`: `keycloak.admin.url` (default `http://127.0.0.1:8080`).
- Admin user/password must match your Keycloak admin user (e.g. `admin` / `admin` in dev).
- The app realm is configured in `keycloak.admin.realm` (e.g. `projetpidev`). The admin token is requested from the `master` realm; users are created in the configured realm.

---

## Realm and roles (projetpidev)

The backend creates users in the realm **projetpidev** and optionally assigns a **realm role** (USER, FREELANCER, ENTERPRISE, ADMIN). If these roles do not exist, signup still succeeds but the user will not have that role in Keycloak until you add it.

### Create the realm (if needed)

1. Open Keycloak Admin Console: `http://localhost:8080/admin` (login: admin / admin).
2. Click **Create realm**, name it `projetpidev`, then Create.

### Create realm roles (optional but recommended)

1. In Admin Console, select realm **projetpidev**.
2. Go to **Realm roles** → **Create role**.
3. Create these roles (one by one): **USER**, **FREELANCER**, **ENTERPRISE**, **ADMIN** (names must match exactly).
4. Save each role.

After this, new signups will get the chosen role assigned in Keycloak. Without these roles, registration still works; only role assignment is skipped (see backend logs for a warning).
