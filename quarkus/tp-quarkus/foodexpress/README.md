# 🚀 FoodExpress — Microservices Quarkus

Plateforme de livraison de repas composée de 4 microservices Quarkus.

## Architecture

| Service | Port | Description |
|---|---|---|
| **customer-service** | 8081 | Gestion des clients |
| **restaurant-service** | 8082 | Gestion des restaurants et menus |
| **order-service** | 8083 | Gestion des commandes (appelle les 3 autres services) |
| **delivery-service** | 8084 | Gestion des livraisons et livreurs |

## Prérequis

- JDK 17+
- Maven 3.9+
- Docker & Docker Compose

## Démarrage rapide

### 1. Lancer les bases de données

```bash
docker-compose up -d
```

Cela crée 4 instances PostgreSQL sur les ports 5441-5444.

### 2. Lancer les microservices (4 terminaux)

```bash
# Terminal 1
cd customer-service && mvn quarkus:dev

# Terminal 2
cd restaurant-service && mvn quarkus:dev

# Terminal 3
cd order-service && mvn quarkus:dev

# Terminal 4
cd delivery-service && mvn quarkus:dev
```

### 3. Accéder aux services

- Customer Swagger UI : http://localhost:8081/q/swagger-ui
- Restaurant Swagger UI : http://localhost:8082/q/swagger-ui
- Order Swagger UI : http://localhost:8083/q/swagger-ui
- Delivery Swagger UI : http://localhost:8084/q/swagger-ui
- Dev UI : http://localhost:808X/q/dev-ui

## Test du flux complet

Utilisez le fichier `test-requests.http` ou les commandes curl suivantes :

```bash
# 1. Créer un client
curl -X POST http://localhost:8081/api/customers \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Mohamed","lastName":"Ali","email":"mohamed@test.com","phone":"+33612345678","address":"10 rue de la Paix","city":"Paris","zipCode":"75001"}'

# 2. Créer un restaurant
curl -X POST http://localhost:8082/api/restaurants \
  -H "Content-Type: application/json" \
  -d '{"name":"Le Petit Bistrot","cuisine":"Française","address":"5 avenue des Champs","city":"Paris","phone":"+33698765432","openingTime":"09:00","closingTime":"23:00"}'

# 3. Ajouter des plats
curl -X POST http://localhost:8082/api/restaurants/1/dishes \
  -H "Content-Type: application/json" \
  -d '{"name":"Steak Frites","description":"Steak grillé avec frites maison","price":18.50,"category":"MAIN"}'

curl -X POST http://localhost:8082/api/restaurants/1/dishes \
  -H "Content-Type: application/json" \
  -d '{"name":"Crème Brûlée","description":"Crème vanille caramélisée","price":8.00,"category":"DESSERT"}'

# 4. Créer un livreur
curl -X POST http://localhost:8084/api/drivers \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Karim","lastName":"Benzema","phone":"+33655443322","vehicleType":"SCOOTER","currentZone":"Paris-Centre"}'

# 5. Passer une commande
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"restaurantId":1,"deliveryAddress":"10 rue de la Paix, Paris","items":[{"dishId":1,"quantity":2},{"dishId":2,"quantity":1}]}'

# 6. Vue agrégée
curl http://localhost:8083/api/orders/1/full
```

## Structure du projet

```
foodexpress/
├── docker-compose.yml
├── README.md
├── test-requests.http
├── customer-service/       ← Active Record Pattern
├── restaurant-service/     ← Active Record Pattern
├── order-service/          ← Repository Pattern + REST Clients
└── delivery-service/       ← Repository Pattern
```

## Concepts couverts

- **CDI** : `@ApplicationScoped`, `@Inject`, `@RestClient`, `@ConfigMapping`
- **JAX-RS** : CRUD complet, pagination, filtres, communication inter-services
- **Panache** : Active Record (`PanacheEntity`) + Repository (`PanacheRepository`)
- **Validation** : Bean Validation, contraintes personnalisées
- **Gestion d'erreurs** : `ExceptionMapper`, format standardisé, erreurs inter-services
