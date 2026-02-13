package com.example.taskmanager;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

/**
 * ============================================================
 * VERSION 6 — Tests @QuarkusTest + RestAssured
 * ============================================================
 * Concepts introduits :
 *   ✅ @QuarkusTest       → démarre l'application complète (≈ @SpringBootTest)
 *   ✅ @TestMethodOrder    → ordre d'exécution des tests
 *   ✅ RestAssured         → DSL fluide pour tester des API REST
 *      given().when().then() → pattern BDD (Behavior-Driven Development)
 *   ✅ Profil %test        → H2 en mémoire, pas besoin de Docker
 *
 * Comparaison Spring Boot :
 *   @SpringBootTest + @AutoConfigureMockMvc + MockMvc
 *   → RestAssured est plus lisible et plus expressif !
 *
 * Lancer :
 *   ./mvnw test
 *   ou dans quarkus:dev → appuyer sur 'r' pour relancer les tests
 * ============================================================
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskResourceTest {

    // =====================================================
    // TEST 1 : GET /api/tasks → Liste toutes les tâches
    // =====================================================
    @Test
    @Order(1)
    void shouldListAllTasks() {
        given()
            .when()
                .get("/api/tasks")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThan(0)))     // Au moins 1 tâche
                .body("[0].title", notNullValue());      // Premier élément a un titre
    }

    // =====================================================
    // TEST 2 : GET /api/tasks/{id} → Détail d'une tâche
    // =====================================================
    @Test
    @Order(2)
    void shouldGetTaskById() {
        given()
            .when()
                .get("/api/tasks/1")
            .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", notNullValue())
                .body("status", notNullValue());
    }

    // =====================================================
    // TEST 3 : GET /api/tasks/999 → 404 Not Found
    // =====================================================
    @Test
    @Order(3)
    void shouldReturn404ForUnknownTask() {
        given()
            .when()
                .get("/api/tasks/999")
            .then()
                .statusCode(404);
    }

    // =====================================================
    // TEST 4 : POST /api/tasks → Créer une tâche (201)
    // =====================================================
    @Test
    @Order(4)
    void shouldCreateTask() {
        String json = """
            {
                "title": "Tâche de test",
                "description": "Créée par RestAssured",
                "status": "TODO",
                "priority": "HIGH"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(json)
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("Tâche de test"))
            .body("status", equalTo("TODO"))
            .body("priority", equalTo("HIGH"));
    }

    // =====================================================
    // TEST 5 : POST avec validation → 400 Bad Request
    // =====================================================
    @Test
    @Order(5)
    void shouldRejectInvalidTask_blankTitle() {
        String json = """
            {
                "title": "",
                "status": "TODO",
                "priority": "HIGH"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(json)
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(400);
    }

    @Test
    @Order(6)
    void shouldRejectInvalidTask_titleTooShort() {
        String json = """
            {
                "title": "AB",
                "status": "TODO",
                "priority": "HIGH"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(json)
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(400);
    }

    @Test
    @Order(7)
    void shouldRejectInvalidTask_missingStatus() {
        String json = """
            {
                "title": "Test sans statut",
                "priority": "HIGH"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(json)
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(400);
    }

    // =====================================================
    // TEST 8 : PUT /api/tasks/{id} → Modifier
    // =====================================================
    @Test
    @Order(8)
    void shouldUpdateTask() {
        String json = """
            {
                "title": "Tâche modifiée",
                "description": "Mise à jour par RestAssured",
                "status": "DONE",
                "priority": "LOW"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(json)
        .when()
            .put("/api/tasks/1")
        .then()
            .statusCode(200)
            .body("title", equalTo("Tâche modifiée"))
            .body("status", equalTo("DONE"));
    }

    // =====================================================
    // TEST 9 : DELETE /api/tasks/{id} → 204 No Content
    // =====================================================
    @Test
    @Order(9)
    void shouldDeleteTask() {
        // D'abord créer une tâche à supprimer
        int id = given()
            .contentType(ContentType.JSON)
            .body("""
                {"title":"A supprimer","status":"TODO","priority":"LOW"}
            """)
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(201)
            .extract().path("id");

        // Puis la supprimer
        given()
            .when()
                .delete("/api/tasks/" + id)
            .then()
                .statusCode(204);

        // Vérifier qu'elle n'existe plus
        given()
            .when()
                .get("/api/tasks/" + id)
            .then()
                .statusCode(404);
    }

    // =====================================================
    // TEST 10 : GET /api/tasks/status/{status} → Filtrer
    // =====================================================
    @Test
    @Order(10)
    void shouldFilterByStatus() {
        given()
            .when()
                .get("/api/tasks/status/TODO")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    // =====================================================
    // TEST 11 : GET /api/tasks/search?q=xxx → Recherche
    // =====================================================
    @Test
    @Order(11)
    void shouldSearchTasks() {
        given()
            .queryParam("q", "Quarkus")
        .when()
            .get("/api/tasks/search")
        .then()
            .statusCode(200);
    }

    // =====================================================
    // TEST 12 : GET /api/config → Vérifier la config
    // =====================================================
    @Test
    @Order(12)
    void shouldReturnConfig() {
        given()
            .when()
                .get("/api/config")
            .then()
                .statusCode(200)
                .body("application.name", equalTo("TaskManager"));
    }
}
