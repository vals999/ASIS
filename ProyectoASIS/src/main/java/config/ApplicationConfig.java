package config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class ApplicationConfig extends ResourceConfig {
    public ApplicationConfig() {
        // Registrar el paquete donde están tus recursos REST
        packages("rest");
        // Registrar el soporte para multipart (carga de archivos)
        register(MultiPartFeature.class);
    }
}
