// ZonaController.java
package controller;

import java.util.List;

import dao_interfaces.I_ZonaDAO;
import dto.DTOMapper;
import dto.ZonaDTO;
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
import model.Zona;

@Path("/zonas")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Zonas", description = "Operaciones ABML para la gestión de zonas")
public class ZonaController {

    @Inject
    private I_ZonaDAO zonaDAO;

    @GET
    @Operation(summary = "Obtener todas las zonas", 
    description = "Retorna la lista completa de zonas en el sistema",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de zonas obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerTodasLasZonas() {
        try {
            List<Zona> zonas = zonaDAO.obtenerTodos();
            List<ZonaDTO> zonasDTO = DTOMapper.toZonasDTOList(zonas);
            return Response.ok(zonasDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activas")
    @Operation(summary = "Obtener zonas activas", 
    description = "Retorna la lista de zonas que no han sido eliminadas lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de zonas activas obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerZonasActivas() {
        try {
            List<Zona> zonas = zonaDAO.obtenerNoBorrados();
            List<ZonaDTO> zonasDTO = DTOMapper.toZonasDTOList(zonas);
            return Response.ok(zonasDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener zona por ID", 
    description = "Retorna una zona específica basada en su ID",
    responses = {
        @ApiResponse(responseCode = "200", description = "Zona encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Zona no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerZonaPorId(@PathParam("id") Long id) {
        try {
            Zona zona = zonaDAO.obtenerPorId(id);
            if (zona == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            ZonaDTO zonaDTO = DTOMapper.toZonaDTO(zona);
            return Response.ok(zonaDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    @Operation(summary = "Crear nueva zona", 
    description = "Crea una nueva zona en el sistema",
    requestBody = @RequestBody(
        content = @Content(
            examples = {
                @ExampleObject(
                    name = "Zona 1",
                    value = "{\"nombre\": \"Zona Norte\", \"geolocalizacion\": \"-34.6037, -58.3816\"}"
                ),
                @ExampleObject(
                    name = "Zona 2",
                    value = "{\"nombre\": \"Zona Sur\", \"geolocalizacion\": \"-34.5627, -58.4584\"}"
                ),
                @ExampleObject(
                    name = "Zona 3",
                    value = "{\"nombre\": \"Zona Este\", \"geolocalizacion\": \"-34.5885, -58.4111\"}"
                )
            }
        )
    ),
    responses = {
        @ApiResponse(responseCode = "201", description = "Zona creada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response crearZona(Zona zona) {
        try {
            zonaDAO.crear(zona);
            ZonaDTO zonaDTO = DTOMapper.toZonaDTO(zona);
            return Response.status(Status.CREATED).entity(zonaDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar zona", 
    description = "Actualiza los datos de una zona existente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Zona actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Zona no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response actualizarZona(@PathParam("id") Long id, Zona zona) {
        try {
            zona.setId(id);
            zonaDAO.actualizar(zona);
            ZonaDTO zonaDTO = DTOMapper.toZonaDTO(zona);
            return Response.ok(zonaDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar zona", 
    description = "Realiza una eliminación lógica de la zona",
    responses = {
        @ApiResponse(responseCode = "200", description = "Zona eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Zona no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response eliminarZona(@PathParam("id") Long id) {
        try {
            zonaDAO.eliminar(id);
            return Response.ok().entity("Zona eliminada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    @Operation(summary = "Recuperar zona", 
    description = "Recupera una zona que fue eliminada lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Zona recuperada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Zona no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response recuperarZona(@PathParam("id") Long id) {
        try {
            zonaDAO.recuperar(id);
            return Response.ok().entity("Zona recuperada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}