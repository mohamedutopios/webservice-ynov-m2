package resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.TaskService;

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


    private final TaskService taskService;

    public TaskResource(TaskService taskService) {
        this.taskService = taskService;
    }


    @GET
    public List<Map<String, Object>> getTasks() {
        return taskService.getTasks();
    }

    @GET
    @Path("/{id}")
    public Map<String, Object> getById(@PathParam("id") Long id) {
        return taskService.findById(id);
    }

    @POST
    public Response create(Map<String, Object> body) {
        Map<String, Object> created = taskService.create(body);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Map<String, Object> update(@PathParam("id") Long id, Map<String, Object> body) {
        return taskService.update(id, body);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        taskService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/search")
    public List<Map<String, Object>> search(@QueryParam("q") String query) {
        if (query == null || query.isBlank()) return taskService.getTasks();
        return taskService.search(query);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public long count() {
        return taskService.count();
    }

}
