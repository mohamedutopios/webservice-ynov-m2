package com.example.taskmanager.health;

import com.example.taskmanager.entity.Task;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;


@Readiness
@ApplicationScoped
public class TaskReadinessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse
                .named("Database connection");
        try {
            long count = Task.count();
            builder.withData("tasks.count", count).up();
        } catch (Exception e) {
            builder.withData("error", e.getMessage()).down();
        }
        return builder.build();
    }
}
