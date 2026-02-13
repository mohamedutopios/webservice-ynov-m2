package com.example.taskmanager.health;

import com.example.taskmanager.entity.Task;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

/**
 * ============================================================
 * VERSION 7 — Readiness Check (MicroProfile Health)
 * ============================================================
 * @Readiness → "Est-ce que l'application est prête à recevoir du trafic ?"
 *   → Si DOWN, Kubernetes RETIRE le pod du load balancer
 *   → URL : GET /q/health/ready
 *
 * Ce check vérifie que la base de données est accessible
 * en exécutant un simple count().
 * ============================================================
 */
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
