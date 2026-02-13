package com.example.taskmanager.config;

import java.util.Optional;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;


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
