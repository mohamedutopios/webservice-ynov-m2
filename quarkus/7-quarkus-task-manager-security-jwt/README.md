# 🔒 Sécurité JWT + Keycloak — TaskManager

## 🎯 Objectif

Sécuriser l'API REST TaskManager avec **Keycloak** (serveur d'identité) et **JWT** (JSON Web Token).
Quarkus vérifie le token JWT à chaque requête et contrôle les rôles.

## 📐 Architecture

```
┌──────────┐     1. login          ┌───────────────┐
│  Client  │ ───────────────────→  │   Keycloak    │
│  (curl)  │ ←─────────────────── │  :8180        │
│          │     2. JWT token      │  realm:       │
│          │                       │  taskmanager  │
│          │     3. GET /api/tasks │               │
│          │     Authorization:    └───────────────┘
│          │     Bearer <JWT>             ↑
│          │ ──────────────────→  ┌──────────────┐
│          │ ←────────────────── │  Quarkus API  │── 4. Vérifie le JWT
│          │     4. Réponse JSON │  :8080        │     auprès de Keycloak
└──────────┘                     └──────────────┘
```

**Le client ne parle JAMAIS directement à la base de données.**
Il obtient un token JWT auprès de Keycloak, puis l'envoie à chaque requête.
Quarkus valide le token (signature, expiration, rôles) automatiquement.

## 👤 Utilisateurs préconfigurés

| Utilisateur | Mot de passe | Rôles | Droits |
|-------------|-------------|-------|--------|
| `alice` | `alice123` | `user` | GET (lecture seule) |
| `bob` | `bob123` | `user` + `admin` | GET, POST, PUT, DELETE |

## 🔐 Matrice de sécurité des endpoints

| Endpoint | Méthode | Accès | Annotation |
|----------|---------|-------|------------|
| `/api/tasks/public/count` | GET | 🔓 Public | `@PermitAll` |
| `/api/tasks` | GET | 🔒 user | `@RolesAllowed("user")` |
| `/api/tasks/{id}` | GET | 🔒 user | `@RolesAllowed("user")` |
| `/api/tasks/status/{s}` | GET | 🔒 user | `@RolesAllowed("user")` |
| `/api/tasks/search?q=` | GET | 🔒 user | `@RolesAllowed("user")` |
| `/api/tasks/me` | GET | 🔒 user ou admin | `@RolesAllowed({"user","admin"})` |
| `/api/tasks` | POST | 🔒 admin | `@RolesAllowed("admin")` |
| `/api/tasks/{id}` | PUT | 🔒 admin | `@RolesAllowed("admin")` |
| `/api/tasks/{id}` | DELETE | 🔒 admin | `@RolesAllowed("admin")` |
| `/api/config` | GET | 🔒 admin | `@RolesAllowed("admin")` |
| `/q/swagger-ui` | GET | 🔓 Public | Swagger UI |

---

## ⚠️ Prérequis

- **JDK 21**
- **Docker** lancé (pour Keycloak + PostgreSQL Dev Services)
- **curl** + **jq** (optionnel, pour les tests)

---

## 🚀 Étapes pour lancer la démo

### Étape 1 — Lancer Keycloak

```bash
cd v-security-jwt
docker compose up -d
```

Attendre ~30 secondes que Keycloak démarre. Vérifier :
```bash
curl -s http://localhost:8180/realms/taskmanager | jq '.realm'
# → "taskmanager"
```

> **Ce qui se passe :** Docker lance Keycloak sur le port **8180** et importe
> automatiquement le fichier `keycloak/taskmanager-realm.json` qui contient :
> - Le **realm** "taskmanager"
> - Le **client** "task-api" (public, direct access grants)
> - Les **rôles** "user" et "admin"
> - Les **utilisateurs** Alice et Bob avec leurs mots de passe

### Étape 2 — Lancer l'API Quarkus

Dans un **autre terminal** :
```bash
cd v-security-jwt
mvn quarkus:dev
```

> **Ce qui se passe :** Quarkus démarre et :
> - Lance **PostgreSQL** automatiquement via Dev Services (Docker)
> - Se connecte à **Keycloak** sur `localhost:8180/realms/taskmanager`
> - Télécharge les **clés publiques** du realm pour vérifier les JWT
> - Charge les données de `import.sql` (6 tâches)

### Étape 3 — Obtenir un token JWT

**Token Alice (user — lecture seule) :**
```bash
export TOKEN_ALICE=$(curl -s -X POST http://localhost:8180/realms/taskmanager/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=task-api" \
  -d "username=alice" \
  -d "password=alice123" | jq -r '.access_token')

echo $TOKEN_ALICE
```

