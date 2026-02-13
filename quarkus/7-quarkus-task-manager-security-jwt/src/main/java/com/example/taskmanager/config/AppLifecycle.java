package com.example.taskmanager.config;

import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * — Utilise AppConfig (@ConfigMapping) au lieu de @ConfigProperty
 */
@ApplicationScoped
public class AppLifecycle {

    private final AppConfig config;

    public AppLifecycle(AppConfig config) {
        this.config = config;
    }

    void onStart(@Observes StartupEvent ev) {
        Log.infof("🚀 %s v%s démarré !", config.name(), config.version());
        config.description().ifPresent(d -> Log.infof("   📝 %s", d));
        Log.infof("   📄 Pagination : %d éléments/page (max %d)",
                config.pagination().defaultSize(),
                config.pagination().maxSize());
        Log.infof("   🔍 Recherche : %s", config.enableSearch() ? "activée" : "désactivée");
        Log.infof("   ☕ Java : %s", System.getProperty("java.version"));
    }

    void onStop(@Observes ShutdownEvent ev) {
        Log.infof("👋 %s arrêté.", config.name());
    }
}
