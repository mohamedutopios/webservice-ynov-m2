package com.example.taskmanager.config;

import java.util.Optional;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;


@ConfigMapping(prefix = "app")
public interface AppConfig {

    // Lit : app.name
    @WithDefault("TaskManager")
    String name();

    // Lit : app.version
    @WithDefault("5.0")
    String version();

    // Lit : app.description (optionnel — pas d'erreur si absent)
    Optional<String> description();

    // Lit : app.features.enable-search (true/false)
    @WithDefault("true")
    boolean enableSearch();

    // Sous-section : app.pagination.*
    Pagination pagination();

    // ✅ Interface imbriquée — mappe app.pagination.*
    interface Pagination {

        // Lit : app.pagination.default-size
        @WithDefault("10")
        int defaultSize();

        // Lit : app.pagination.max-size
        @WithDefault("100")
        int maxSize();
    }
}
