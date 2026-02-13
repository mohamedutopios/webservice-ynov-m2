package com.example.taskmanager.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================
 * VERSION 3 — Entité JPA avec Panache (Active Record)
 * ============================================================
 * Concepts introduits :
 *   ✅ PanacheEntity     → fournit id + méthodes CRUD automatiques
 *   ✅ Champs publics    → Quarkus génère les getters/setters au build
 *   ✅ find(), count()   → requêtes simplifiées
 *   ✅ persist()         → INSERT automatique
 *   ✅ @PreUpdate        → callback JPA
 *
 * Pas besoin de :
 *   ❌ @Id, @GeneratedValue → dans PanacheEntity
 *   ❌ Getters/Setters → générés au build-time
 *   ❌ Repository séparé → Active Record = tout dans l'entité
 *
 * Équivalent Spring :
 *   @Entity + JpaRepository<Task, Long> (séparés)
 * ============================================================
 */
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

    // === Requêtes personnalisées (Active Record) ===

    public static List<Task> findByStatus(Status status) {
        return find("status", status).list();
    }

    public static List<Task> findByPriority(Priority priority) {
        return find("priority", Sort.by("createdAt").descending(), priority).list();
    }

    public static List<Task> search(String query) {
        return find("LOWER(title) LIKE ?1", "%" + query.toLowerCase() + "%").list();
    }

    public static long countByStatus(Status status) {
        return count("status", status);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
