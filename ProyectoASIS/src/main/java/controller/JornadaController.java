// JornadaController.java
package controller;

import java.util.List;

import dao_interfaces.I_JornadaDAO;
import dto.JornadaDTO;
import dto.DTOMapper;
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
import model.Jornada;

@Path("/jornadas")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Jornadas", description = "Operaciones ABML para la gestión de jornadas")
public class JornadaController {

    @Inject
    private I_JornadaDAO jornadaDAO;

    @GET
    @Operation(summary = "Obtener todas las jornadas", 
    description = "Retorna la lista completa de jornadas en el sistema",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de jornadas obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerTodasLasJornadas() {
        try {
            List<Jornada> jornadas = jornadaDAO.obtenerTodos();
            List<JornadaDTO> jornadasDTO = DTOMapper.toJornadasDTOList(jornadas);
            return Response.ok(jornadasDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activas")
    @Operation(summary = "Obtener jornadas activas", 
    description = "Retorna la lista de jornadas que no han sido eliminadas lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de jornadas activas obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerJornadasActivas() {
        try {
            List<Jornada> jornadas = jornadaDAO.obtenerNoBorrados();
            List<JornadaDTO> jornadasDTO = DTOMapper.toJornadasDTOList(jornadas);
            return Response.ok(jornadasDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener jornada por ID", 
    description = "Retorna una jornada específica basada en su ID",
    responses = {
        @ApiResponse(responseCode = "200", description = "Jornada encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Jornada no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerJornadaPorId(@PathParam("id") Long id) {
        try {
            Jornada jornada = jornadaDAO.obtenerPorId(id);
            if (jornada == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            JornadaDTO jornadaDTO = DTOMapper.toJornadaDTO(jornada);
            return Response.ok(jornadaDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    @Operation(summary = "Crear nueva jornada", 
    description = "Crea una nueva jornada en el sistema",
    requestBody = @RequestBody(
        content = @Content(
            examples = {
                @ExampleObject(
                    name = "Jornada 1",
                    value = "{\"fecha\": \"2025-03-15\"}"
                ),
                @ExampleObject(
                    name = "Jornada 2",
                    value = "{\"fecha\": \"2025-03-16\"}"
                ),
                @ExampleObject(
                    name = "Jornada 3",
                    value = "{\"fecha\": \"2025-03-17\"}"
                )
            }
        )
    ),
    responses = {
        @ApiResponse(responseCode = "201", description = "Jornada creada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response crearJornada(Jornada jornada) {
        try {
            jornadaDAO.crear(jornada);
            return Response.status(Status.CREATED).entity(jornada).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar jornada", 
    description = "Actualiza los datos de una jornada existente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Jornada actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Jornada no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response actualizarJornada(@PathParam("id") Long id, Jornada jornada) {
        try {
            jornada.setId(id);
            jornadaDAO.actualizar(jornada);
            return Response.ok(jornada).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar jornada", 
    description = "Realiza una eliminación lógica de la jornada",
    responses = {
        @ApiResponse(responseCode = "200", description = "Jornada eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Jornada no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response eliminarJornada(@PathParam("id") Long id) {
        try {
            jornadaDAO.eliminar(id);
            return Response.ok().entity("Jornada eliminada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    @Operation(summary = "Recuperar jornada", 
    description = "Recupera una jornada que fue eliminada lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Jornada recuperada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Jornada no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response recuperarJornada(@PathParam("id") Long id) {
        try {
            jornadaDAO.recuperar(id);
            return Response.ok().entity("Jornada recuperada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}