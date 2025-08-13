// DatosPersonalesController.java
package controller;

import java.util.List;

import dao_interfaces.I_PersonaDAO;
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
import model.DatosPersonales;

@Path("/personas")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Datos Personales", description = "Operaciones ABML para la gestión de datos personales")
public class DatosPersonalesController {

    @Inject
    private I_PersonaDAO personaDAO;

    @GET
    @Operation(summary = "Obtener todos los datos personales", 
    description = "Retorna la lista completa de datos personales en el sistema",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de datos personales obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerTodasLasPersonas() {
        try {
            List<DatosPersonales> personas = personaDAO.obtenerTodos();
            return Response.ok(personas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activas")
    @Operation(summary = "Obtener datos personales activos", 
    description = "Retorna la lista de datos personales que no han sido eliminados lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de datos personales activos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerPersonasActivas() {
        try {
            List<DatosPersonales> personas = personaDAO.obtenerNoBorrados();
            return Response.ok(personas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener datos personales por ID", 
    description = "Retorna los datos personales específicos basados en su ID",
    responses = {
        @ApiResponse(responseCode = "200", description = "Datos personales encontrados exitosamente"),
        @ApiResponse(responseCode = "404", description = "Datos personales no encontrados"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerPersonaPorId(@PathParam("id") Long id) {
        try {
            DatosPersonales persona = personaDAO.obtenerPorId(id);
            if (persona == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(persona).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    @Operation(summary = "Crear nuevos datos personales", 
    description = "Crea un nuevo registro de datos personales en el sistema",
    requestBody = @RequestBody(
        content = @Content(
            examples = {
                @ExampleObject(
                    name = "Persona 1",
                    value = "{\"nombre\": \"Juan\", \"apellido\": \"Pérez\", \"dni\": \"12345678\", \"edad\": 25, \"genero\": \"Masculino\"}"
                ),
                @ExampleObject(
                    name = "Persona 2",
                    value = "{\"nombre\": \"María\", \"apellido\": \"González\", \"dni\": \"87654321\", \"edad\": 30, \"genero\": \"Femenino\"}"
                ),
                @ExampleObject(
                    name = "Persona 3",
                    value = "{\"nombre\": \"Carlos\", \"apellido\": \"Rodríguez\", \"dni\": \"45678912\", \"edad\": 28, \"genero\": \"Masculino\"}"
                )
            }
        )
    ),
    responses = {
        @ApiResponse(responseCode = "201", description = "Datos personales creados exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response crearPersona(DatosPersonales persona) {
        try {
            personaDAO.crear(persona);
            return Response.status(Status.CREATED).entity(persona).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar datos personales", 
    description = "Actualiza los datos personales de un registro existente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Datos personales actualizados exitosamente"),
        @ApiResponse(responseCode = "404", description = "Datos personales no encontrados"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response actualizarPersona(@PathParam("id") Long id, DatosPersonales persona) {
        try {
            persona.setId(id);
            personaDAO.actualizar(persona);
            return Response.ok(persona).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar datos personales", 
    description = "Realiza una eliminación lógica de los datos personales",
    responses = {
        @ApiResponse(responseCode = "200", description = "Datos personales eliminados exitosamente"),
        @ApiResponse(responseCode = "404", description = "Datos personales no encontrados"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response eliminarPersona(@PathParam("id") Long id) {
        try {
            personaDAO.eliminar(id);
            return Response.ok().entity("Persona eliminada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    @Operation(summary = "Recuperar datos personales", 
    description = "Recupera los datos personales que fueron eliminados lógicamente",
    responses = {
        @ApiResponse(responseCode = "200", description = "Datos personales recuperados exitosamente"),
        @ApiResponse(responseCode = "404", description = "Datos personales no encontrados"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response recuperarPersona(@PathParam("id") Long id) {
        try {
            personaDAO.recuperar(id);
            return Response.ok().entity("Persona recuperada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}