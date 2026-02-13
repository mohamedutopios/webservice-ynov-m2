package services;

import dto.TaskDTO;
import entity.Status;
import entity.Task;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import mapper.TaskMapper;
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

    private final TaskMapper mapper;

    public TaskService(TaskMapper mapper) {
        this.mapper = mapper;
    }


    public List<TaskDTO> getTasks() {

        return Task.<Task>listAll().stream().map(mapper::toDTO).toList();
    }

    public TaskDTO findById(Long id) {
       Task task =  Task.findById(id);
        if (task == null) {
            throw new NotFoundException("Tâche non trouvée :=" + id);
        }
        return mapper.toDTO(task);
    }

    @Transactional
    public TaskDTO create(TaskDTO dto) {
        Task task = mapper.toEntity(dto);
        task.persist();
        Log.infof("Tâche créée : id=%d, title=%s", task.id, task.title);
        return mapper.toDTO(task);
    }

    @Transactional
    public TaskDTO update(Long id, TaskDTO input) {
        Task task =  Task.findById(id);
        task.title = input.title;
        task.description = input.description;
        task.status = input.status;
        task.priority = input.priority;
        return mapper.toDTO(task);
    }

    @Transactional
    public void delete(Long id) {
        if (!Task.deleteById(id)) {
            throw new NotFoundException("Tâche non trouvée : id=" + id);
        }
    }

    public List<TaskDTO> findByStatus(Status status) {
        return Task.findByStatus(status).stream().map(mapper::toDTO).toList();
    }

    public List<TaskDTO> search(String query) {
        return Task.search(query).stream().map(mapper::toDTO).toList();
    }

    public long count() {
        return Task.count();
    }




}
