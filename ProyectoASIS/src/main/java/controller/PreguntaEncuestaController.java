// PreguntaEncuestaController.java
package controller;

import java.util.List;
import dao_interfaces.I_PreguntaEncuestaDAO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import model.PreguntaEncuesta;

@Path("/preguntas-encuesta")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Pregunta Encuesta", description = "Operaciones ABML para la gestión de preguntas encuestas")
public class PreguntaEncuestaController {

    @Inject
    private I_PreguntaEncuestaDAO preguntaEncuestaDAO;

    @GET
    public Response obtenerTodasLasPreguntas() {
        try {
            List<PreguntaEncuesta> preguntas = preguntaEncuestaDAO.obtenerTodos();
            return Response.ok(preguntas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activas")
    public Response obtenerPreguntasActivas() {
        try {
            List<PreguntaEncuesta> preguntas = preguntaEncuestaDAO.obtenerNoBorrados();
            return Response.ok(preguntas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtenerPreguntaPorId(@PathParam("id") Long id) {
        try {
            PreguntaEncuesta pregunta = preguntaEncuestaDAO.obtenerPorId(id);
            if (pregunta == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(pregunta).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    public Response crearPregunta(PreguntaEncuesta pregunta) {
        try {
            preguntaEncuestaDAO.crear(pregunta);
            return Response.status(Status.CREATED).entity(pregunta).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizarPregunta(@PathParam("id") Long id, PreguntaEncuesta pregunta) {
        try {
            pregunta.setId(id);
            preguntaEncuestaDAO.actualizar(pregunta);
            return Response.ok(pregunta).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarPregunta(@PathParam("id") Long id) {
        try {
            preguntaEncuestaDAO.eliminar(id);
            return Response.ok().entity("Pregunta eliminada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    public Response recuperarPregunta(@PathParam("id") Long id) {
        try {
            preguntaEncuestaDAO.recuperar(id);
            return Response.ok().entity("Pregunta recuperada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}