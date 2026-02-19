# Keycloak Admin Console – Avoiding "Network response was not OK"

Keycloak 23.0.7 has a bug: opening the Admin Console with a URL that goes **directly to a realm** (e.g. `.../admin/master/console/#/projetpidev/roles`) can cause:

- **Server:** `NullPointerException` in `AdminConsole.whoAmI` (realm is null)
- **Browser:** "Unexpected Application Error! Network response was not OK."

## Workaround

1. **Always open the Admin Console from the root or master realm:**
   - **Use:** `http://localhost:8080/admin/`
   - **Or:** `http://localhost:8080/admin/master/console/`
   - Do **not** bookmark or open a URL that already contains `#/projetpidev/...` in the hash.

2. **Log in** with username `admin` and password `admin`.

3. **Switch realm from the UI:** use the **realm dropdown** (top-left, currently showing "Keycloak" or "master") and select **projetpidev**. Then open Roles, Clients, Users, etc.

4. **Avoid** typing or bookmarking:
   - `http://localhost:8080/admin/master/console/#/projetpidev/roles`
   - Open the console first, then navigate to the realm and section from the menu.

This way the console always starts with a valid realm context and the `whoAmI` call succeeds.
