package config;

import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AppLifecycle {

    @ConfigProperty(name = "app.name", defaultValue = "TaskManager")
    String appName;

    @ConfigProperty(name = "app.version", defaultValue = "2.0")
    String appVersion;

    void onStart(@Observes StartupEvent ev) {
        Log.infof(" %s v%s démarré !", appName, appVersion);
        Log.infof("   Java : %s", System.getProperty("java.version"));
        Log.infof("   OS   : %s", System.getProperty("os.name"));
    }

    void onStop(@Observes ShutdownEvent ev) {
        Log.infof(" %s arrêté.", appName);
    }
}
