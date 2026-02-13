package services;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class TaskService {


    @ConfigProperty(name="app.name", defaultValue = "TaskManger")
    String name;

    @ConfigProperty(name="app.default-priority", defaultValue = "MEDIUM")
    String defaultPriority;


    private static final Map<Long, Map<String, Object>> tasks = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);

    static {
        addTask("Découvre Quarkus", "Installer et lancer le premier projet", "TODO", "HIGH");
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


    public List<Map<String, Object>> getTasks() {
        Log.infof("[%s] Récupération de %d tâches", name, tasks.size());
        return new ArrayList<>(tasks.values());
    }

    public Map<String, Object> findById(Long id) {
        Map<String, Object> task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Tâche non trouvée :=" + id);
        }
        return task;
    }

    public Map<String, Object> create(Map<String, Object> body) {
        long id = idGenerator.getAndIncrement();
        Map<String, Object> task = new LinkedHashMap<>();
        task.put("id", id);
        task.put("title", body.get("title"));
        task.put("description", body.getOrDefault("description", ""));
        task.put("status", body.getOrDefault("status", "TODO"));
        task.put("priority", body.getOrDefault("priority", defaultPriority));
        task.put("createdAt", LocalDateTime.now().toString());
        tasks.put(id, task);
        Log.infof("[%s] Tâche créée : id=%d, title=%s", name, id, body.get("title"));
        return task;
    }

    public Map<String, Object> update(Long id, Map<String, Object> body) {
        Map<String, Object> task = findById(id); // throws NotFoundException
        if (body.containsKey("title")) task.put("title", body.get("title"));
        if (body.containsKey("description")) task.put("description", body.get("description"));
        if (body.containsKey("status")) task.put("status", body.get("status"));
        if (body.containsKey("priority")) task.put("priority", body.get("priority"));
        task.put("updatedAt", LocalDateTime.now().toString());
        return task;
    }

    public void delete(Long id) {
        if (tasks.remove(id) == null) {
            throw new NotFoundException("Tâche non trouvée : id=" + id);
        }
        Log.infof("[%s] Tâche supprimée : id=%d", name, id);
    }

    public List<Map<String, Object>> search(String query) {
        String q = query.toLowerCase();
        return tasks.values().stream()
                .filter(t -> t.get("title").toString().toLowerCase().contains(q))
                .toList();
    }

    public long count() {
        return tasks.size();
    }




}