**Token Bob (admin — CRUD complet) :**
```bash
export TOKEN_BOB=$(curl -s -X POST http://localhost:8180/realms/taskmanager/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=task-api" \
  -d "username=bob" \
  -d "password=bob123" | jq -r '.access_token')

echo $TOKEN_BOB
```

> **Ce qui se passe :** On envoie le login/password à Keycloak via le
> **Resource Owner Password Grant** (grant_type=password).
> Keycloak renvoie un **JWT signé** contenant les rôles de l'utilisateur.
> Le token expire après **5 minutes** (réglage par défaut Keycloak).

**Ou avec le script fourni :**
```bash
./scripts/get-token.sh alice alice123
./scripts/get-token.sh bob bob123
```

### Étape 4 — Tester les endpoints

```bash
# ─── 🔓 PUBLIC : pas besoin de token ───
curl http://127.0.0.1:8080/api/tasks/public/count
# → 6

# ─── ❌ SANS TOKEN → 401 Unauthorized ───
curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8080/api/tasks
# → 401

# ─── ✅ Alice (user) : lecture OK ───
curl -H "Authorization: Bearer $TOKEN_ALICE" http://127.0.0.1:8080/api/tasks | jq
# → [liste des 6 tâches]

# ─── ❌ Alice (user) : écriture REFUSÉE → 403 ───
curl -s -o /dev/null -w "%{http_code}" \
  -X POST -H "Authorization: Bearer $TOKEN_ALICE" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","status":"TODO","priority":"LOW"}' \
  http://127.0.0.1:8080/api/tasks
# → 403

# ─── ✅ Bob (admin) : écriture OK → 201 ───
curl -X POST -H "Authorization: Bearer $TOKEN_BOB" \
  -H "Content-Type: application/json" \
  -d '{"title":"Tâche admin","description":"Créée par Bob","status":"TODO","priority":"HIGH"}' \
  http://127.0.0.1:8080/api/tasks | jq
# → {"id":20,"title":"Tâche admin",...}

# ─── ✅ Qui suis-je ? ───
curl -H "Authorization: Bearer $TOKEN_ALICE" http://127.0.0.1:8080/api/tasks/me | jq
# → {"username":"alice","roles":["user"],"isAdmin":false}

curl -H "Authorization: Bearer $TOKEN_BOB" http://127.0.0.1:8080/api/tasks/me | jq
# → {"username":"bob","roles":["user","admin"],"isAdmin":true}
```

**Ou lancer tous les tests d'un coup :**
```bash
./scripts/test-security.sh
```

### Étape 5 — Swagger UI avec JWT

1. Ouvrir http://localhost:8080/q/swagger-ui
2. Cliquer sur le bouton **🔒 Authorize** en haut à droite
3. Coller le token Bob : `Bearer <TOKEN_BOB>`
4. Tester les endpoints directement depuis Swagger

### Étape 6 — Voir le contenu du JWT

Copier un token et le coller sur **https://jwt.io** pour voir :

```json
{
  "realm_access": {
    "roles": ["user", "admin"]     ← Quarkus lit ces rôles
  },
  "preferred_username": "bob",     ← SecurityIdentity.getPrincipal().getName()
  "email": "bob@example.com",
  "exp": 1737123456                ← Expiration (5 min par défaut)
}
```

---

## 🧠 Comment ça marche (en détail)

### Flux d'une requête authentifiée

```
1. curl envoie : Authorization: Bearer eyJhbGciOiJSUzI1NiI...
                                        │
2. Quarkus reçoit le header              │
   └→ quarkus-oidc intercepte            │
      └→ Extrait le JWT                  │
         └→ Vérifie la SIGNATURE avec la clé publique Keycloak
            └→ Vérifie l'EXPIRATION (exp)
               └→ Extrait les RÔLES depuis realm_access.roles
                  └→ Injecte dans SecurityIdentity
                     └→ @RolesAllowed("admin") vérifie le rôle
                        └→ ✅ 200 OK  ou  ❌ 403 Forbidden
```

### Configuration OIDC expliquée

```properties
# URL du realm Keycloak — Quarkus y télécharge les clés publiques
quarkus.oidc.auth-server-url=http://localhost:8180/realms/taskmanager

# Client ID — doit correspondre à celui configuré dans Keycloak
quarkus.oidc.client-id=task-api

# Mode "service" = Resource Server (vérifie le JWT, ne gère pas le login)
quarkus.oidc.application-type=service

# Où lire les rôles dans le JWT : realm_access.roles
quarkus.oidc.roles.source=realm
```

### Annotations Java

```java
@PermitAll                          // Pas de token requis
@RolesAllowed("user")              // Token avec rôle "user" requis
@RolesAllowed("admin")             // Token avec rôle "admin" requis
@RolesAllowed({"user", "admin"})   // L'un OU l'autre

// Récupérer les infos de l'utilisateur connecté
@Inject SecurityIdentity identity;
identity.getPrincipal().getName();  // → "alice"
identity.getRoles();                // → ["user"]
identity.hasRole("admin");          // → false
```

