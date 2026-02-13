package com.example.taskmanager.service;

import java.util.List;

import com.example.taskmanager.config.AppConfig;
import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.TaskRepository;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;


@ApplicationScoped
public class TaskService {

    private final TaskRepository taskRepository;
    private final AppConfig config;

    // Injection par constructeur : Repository + Config
    public TaskService(TaskRepository taskRepository, AppConfig config) {
        this.taskRepository = taskRepository;
        this.config = config;
    }

    public List<TaskDTO> findAll() {
        return taskRepository.listAll().stream()
                .map(TaskMapper::toDTO)
                .toList();
    }

    public TaskDTO findById(Long id) {
        Task task = taskRepository.findById(id);
        if (task == null) throw new NotFoundException("Tâche non trouvée : id=" + id);
        return TaskMapper.toDTO(task);
    }

    @Transactional
    public TaskDTO create(@Valid TaskDTO dto) {
        Task task = TaskMapper.toEntity(dto);
        taskRepository.persist(task);
        Log.infof("[%s] Tâche créée : id=%d, title=%s", config.name(), task.id, task.title);
        return TaskMapper.toDTO(task);
    }

    @Transactional
    public TaskDTO update(Long id, @Valid TaskDTO dto) {
        Task task = taskRepository.findById(id);
        if (task == null) throw new NotFoundException("Tâche non trouvée : id=" + id);
        TaskMapper.updateEntity(task, dto);
        // Dirty checking JPA → pas besoin de persist()
        return TaskMapper.toDTO(task);
    }

    @Transactional
    public void delete(Long id) {
        if (!taskRepository.deleteById(id)) {
            throw new NotFoundException("Tâche non trouvée : id=" + id);
        }
    }

    public List<TaskDTO> findByStatus(Status status) {
        return taskRepository.findByStatus(status).stream()
                .map(TaskMapper::toDTO)
                .toList();
    }


    public List<TaskDTO> search(String query) {
        if (!config.enableSearch()) {
            throw new UnsupportedOperationException("La recherche est désactivée");
        }
        return taskRepository.search(query).stream()
                .map(TaskMapper::toDTO)
                .toList();
    }

  
    public List<TaskDTO> findPaginated(int page, Integer size) {
        int effectiveSize = (size != null) ? size : config.pagination().defaultSize();
        // Plafonner au max configuré
        effectiveSize = Math.min(effectiveSize, config.pagination().maxSize());
        return taskRepository.findPaginated(page, effectiveSize).stream()
                .map(TaskMapper::toDTO)
                .toList();
    }

    public long count() {
        return taskRepository.count();
    }
}
