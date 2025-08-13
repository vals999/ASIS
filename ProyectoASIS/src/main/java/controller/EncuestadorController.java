// EncuestadorController.java
package controller;

import java.util.List;

import dao_interfaces.I_EncuestadorDAO;
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
import model.Encuestador;

@Path("/encuestadores")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Encuestador", description = "Operaciones ABML para la gestión de encuestador")
public class EncuestadorController {

    @Inject
    private I_EncuestadorDAO encuestadorDAO;

    @GET
    @Operation(summary = "Obtener todos los encuestadores", 
    description = "Retorna la lista completa de encuestadores en el sistema",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de encuestadores obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerTodosLosEncuestadores() {
        try {
            List<Encuestador> encuestadores = encuestadorDAO.obtenerTodos();
            return Response.ok(encuestadores).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activos")
    @Operation(summary = "Obtener encuestadores activos", 
    description = "Retorna la lista de encuestadores que no han sido eliminados lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de encuestadores activos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerEncuestadoresActivos() {
        try {
            List<Encuestador> encuestadores = encuestadorDAO.obtenerNoBorrados();
            return Response.ok(encuestadores).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener encuestador por ID", 
    description = "Retorna un encuestador específico basado en su ID",
    responses = {
        @ApiResponse(responseCode = "200", description = "Encuestador encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Encuestador no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerEncuestadorPorId(@PathParam("id") Long id) {
        try {
            Encuestador encuestador = encuestadorDAO.obtenerPorId(id);
            if (encuestador == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(encuestador).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    @Operation(summary = "Crear nuevo encuestador", 
    description = "Crea un nuevo encuestador en el sistema",
    requestBody = @RequestBody(
        content = @Content(
            examples = {
                @ExampleObject(
                    name = "Encuestador 1",
                    value = "{\"ocupacion\": \"Estudiante de Sociología\"}"
                ),
                @ExampleObject(
                    name = "Encuestador 2",
                    value = "{\"ocupacion\": \"Trabajador Social\"}"
                ),
                @ExampleObject(
                    name = "Encuestador 3",
                    value = "{\"ocupacion\": \"Licenciado en Estadística\"}"
                )
            }
        )
    ),
    responses = {
        @ApiResponse(responseCode = "201", description = "Encuestador creado exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    }
    )
    public Response crearEncuestador(Encuestador encuestador) {
        try {
            encuestadorDAO.crear(encuestador);
            return Response.status(Status.CREATED).entity(encuestador).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar encuestador", 
    description = "Actualiza los datos de un encuestador existente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Encuestador actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Encuestador no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response actualizarEncuestador(@PathParam("id") Long id, Encuestador encuestador) {
        try {
            encuestador.setId(id);
            encuestadorDAO.actualizar(encuestador);
            return Response.ok(encuestador).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar encuestador", 
    description = "Realiza una eliminación lógica del encuestador",
    responses = {
        @ApiResponse(responseCode = "200", description = "Encuestador eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Encuestador no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response eliminarEncuestador(@PathParam("id") Long id) {
        try {
            encuestadorDAO.eliminar(id);
            return Response.ok().entity("Encuestador eliminado correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    @Operation(summary = "Recuperar encuestador", 
    description = "Recupera un encuestador que fue eliminado lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Encuestador recuperado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Encuestador no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response recuperarEncuestador(@PathParam("id") Long id) {
        try {
            encuestadorDAO.recuperar(id);
            return Response.ok().entity("Encuestador recuperado correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}