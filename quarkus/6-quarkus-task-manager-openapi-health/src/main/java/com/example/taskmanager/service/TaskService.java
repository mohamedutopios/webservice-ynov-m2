package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.mapper.TaskMapper;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class TaskService {

    private final TaskMapper mapper;

    public TaskService(TaskMapper mapper) {
        this.mapper = mapper;
    }

    public List<TaskDTO> findAll() {
        return Task.<Task>listAll().stream().map(mapper::toDTO).toList();
    }

    public TaskDTO findById(Long id) {
        Task task = Task.findById(id);
        if (task == null) throw new NotFoundException("Tâche non trouvée : id=" + id);
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
    public TaskDTO update(Long id, TaskDTO dto) {
        Task task = Task.findById(id);
        if (task == null) throw new NotFoundException("Tâche non trouvée : id=" + id);
        mapper.updateEntity(task, dto);
        return mapper.toDTO(task);
    }

    @Transactional
    public void delete(Long id) {
        if (!Task.deleteById(id)) throw new NotFoundException("Tâche non trouvée : id=" + id);
    }

    public List<TaskDTO> findByStatus(Status status) {
        return Task.findByStatus(status).stream().map(mapper::toDTO).toList();
    }

    public List<TaskDTO> search(String query) {
        return Task.search(query).stream().map(mapper::toDTO).toList();
    }
}
