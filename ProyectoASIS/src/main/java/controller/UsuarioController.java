package controller;

import java.util.List;

import dao_interfaces.I_UsuarioDAO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import model.Usuario;

@Path("/usuarios")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Usuarios", description = "Operaciones ABML para la gestión de usuarios")
public class UsuarioController {

    @Inject
    private I_UsuarioDAO usuarioDAO;

    @GET    
    @Operation(summary = "Obtener todos los usuarios", 
    description = "Retorna la lista completa de usuarios en el sistema",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerTodosLosUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();
            return Response.ok(usuarios).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activos")
    @Operation(summary = "Obtener usuarios activas", 
    description = "Retorna la lista de usuarios que no han sido eliminados lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios activos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerUsuariosActivos() {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerNoBorrados();
            return Response.ok(usuarios).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener usuarios por ID", 
    description = "Retorna un usuario específico basado en su ID",
    responses = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerUsuarioPorId(@PathParam("id") Long id) {
        try {
            Usuario usuario = usuarioDAO.obtenerPorId(id);
            if (usuario == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(usuario).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    @Operation(summary = "Crear nuevo usuario", 
    description = "Crea un nuevo usuario en el sistema",
    requestBody = @RequestBody(
        content = @Content(
            examples = {
                @ExampleObject(
                    name = "Usuario 1",
                    value = "{\"nombreUsuario\": \"nombreusuario1\", \"email\": \"usuario1@example.com\", \"contrasena\": \"contrasena1\", \"perfil\": \"ADMINISTRADOR\", \"habilitado\": true}"
                ),
                @ExampleObject(
                        name = "Usuario 2",
                        value = "{\"nombreUsuario\": \"nombreusuario2\", \"email\": \"usuario2@example.com\", \"contrasena\": \"contrasena12\", \"perfil\": \"PERSONAL_SALUD\", \"habilitado\": true}"
                    ),
                @ExampleObject(
                        name = "Usuario 3",
                        value = "{\"nombreUsuario\": \"nombreusuario3\", \"email\": \"usuario3@example.com\", \"contrasena\": \"contrasena123\", \"perfil\": \"REFERENTE_ORG_SOCIAL\", \"habilitado\": false}"
                    )
            }
        )
    ), 
    responses = {
    	@ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
    	@ApiResponse(responseCode = "500", description = "Error interno del servidor")
    }
    )
    public Response crearUsuario(Usuario usuario) {
        try {
            usuarioDAO.crear(usuario);
            return Response.status(Status.CREATED).entity(usuario).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar usuario", 
    description = "Actualiza los datos de un usuario existente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response actualizarUsuario(@PathParam("id") Long id, Usuario usuario) {
        try {
            usuario.setId(id);
            usuarioDAO.actualizar(usuario);
            return Response.ok(usuario).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar usuario", 
    description = "Realiza una eliminación lógica del usuario",
    responses = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response eliminarUsuario(@PathParam("id") Long id) {
        try {
            usuarioDAO.eliminar(id);
            return Response.ok().entity("Usuario eliminado correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    @Operation(summary = "Recuperar usuario", 
    description = "Recupera un usuario que fue eliminado lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Usuario recuperado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response recuperarUsuario(@PathParam("id") Long id) {
        try {
            usuarioDAO.recuperar(id);
            return Response.ok().entity("Usuario recuperado correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/pendientes")
    @Operation(summary = "Obtener usuarios pendientes", 
    description = "Retorna la lista de usuarios no habilitados",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios pendientes obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerUsuariosPendientes() {
        try {
            List<Usuario> usuariosPendientes = usuarioDAO.obtenerUsuariosPendientes();
            return Response.ok(usuariosPendientes).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/habilitar")
    @Operation(summary = "Habilitar usuario", 
    description = "Habilita un usuario para que pueda acceder al sistema",
    responses = {
        @ApiResponse(responseCode = "200", description = "Usuario habilitado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Usuario ya está habilitado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response habilitarUsuario(@PathParam("id") Long id) {
        try {
            Usuario usuario = usuarioDAO.obtenerPorId(id);
            if (usuario == null) {
                return Response.status(Status.NOT_FOUND)
                    .entity("Usuario no encontrado").build();
            }

            if (usuario.isHabilitado()) {
                return Response.status(Status.BAD_REQUEST)
                    .entity("El usuario ya está habilitado").build();
            }

            usuario.setHabilitado(true);
            usuarioDAO.actualizar(usuario);

            return Response.ok()
                .entity("Usuario habilitado exitosamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}