---

## 🖥️ Admin Keycloak (optionnel)

Pour voir/modifier la config Keycloak manuellement :

1. Ouvrir http://localhost:8180
2. Se connecter : `admin` / `admin`
3. Sélectionner le realm **taskmanager** (menu déroulant en haut à gauche)
4. Explorer :
   - **Users** → alice, bob (voir leurs rôles)
   - **Realm roles** → user, admin
   - **Clients** → task-api (configuration du client)

### Ajouter un utilisateur manuellement

1. Users → Add user
2. Renseigner username, email
3. Tab "Credentials" → Set password (désactiver "Temporary")
4. Tab "Role mapping" → Assign role → user et/ou admin

---

## 📁 Structure du projet

```
v-security-jwt/
├── docker-compose.yml              ← Lance Keycloak sur :8180
├── keycloak/
│   └── taskmanager-realm.json      ← Realm pré-configuré (import auto)
├── scripts/
│   ├── get-token.sh                ← Obtenir un JWT facilement
│   └── test-security.sh            ← Tester tous les scénarios
├── pom.xml
└── src/main/
    ├── java/com/example/taskmanager/
    │   ├── config/
    │   │   ├── AppConfig.java          ← @ConfigMapping
    │   │   └── AppLifecycle.java
    │   ├── dto/
    │   │   └── TaskDTO.java            ← @NotBlank, @Size
    │   ├── entity/
    │   │   ├── Task.java               ← PanacheEntity
    │   │   ├── Status.java
    │   │   └── Priority.java
    │   ├── exception/
    │   │   └── GlobalExceptionMapper.java
    │   ├── mapper/
    │   │   └── TaskMapper.java
    │   ├── repository/
    │   │   └── TaskRepository.java     ← PanacheRepository
    │   ├── resource/
    │   │   ├── TaskResource.java       ← ✨ @RolesAllowed + @PermitAll
    │   │   └── ConfigResource.java     ← ✨ @RolesAllowed("admin")
    │   └── service/
    │       └── TaskService.java
    └── resources/
        ├── application.properties      ← ✨ Config OIDC/Keycloak
        └── import.sql
```

## 🆕 Ce qui change par rapport à la version OpenAPI

| Fichier | Changement |
|---------|-----------|
| `pom.xml` | + `quarkus-oidc` |
| `application.properties` | + section OIDC (4 lignes) + CORS |
| `TaskResource.java` | + `@RolesAllowed`, `@PermitAll`, `SecurityIdentity`, `@SecurityScheme` |
| `ConfigResource.java` | + `@RolesAllowed("admin")` |
| `docker-compose.yml` | **NOUVEAU** — Keycloak |
| `keycloak/taskmanager-realm.json` | **NOUVEAU** — Realm pré-configuré |
| `scripts/get-token.sh` | **NOUVEAU** — Helper pour obtenir un JWT |
| `scripts/test-security.sh` | **NOUVEAU** — Tests automatisés |

## 📊 Comparaison Spring Boot

| Concept | Quarkus | Spring Boot |
|---------|---------|-------------|
| Dépendance | `quarkus-oidc` | `spring-boot-starter-oauth2-resource-server` |
| Config serveur | `quarkus.oidc.auth-server-url` | `spring.security.oauth2.resourceserver.jwt.issuer-uri` |
| Rôles | `@RolesAllowed("admin")` | `@PreAuthorize("hasRole('ADMIN')")` |
| Public | `@PermitAll` | `.permitAll()` dans SecurityFilterChain |
| Identité | `SecurityIdentity` (CDI) | `SecurityContextHolder.getContext()` |
| Mapping rôles | `quarkus.oidc.roles.source=realm` | Custom `JwtAuthenticationConverter` |
| Swagger + JWT | `@SecurityScheme` (OpenAPI) | `@SecurityScheme` (springdoc) |

## ⏹️ Arrêter la démo

```bash
# Arrêter Keycloak
docker compose down

# Arrêter Quarkus
# Ctrl+C dans le terminal quarkus:dev
```

## ❓ Troubleshooting

**"OIDC server is not available"** → Keycloak pas encore démarré. Attendre 30s ou vérifier `docker compose logs keycloak`.

**"401 Unauthorized" alors que le token est bon** → Le token a expiré (5 min). En demander un nouveau avec `get-token.sh`.

**"403 Forbidden" avec Bob** → Vérifier que Bob a bien le rôle "admin" dans Keycloak → Users → bob → Role mapping.

**Port 8180 occupé** → Changer le port dans `docker-compose.yml` et dans `application.properties`.
