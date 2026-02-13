package com.example.taskmanager.resource;

import java.util.Map;

import com.example.taskmanager.config.AppConfig;

import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;


@Path("/api/config")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("admin")
public class ConfigResource {

    private final AppConfig config;

    public ConfigResource(AppConfig config) {
        this.config = config;
    }

    @GET
    public Map<String, Object> getActiveConfig() {
        return Map.of(
                "appName", config.name(),
                "appVersion", config.version(),
                "description", config.description().orElse("(non définie)"),
                "searchEnabled", config.enableSearch(),
                "pagination", Map.of(
                        "defaultSize", config.pagination().defaultSize(),
                        "maxSize", config.pagination().maxSize()
                ),
                "activeProfiles", ConfigUtils.getProfiles(),
                "javaVersion", System.getProperty("java.version")
        );
    }
}
