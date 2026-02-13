package com.example.taskmanager.resource;

import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.util.List;

/**
 * ============================================================
 * VERSION 7 — OpenAPI + Fault Tolerance
 * ============================================================
 * Concepts introduits :
 *   ✅ @Tag, @Operation, @APIResponse → documentation OpenAPI auto
 *      → Swagger UI : http://localhost:8080/q/swagger-ui
 *      → JSON spec  : http://localhost:8080/q/openapi
 *   ✅ @Timeout       → timeout automatique (MicroProfile Fault Tolerance)
 *   ✅ @Fallback      → méthode de secours si timeout/erreur
 *
 * Équivalent Spring : SpringDoc OpenAPI + @ApiOperation (Swagger)
 * ============================================================
 */
@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tasks", description = "Gestion des tâches — CRUD complet")
public class TaskResource {

    private final TaskService taskService;

    public TaskResource(TaskService taskService) {
        this.taskService = taskService;
    }

    @GET
    @Operation(summary = "Lister toutes les tâches", description = "Retourne la liste complète des tâches")
    @APIResponse(responseCode = "200", description = "Liste des tâches",
            content = @Content(schema = @Schema(implementation = TaskDTO.class)))
    @Timeout(5000)               // ← 5 secondes max
    @Fallback(fallbackMethod = "fallbackGetAll")
    public List<TaskDTO> getAll() {
        return taskService.findAll();
    }

    // Méthode de secours si timeout
    public List<TaskDTO> fallbackGetAll() {
        return List.of(); // Retourne une liste vide plutôt qu'une erreur
    }

    @GET @Path("/{id}")
    @Operation(summary = "Obtenir une tâche par ID")
    @APIResponse(responseCode = "200", description = "Tâche trouvée")
    @APIResponse(responseCode = "404", description = "Tâche non trouvée")
    public TaskDTO getById(
            @Parameter(description = "ID de la tâche", required = true)
            @PathParam("id") Long id) {
        return taskService.findById(id);
    }

    @POST
    @Operation(summary = "Créer une nouvelle tâche")
    @APIResponse(responseCode = "201", description = "Tâche créée")
    @APIResponse(responseCode = "400", description = "Données invalides")
    public Response create(@Valid TaskDTO dto) {
        TaskDTO created = taskService.create(dto);
        return Response.status(201).entity(created).build();
    }

    @PUT @Path("/{id}")
    @Operation(summary = "Modifier une tâche existante")
    @APIResponse(responseCode = "200", description = "Tâche modifiée")
    @APIResponse(responseCode = "404", description = "Tâche non trouvée")
    public TaskDTO update(@PathParam("id") Long id, @Valid TaskDTO dto) {
        return taskService.update(id, dto);
    }

    @DELETE @Path("/{id}")
    @Operation(summary = "Supprimer une tâche")
    @APIResponse(responseCode = "204", description = "Tâche supprimée")
    @APIResponse(responseCode = "404", description = "Tâche non trouvée")
    public Response delete(@PathParam("id") Long id) {
        taskService.delete(id);
        return Response.noContent().build();
    }

    @GET @Path("/status/{status}")
    @Operation(summary = "Filtrer les tâches par statut")
    public List<TaskDTO> byStatus(
            @Parameter(description = "Statut : TODO, IN_PROGRESS, DONE, CANCELLED")
            @PathParam("status") Status status) {
        return taskService.findByStatus(status);
    }

    @GET @Path("/search")
    @Operation(summary = "Rechercher des tâches par titre")
    public List<TaskDTO> search(
            @Parameter(description = "Terme de recherche")
            @QueryParam("q") String q) {
        if (q == null || q.isBlank()) return taskService.findAll();
        return taskService.search(q);
    }
}
