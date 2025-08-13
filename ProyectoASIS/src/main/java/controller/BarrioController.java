// BarrioController.java
package controller;

import java.util.List;

import dao_interfaces.I_BarrioDAO;
import dto.BarrioDTO;
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
import model.Barrio;

@Path("/barrios")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Barrios", description = "Operaciones ABML para la gestión de barrios")
public class BarrioController {

    @Inject
    private I_BarrioDAO barrioDAO;

    @GET
    @Operation(summary = "Obtener todos los barrios", 
    description = "Retorna la lista completa de barrios en el sistema",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de barrios obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerTodosLosBarrios() {
        try {
            List<Barrio> barrios = barrioDAO.obtenerTodos();
            List<BarrioDTO> barriosDTO = DTOMapper.toBarriosDTOList(barrios);
            return Response.ok(barriosDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activos")
    @Operation(summary = "Obtener barrios activos", 
    description = "Retorna la lista de barrios que no han sido eliminados lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de barrios activos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerBarriosActivos() {
        try {
            List<Barrio> barrios = barrioDAO.obtenerNoBorrados();
            List<BarrioDTO> barriosDTO = DTOMapper.toBarriosDTOList(barrios);
            return Response.ok(barriosDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener barrio por ID", 
    description = "Retorna un barrio específico basado en su ID",
    responses = {
        @ApiResponse(responseCode = "200", description = "Barrio encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Barrio no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerBarrioPorId(@PathParam("id") Long id) {
        try {
            Barrio barrio = barrioDAO.obtenerPorId(id);
            if (barrio == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            BarrioDTO barrioDTO = DTOMapper.toBarrioDTO(barrio);
            return Response.ok(barrioDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    @Operation(summary = "Crear nuevo barrio", 
    description = "Crea un nuevo barrio en el sistema",
    requestBody = @RequestBody(
        content = @Content(
            examples = {
                @ExampleObject(
                    name = "Barrio 1",
                    value = "{\"nombre\": \"Barrio San Martín\", \"geolocalizacion\": \"-34.6037, -58.3816\"}"
                ),
                @ExampleObject(
                    name = "Barrio 2",
                    value = "{\"nombre\": \"Barrio Belgrano\", \"geolocalizacion\": \"-34.5627, -58.4584\"}"
                ),
                @ExampleObject(
                    name = "Barrio 3",
                    value = "{\"nombre\": \"Barrio Palermo\", \"geolocalizacion\": \"-34.5885, -58.4111\"}"
                )
            }
        )
    ),
    responses = {
        @ApiResponse(responseCode = "201", description = "Barrio creado exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    }
    )
    public Response crearBarrio(Barrio barrio) {
        try {
            barrioDAO.crear(barrio);
            BarrioDTO barrioDTO = DTOMapper.toBarrioDTO(barrio);
            return Response.status(Status.CREATED).entity(barrioDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar barrio", 
    description = "Actualiza los datos de un barrio existente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Barrio actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Barrio no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response actualizarBarrio(@PathParam("id") Long id, Barrio barrio) {
        try {
            barrio.setId(id);
            barrioDAO.actualizar(barrio);
            BarrioDTO barrioDTO = DTOMapper.toBarrioDTO(barrio);
            return Response.ok(barrioDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar barrio", 
    description = "Realiza una eliminación lógica del barrio",
    responses = {
        @ApiResponse(responseCode = "200", description = "Barrio eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Barrio no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response eliminarBarrio(@PathParam("id") Long id) {
        try {
            barrioDAO.eliminar(id);
            return Response.ok().entity("Barrio eliminado correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    @Operation(summary = "Recuperar barrio", 
    description = "Recupera un barrio que fue eliminado lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Barrio recuperado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Barrio no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response recuperarBarrio(@PathParam("id") Long id) {
        try {
            barrioDAO.recuperar(id);
            return Response.ok().entity("Barrio recuperado correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}