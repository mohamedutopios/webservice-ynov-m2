package services;

import entity.Status;
import entity.Task;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    public List<Task> getTasks() {
        return taskRepository.listAll();
    }

    public Task findById(Long id) {
       Task task =  taskRepository.findById(id);
        if (task == null) {
            throw new NotFoundException("Tâche non trouvée :=" + id);
        }
        return task;
    }

    @Transactional
    public Task create(Task task) {
        taskRepository.persist(task);
        return task;
    }

    @Transactional
    public Task update(Long id, Task input) {
        Task task =  taskRepository.findById(id);
        task.title = input.title;
        task.description = input.description;
        task.status = input.status;
        task.priority = input.priority;
        return task;
    }

    @Transactional
    public void delete(Long id) {
        if (!taskRepository.deleteById(id)) {
            throw new NotFoundException("Tâche non trouvée : id=" + id);
        }
    }

    public List<Task> findByStatus(Status status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> search(String query) {
        return taskRepository.search(query);
    }

    public long count() {
        return taskRepository.count();
    }




}
