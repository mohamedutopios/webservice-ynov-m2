package com.example.taskmanager.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

/**
 * ============================================================
 * VERSION 7 — Liveness Check (MicroProfile Health)
 * ============================================================
 * @Liveness → "Est-ce que l'application est vivante ?"
 *   → Si DOWN, Kubernetes RESTART le pod
 *   → URL : GET /q/health/live
 *
 * Équivalent Spring : @Component implements HealthIndicator
 * ============================================================
 */
@Liveness
@ApplicationScoped
public class TaskLivenessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("TaskManager Liveness")
                .withData("jvm.uptime", Runtime.getRuntime().totalMemory() / (1024 * 1024) + " MB")
                .up()
                .build();
    }
}
