package repository;


import entity.Priority;
import entity.Status;
import entity.Task;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TaskRepository implements PanacheRepository<Task> {

    public List<Task> findByStatus(Status status) {
        return find("status", status).list();
    }

    public List<Task> findByPriority(Priority priority) {
        return find("priority", Sort.by("createdAt").descending(), priority).list();
    }

    public List<Task> search(String query) {
        return find("LOWER(title) LIKE ?1", "%" + query.toLowerCase() + "%").list();
    }

    public long countByStatus(Status status) {
        return count("status", status);
    }

}
