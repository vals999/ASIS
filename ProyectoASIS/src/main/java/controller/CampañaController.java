package controller;

import java.util.List;

import dao_interfaces.I_CampañaDAO;
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
import model.Campaña;

@Path("/campanias")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Campañas", description = "Operaciones ABML para la gestión de campañas")
public class CampañaController {

    @Inject
    private I_CampañaDAO campañaDAO;

    @GET
    @Operation(summary = "Obtener todas las campañas", 
    description = "Retorna la lista completa de campañas en el sistema",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de campañas obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerTodasLasCampañas() {
        try {
            List<Campaña> campañas = campañaDAO.obtenerTodos();
            return Response.ok(campañas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activas")
    @Operation(summary = "Obtener campañas activas", 
    description = "Retorna la lista de campañas que no han sido eliminadas lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de campañas activas obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerCampañasActivas() {
        try {
            List<Campaña> campañas = campañaDAO.obtenerNoBorrados();
            return Response.ok(campañas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener campaña por ID", 
    description = "Retorna una campaña específica basada en su ID",
    responses = {
        @ApiResponse(responseCode = "200", description = "Campaña encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Campaña no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerCampañaPorId(@PathParam("id") Long id) {
        try {
            Campaña campaña = campañaDAO.obtenerPorId(id);
            if (campaña == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(campaña).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    @Operation(summary = "Crear nueva campaña", 
    description = "Crea una nueva campaña en el sistema",
    requestBody = @RequestBody(
        content = @Content(
            examples = {
                @ExampleObject(
                    name = "Campaña 1",
                    value = "{\"nombre\": \"Campaña de Salud 2024\", \"fechaInicio\": \"2025-03-01\", \"fechaFin\": \"2025-06-30\"}"
                ),
                @ExampleObject(
                    name = "Campaña 2",
                    value = "{\"nombre\": \"Campaña de Educación\", \"fechaInicio\": \"2025-04-01\", \"fechaFin\": \"2025-07-31\"}"
                ),
                @ExampleObject(
                    name = "Campaña 3",
                    value = "{\"nombre\": \"Campaña de Vivienda\", \"fechaInicio\": \"2025-05-01\", \"fechaFin\": \"2025-08-31\"}"
                )
            }
        )
    ),
    responses = {
        @ApiResponse(responseCode = "201", description = "Campaña creada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    }
    )
    public Response crearCampaña(Campaña campaña) {
        try {
            campañaDAO.crear(campaña);
            return Response.status(Status.CREATED).entity(campaña).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar campaña", 
    description = "Actualiza los datos de una campaña existente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Campaña actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Campaña no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response actualizarCampaña(@PathParam("id") Long id, Campaña campaña) {
        try {
            campaña.setId(id);
            campañaDAO.actualizar(campaña);
            return Response.ok(campaña).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar campaña", 
    description = "Realiza una eliminación lógica de la campaña",
    responses = {
        @ApiResponse(responseCode = "200", description = "Campaña eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Campaña no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response eliminarCampaña(@PathParam("id") Long id) {
        try {
            campañaDAO.eliminar(id);
            return Response.ok().entity("Campaña eliminada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    @Operation(summary = "Recuperar campaña", 
    description = "Recupera una campaña que fue eliminada lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Campaña recuperada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Campaña no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response recuperarCampaña(@PathParam("id") Long id) {
        try {
            campañaDAO.recuperar(id);
            return Response.ok().entity("Campaña recuperada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}
