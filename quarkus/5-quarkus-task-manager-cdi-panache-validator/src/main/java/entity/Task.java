package entity;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

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



}
