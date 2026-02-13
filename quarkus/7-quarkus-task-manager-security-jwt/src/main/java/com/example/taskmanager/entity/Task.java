package com.example.taskmanager.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task extends PanacheEntity {

    @Column(nullable = false)
    public String title;

    @Column(length = 500)
    public String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status status = Status.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Priority priority = Priority.MEDIUM;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
