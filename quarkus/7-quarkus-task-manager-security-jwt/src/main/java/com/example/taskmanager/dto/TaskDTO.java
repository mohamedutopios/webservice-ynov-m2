package com.example.taskmanager.dto;

import java.time.LocalDateTime;

import com.example.taskmanager.entity.Priority;
import com.example.taskmanager.entity.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class TaskDTO {

    public Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 100, message = "Le titre doit faire entre 3 et 100 caractères")
    public String title;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    public String description;

    @NotNull(message = "Le statut est obligatoire")
    public Status status;

    @NotNull(message = "La priorité est obligatoire")
    public Priority priority;

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
