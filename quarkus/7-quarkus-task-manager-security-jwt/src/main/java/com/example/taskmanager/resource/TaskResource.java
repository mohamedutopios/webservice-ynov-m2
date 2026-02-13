package com.example.taskmanager.resource;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.service.TaskService;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tasks", description = "CRUD des tâches (sécurisé JWT)")
@SecurityScheme(
        securitySchemeName = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@SecurityRequirement(name = "bearerAuth")
public class TaskResource {

    private final TaskService taskService;
    private final SecurityIdentity identity;

    public TaskResource(TaskService taskService, SecurityIdentity identity) {
        this.taskService = taskService;
        this.identity = identity;
    }

    // =====================================================
    //  PUBLIC — pas besoin de token
    // =====================================================

    @GET
    @Path("/public/count")
    @PermitAll
    @Operation(summary = "Nombre de tâches (public)")
    public long count() {
        return taskService.count();
    }

    // =====================================================
    //  ROLE "user" — lecture seule
    // =====================================================

    @GET
    @RolesAllowed("user")
    @Operation(summary = "Lister toutes les tâches")
    @APIResponse(responseCode = "200", description = "Liste des tâches")
    @APIResponse(responseCode = "401", description = "Non authentifié")
    @APIResponse(responseCode = "403", description = "Rôle insuffisant")
    public List<TaskDTO> getAll() {
        return taskService.findAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("user")
    @Operation(summary = "Détail d'une tâche")
    public TaskDTO getById(@PathParam("id") Long id) {
        return taskService.findById(id);
    }

    @GET
    @Path("/status/{status}")
    @RolesAllowed("user")
    @Operation(summary = "Filtrer par statut")
    public List<TaskDTO> byStatus(@PathParam("status") Status status) {
        return taskService.findByStatus(status);
    }

    @GET
    @Path("/search")
    @RolesAllowed("user")
    @Operation(summary = "Recherche par titre")
    public List<TaskDTO> search(@QueryParam("q") String q) {
        if (q == null || q.isBlank()) return taskService.findAll();
        return taskService.search(q);
    }

    // =====================================================
    //  ROLE "admin" — écriture (CRUD complet)
    // =====================================================

    @POST
    @RolesAllowed("admin")
    @Operation(summary = "Créer une tâche (admin)")
    @APIResponse(responseCode = "201", description = "Tâche créée")
    @APIResponse(responseCode = "400", description = "Validation échouée")
    @APIResponse(responseCode = "403", description = "Rôle admin requis")
    public Response create(@Valid TaskDTO dto) {
        TaskDTO created = taskService.create(dto);
        return Response.status(201).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    @Operation(summary = "Modifier une tâche (admin)")
    public TaskDTO update(@PathParam("id") Long id, @Valid TaskDTO dto) {
        return taskService.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Operation(summary = "Supprimer une tâche (admin)")
    public Response delete(@PathParam("id") Long id) {
        taskService.delete(id);
        return Response.noContent().build();
    }

    // =====================================================
    //  Endpoint "me" — infos utilisateur connecté
    // =====================================================

    @GET
    @Path("/me")
    @RolesAllowed({"user", "admin"})
    @Operation(summary = "Infos de l'utilisateur connecté")
    public java.util.Map<String, Object> me() {
        return java.util.Map.of(
                "username", identity.getPrincipal().getName(),
                "roles", identity.getRoles(),
                "isAdmin", identity.hasRole("admin")
        );
    }
}
