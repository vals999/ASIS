// PersonaEncuestadaController.java
package controller;

import java.util.List;
import dao_interfaces.I_PersonaEncuestadaDAO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import model.PersonaEncuestada;

@Path("/personas-encuestadas")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Persona Encuestada", description = "Operaciones ABML para la gestión de personas encuestadas")
public class PersonaEncuestadaController {

    @Inject
    private I_PersonaEncuestadaDAO personaEncuestadaDAO;

    @GET
    public Response obtenerTodasLasPersonasEncuestadas() {
        try {
            List<PersonaEncuestada> personas = personaEncuestadaDAO.obtenerTodos();
            return Response.ok(personas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activas")
    public Response obtenerPersonasEncuestadasActivas() {
        try {
            List<PersonaEncuestada> personas = personaEncuestadaDAO.obtenerNoBorrados();
            return Response.ok(personas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtenerPersonaEncuestadaPorId(@PathParam("id") Long id) {
        try {
            PersonaEncuestada persona = personaEncuestadaDAO.obtenerPorId(id);
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
    public Response crearPersonaEncuestada(PersonaEncuestada persona) {
        try {
            personaEncuestadaDAO.crear(persona);
            return Response.status(Status.CREATED).entity(persona).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizarPersonaEncuestada(@PathParam("id") Long id, PersonaEncuestada persona) {
        try {
            persona.setId(id);
            personaEncuestadaDAO.actualizar(persona);
            return Response.ok(persona).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarPersonaEncuestada(@PathParam("id") Long id) {
        try {
            personaEncuestadaDAO.eliminar(id);
            return Response.ok().entity("Persona encuestada eliminada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    public Response recuperarPersonaEncuestada(@PathParam("id") Long id) {
        try {
            personaEncuestadaDAO.recuperar(id);
            return Response.ok().entity("Persona encuestada recuperada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}