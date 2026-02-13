# V8 — Docker & Compilation Native (Production)

## 🎯 Nouveaux concepts (par rapport à v7)
- **Dockerfile.jvm** : multi-stage build, image ~200MB, startup ~1s
- **Dockerfile.native** : compilation GraalVM AOT, image ~50MB, startup ~0.02s
- **docker-compose.yml** : orchestration App + PostgreSQL
- **Profils Docker Compose** : `--profile jvm` ou `--profile native`
- **Limites mémoire** : JVM=256MB, Native=64MB

## 📁 Nouveaux fichiers
```
├── src/main/docker/
│   ├── Dockerfile.jvm       ← ✨ NOUVEAU : image JVM multi-stage
│   └── Dockerfile.native    ← ✨ NOUVEAU : image Native GraalVM
└── docker-compose.yml       ← ✨ NOUVEAU : app + PostgreSQL
```

## 🚀 Lancer

### Mode développement (hot reload)
```bash
./mvnw quarkus:dev
```

### Mode JVM (Docker)
```bash
docker compose --profile jvm up -d
docker compose logs -f app-jvm
curl http://localhost:8080/api/tasks
```

### Mode Native (Docker)
```bash
# ⚠️ Build initial : 3-5 min, 4GB+ RAM
docker compose --profile native up -d --build
docker compose logs -f app-native
curl http://localhost:8080/api/tasks
```

### Build sans Docker Compose
```bash
# JVM
docker build -f src/main/docker/Dockerfile.jvm -t task-manager:jvm .
docker run -p 8080:8080 --env DB_HOST=host.docker.internal task-manager:jvm

# Native
docker build -f src/main/docker/Dockerfile.native -t task-manager:native .
docker run -p 8080:8080 --env DB_HOST=host.docker.internal task-manager:native
```

## 📊 Comparaison JVM vs Native

| Métrique | JVM (OpenJDK) | Native (GraalVM) |
|----------|:-------------:|:----------------:|
| Startup | ~1s | **~0.02s** |
| RAM (idle) | ~120 MB | **~15 MB** |
| Image Docker | ~200 MB | **~50 MB** |
| Build time | ~10s | ~3-5 min |
| Peak throughput | **Meilleur** (JIT optimize) | Bon |
| Warm-up | Nécessaire | Aucun |

## 🔗 URLs complètes
```bash
# API
curl http://localhost:8080/api/tasks
curl http://localhost:8080/api/tasks/1
curl http://localhost:8080/api/config

# MicroProfile
curl http://localhost:8080/q/health | jq
curl http://localhost:8080/q/health/live | jq
curl http://localhost:8080/q/health/ready | jq

# Documentation
open http://localhost:8080/q/swagger-ui
curl http://localhost:8080/q/openapi
open http://localhost:8080/q/dev-ui   # dev mode only
```

## 🧹 Nettoyage
```bash
docker compose --profile jvm down -v
docker compose --profile native down -v
```

---

## 📋 Récapitulatif de toutes les versions

| Version | Concepts | Fichiers clés |
|---------|----------|---------------|
| **v1** | JAX-RS (@Path, @GET, @POST...) | TaskResource.java |
| **v2** | CDI (@ApplicationScoped), injection constructeur | + TaskService.java, AppLifecycle.java |
| **v3** | Panache (PanacheEntity), @Transactional, Dev Services | + Task.java (entity), import.sql |
| **v4** | Bean Validation (@Valid, @NotBlank), ExceptionMapper | + TaskDTO, TaskMapper, GlobalExceptionMapper |
| **v5** | @ConfigMapping, profils %dev/%test/%prod | + AppConfig.java, ConfigResource.java |
| **v6** | @QuarkusTest, RestAssured, continuous testing | + TaskResourceTest.java (12 tests) |
| **v7** | OpenAPI/Swagger, @Liveness/@Readiness, @Timeout/@Fallback | + HealthChecks, annotations OpenAPI |
| **v8** | Docker JVM + Native, docker-compose, production | + Dockerfiles, docker-compose.yml |

---

**Formation Quarkus — Mohamed / 2iTech**
