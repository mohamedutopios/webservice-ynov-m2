package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.entity.Task;

public class TaskMapper {

    public static TaskDTO toDTO(Task entity) {
        TaskDTO dto = new TaskDTO();
        dto.id = entity.id;
        dto.title = entity.title;
        dto.description = entity.description;
        dto.status = entity.status;
        dto.priority = entity.priority;
        dto.createdAt = entity.createdAt;
        dto.updatedAt = entity.updatedAt;
        return dto;
    }

    public static Task toEntity(TaskDTO dto) {
        Task entity = new Task();
        entity.title = dto.title;
        entity.description = dto.description;
        entity.status = dto.status;
        entity.priority = dto.priority;
        return entity;
    }

    public static void updateEntity(Task entity, TaskDTO dto) {
        entity.title = dto.title;
        entity.description = dto.description;
        entity.status = dto.status;
        entity.priority = dto.priority;
    }
}
