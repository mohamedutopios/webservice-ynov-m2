package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.entity.Task;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * VERSION 4 — Mapper Entity ↔ DTO
 * Sépare la représentation API de l'entité JPA.
 */
@ApplicationScoped
public class TaskMapper {

    public TaskDTO toDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.id = task.id;
        dto.title = task.title;
        dto.description = task.description;
        dto.status = task.status;
        dto.priority = task.priority;
        dto.createdAt = task.createdAt;
        dto.updatedAt = task.updatedAt;
        return dto;
    }

    public Task toEntity(TaskDTO dto) {
        Task task = new Task();
        task.title = dto.title;
        task.description = dto.description;
        task.status = dto.status;
        task.priority = dto.priority;
        return task;
    }

    public void updateEntity(Task task, TaskDTO dto) {
        task.title = dto.title;
        task.description = dto.description;
        task.status = dto.status;
        task.priority = dto.priority;
    }
}
