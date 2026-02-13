package resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {


    private static final Map<Long, Map<String, Object>> tasks = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);

    static {
        addTask("Découvrir Quarkus", "Installer et lancer le premier projet", "TODO", "HIGH");
        addTask("Apprendre JAX-RS", "Créer des endpoints REST", "IN_PROGRESS", "MEDIUM");
        addTask("Configurer le projet", "pom.xml et application.properties", "DONE", "LOW");
    }

    private static void addTask(String title, String description, String status, String priority) {
        long id = idGenerator.getAndIncrement();
        Map<String, Object> task = new LinkedHashMap<>();
        task.put("id", id);
        task.put("title", title);
        task.put("description", description);
        task.put("status", status);
        task.put("priority", priority);
        task.put("createdAt", LocalDateTime.now().toString());
        tasks.put(id, task);
    }


    @GET
    public List<Map<String, Object>> getTasks() {
        return new ArrayList<>(tasks.values());

    }

    @GET
    @Path("/{id}")
    public Map<String, Object> getTask(@PathParam("id") long id) {
        Map<String, Object> task = tasks.get(id);
        if(task == null) {
            throw new NotFoundException("Task not found with id " + id);
        }
        return task;
    }

    @POST
    public Response create(Map<String, Object> body) {
        long id = idGenerator.getAndIncrement();
        Map<String, Object> task = new LinkedHashMap<>();
        task.put("id", id);
        task.put("title", body.get("title"));
        task.put("description", body.get("description"));
        task.put("status", body.getOrDefault("status", "TODO"));
        task.put("priority", body.getOrDefault("priority", "MEDIUM"));
        task.put("createdAt", LocalDateTime.now().toString());
        tasks.put(id, task);

        return Response.status(Response.Status.CREATED).entity(task).build();
    }

    @PUT
    @Path("/{id}")
    public Map<String, Object> update(@PathParam("id") Long id, Map<String, Object> body) {
        Map<String, Object> task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Tâche non trouvée : id=" + id);
        }
        if (body.containsKey("title")) task.put("title", body.get("title"));
        if (body.containsKey("description")) task.put("description", body.get("description"));
        if (body.containsKey("status")) task.put("status", body.get("status"));
        if (body.containsKey("priority")) task.put("priority", body.get("priority"));
        task.put("updatedAt", LocalDateTime.now().toString());
        return task;
    }


    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Map<String, Object> removed = tasks.remove(id);
        if (removed == null) {
            throw new NotFoundException("Tâche non trouvée : id=" + id);
        }
        return Response.noContent().build();
    }


    @GET
    @Path("/search")
    public List<Map<String, Object>> search(@QueryParam("q") String query) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>(tasks.values());
        }
        String q = query.toLowerCase();
        return tasks.values().stream()
                .filter(t -> t.get("title").toString().toLowerCase().contains(q))
                .toList();
    }

}
