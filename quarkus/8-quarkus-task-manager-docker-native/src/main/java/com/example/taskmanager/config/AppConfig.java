package com.example.taskmanager.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import java.util.Optional;

/**
 * ============================================================
 * VERSION 5 — @ConfigMapping (configuration typée)
 * ============================================================
 * Concepts introduits :
 *   ✅ @ConfigMapping     → interface de config typée (remplace @ConfigProperty)
 *   ✅ @WithDefault       → valeur par défaut
 *   ✅ Optional<>         → valeur optionnelle
 *   ✅ Nested interface   → config hiérarchique (app.pagination.default-size)
 *
 * Équivalent Spring : @ConfigurationProperties(prefix = "app")
 *
 * Mapping depuis application.properties :
 *   app.name          → name()
 *   app.version       → version()
 *   app.pagination.default-size → pagination().defaultSize()
 * ============================================================
 */
@ConfigMapping(prefix = "app")
public interface AppConfig {

    @WithDefault("TaskManager")
    String name();

    @WithDefault("5.0")
    String version();

    @WithDefault("Bienvenue sur TaskManager")
    String welcomeMessage();

    Optional<String> adminEmail();

    Pagination pagination();

    interface Pagination {
        @WithDefault("10")
        int defaultSize();

        @WithDefault("50")
        int maxSize();
    }
}
