// EncuestaController.java
package controller;

import java.util.List;
import dao_interfaces.I_EncuestaDAO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import model.Encuesta;

@Path("/encuestas")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Encuestas", description = "Operaciones ABML para la gestión de encuestas")
public class EncuestaController {

    @Inject
    private I_EncuestaDAO encuestaDAO;

    @GET
    public Response obtenerTodasLasEncuestas() {
        try {
            List<Encuesta> encuestas = encuestaDAO.obtenerTodos();
            return Response.ok(encuestas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activas")
    public Response obtenerEncuestasActivas() {
        try {
            List<Encuesta> encuestas = encuestaDAO.obtenerNoBorrados();
            return Response.ok(encuestas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtenerEncuestaPorId(@PathParam("id") Long id) {
        try {
            Encuesta encuesta = encuestaDAO.obtenerPorId(id);
            if (encuesta == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(encuesta).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    public Response crearEncuesta(Encuesta encuesta) {
        try {
            encuestaDAO.crear(encuesta);
            return Response.status(Status.CREATED).entity(encuesta).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizarEncuesta(@PathParam("id") Long id, Encuesta encuesta) {
        try {
            encuesta.setId(id);
            encuestaDAO.actualizar(encuesta);
            return Response.ok(encuesta).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarEncuesta(@PathParam("id") Long id) {
        try {
            encuestaDAO.eliminar(id);
            return Response.ok().entity("Encuesta eliminada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    public Response recuperarEncuesta(@PathParam("id") Long id) {
        try {
            encuestaDAO.recuperar(id);
            return Response.ok().entity("Encuesta recuperada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}