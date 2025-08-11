package config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "ABML Proyecto ASIS",
        version = "1.0.0",
        description = "Acá se podrán probar todas las operaciones ABML a traves de request y también se reflejarán en la base de datos"
    ),
    servers = {
        @Server(
            description = "Servidor local de desarrollo",
            url = "http://localhost:8080/ProyectoASIS/api"
        ),
    }
)
public class OpenAPI {

}