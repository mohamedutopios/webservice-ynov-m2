package services;

import entity.Status;
import entity.Task;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
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


    public List<Task> getTasks() {
        return Task.listAll();
    }

    public Task findById(Long id) {
       Task task =  Task.findById(id);
        if (task == null) {
            throw new NotFoundException("Tâche non trouvée :=" + id);
        }
        return task;
    }

    @Transactional
    public Task create(Task task) {
        task.persist();
        return task;
    }

    @Transactional
    public Task update(Long id, Task input) {
        Task task =  Task.findById(id);
        task.title = input.title;
        task.description = input.description;
        task.status = input.status;
        task.priority = input.priority;
        return task;
    }

    @Transactional
    public void delete(Long id) {
        if (!Task.deleteById(id)) {
            throw new NotFoundException("Tâche non trouvée : id=" + id);
        }
    }

    public List<Task> findByStatus(Status status) {
        return Task.findByStatus(status);
    }

    public List<Task> search(String query) {
        return Task.search(query);
    }

    public long count() {
        return Task.count();
    }




}
