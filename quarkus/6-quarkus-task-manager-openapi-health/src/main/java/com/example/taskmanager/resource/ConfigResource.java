package com.example.taskmanager.resource;

import com.example.taskmanager.config.AppConfig;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.Map;


@Path("/api/config")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigResource {

    private final AppConfig appConfig;

    // @ConfigProperty pour des valeurs individuelles
    @ConfigProperty(name = "quarkus.http.port")
    int httpPort;

    @ConfigProperty(name = "quarkus.datasource.db-kind", defaultValue = "unknown")
    String dbKind;

    public ConfigResource(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @GET
    public Map<String, Object> getConfig() {
        return Map.of(
            "application", Map.of(
                "name", appConfig.name(),
                "version", appConfig.version(),
                "welcomeMessage", appConfig.welcomeMessage(),
                "adminEmail", appConfig.adminEmail().orElse("non configuré")
            ),
            "pagination", Map.of(
                "defaultSize", appConfig.pagination().defaultSize(),
                "maxSize", appConfig.pagination().maxSize()
            ),
            "infrastructure", Map.of(
                "httpPort", httpPort,
                "dbKind", dbKind,
                "javaVersion", System.getProperty("java.version"),
                "profile", org.eclipse.microprofile.config.ConfigProvider.getConfig()
                        .getOptionalValue("quarkus.profile", String.class).orElse("prod")
            )
        );
    }
}
