package dto;

import entity.Priority;
import entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TaskDTO {

    public Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 100, message = "Le titre doit contenir entre 3 et 100 caractères")
    public String title;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    public String description;

    @NotNull(message = "Le statut est obligatoire")
    public Status status;

    @NotNull(message = "La priorité est obligatoire")
    public Priority priority;

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

}
