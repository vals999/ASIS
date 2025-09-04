
package controller;

import java.util.List;
import dao_interfaces.I_RespuestaEncuestaDAO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import model.RespuestaEncuesta;
import dto.PreguntaRespuestaCategoriaDTO;

@Path("/respuestas-encuesta")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Respuestas Encuestas", description = "Operaciones ABML para la gestión de respuestas encuestas")
public class RespuestaEncuestaController {
    @GET
    @Path("/preguntas-por-categoria")
    public Response obtenerPreguntasPorCategoria(@QueryParam("categoria") String categoria) {
        try {
            List<RespuestaEncuesta> respuestas = respuestaEncuestaDAO.obtenerTodos();
            var preguntas = respuestas.stream()
                .map(r -> r.getPregunta())
                .filter(p -> p != null)
                .filter(p -> categoria == null || (p.getCategoria() != null && p.getCategoria().name().equalsIgnoreCase(categoria)))
                .map(p -> p.getTexto())
                .distinct()
                .toList();
            return Response.ok(preguntas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
    @POST
    @Path("/filtrar-preguntas-respuestas")
    public Response filtrarPreguntasRespuestas(dto.Filtros filtros) {
        try {
            List<RespuestaEncuesta> respuestas = respuestaEncuestaDAO.obtenerTodos();
            // Si hay filtro de edad, obtener los encuesta_id que cumplen el rango
            final List<Long> encuestaIdsFiltrados;
            if (filtros.getEdadDesde() != null || filtros.getEdadHasta() != null) {
                encuestaIdsFiltrados = respuestas.stream()
                    .filter(r -> r.getPregunta() != null && r.getPregunta().getTexto().toLowerCase().contains("edad"))
                    .map(r -> {
                        try {
                            return new Object[]{r.getEncuesta().getId(), Integer.parseInt(r.getValor())};
                        } catch (Exception ex) {
                            return null;
                        }
                    })
                    .filter(obj -> obj != null)
                    .filter(obj -> {
                        Integer edad = (Integer) ((Object[]) obj)[1];
                        boolean desde = filtros.getEdadDesde() == null || edad >= filtros.getEdadDesde();
                        boolean hasta = filtros.getEdadHasta() == null || edad <= filtros.getEdadHasta();
                        return desde && hasta;
                    })
                    .map(obj -> (Long) ((Object[]) obj)[0])
                    .distinct()
                    .toList();
            } else {
                encuestaIdsFiltrados = null;
            }
            var lista = respuestas.stream()
                .filter(r -> r.getPregunta() != null)
                .filter(r -> filtros.getCategoria() == null || (r.getPregunta().getCategoria() != null && r.getPregunta().getCategoria().name().equalsIgnoreCase(filtros.getCategoria())))
                .filter(r -> filtros.getTipoRespuesta() == null || (r.getPregunta().getTipoRespuesta() != null && r.getPregunta().getTipoRespuesta().name().equalsIgnoreCase(filtros.getTipoRespuesta())))
                .filter(r -> filtros.getPregunta() == null || r.getPregunta().getTexto().equalsIgnoreCase(filtros.getPregunta()))
                .filter(r -> encuestaIdsFiltrados == null || encuestaIdsFiltrados.contains(r.getEncuesta().getId()))
                .map(r -> new PreguntaRespuestaCategoriaDTO(
                    r.getPregunta().getTexto(),
                    r.getValor(),
                    r.getPregunta().getCategoria() != null ? r.getPregunta().getCategoria().name() : null
                ))
                .toList();
            return Response.ok(lista).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @Inject
    private I_RespuestaEncuestaDAO respuestaEncuestaDAO;

    @GET
    @Path("/preguntas-respuestas-categoria")
    public Response obtenerPreguntasRespuestasPorCategoria(@QueryParam("categoria") String categoria) {
        try {
            List<RespuestaEncuesta> respuestas = respuestaEncuestaDAO.obtenerTodos();
            var lista = respuestas.stream()
                .filter(r -> r.getPregunta() != null)
                .filter(r -> categoria == null || (r.getPregunta().getCategoria() != null && r.getPregunta().getCategoria().name().equalsIgnoreCase(categoria)))
                .map(r -> new PreguntaRespuestaCategoriaDTO(
                    r.getPregunta().getTexto(),
                    r.getValor(),
                    r.getPregunta().getCategoria() != null ? r.getPregunta().getCategoria().name() : null
                ))
                .toList();
            return Response.ok(lista).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    public Response obtenerTodasLasRespuestas() {
        try {
            List<RespuestaEncuesta> respuestas = respuestaEncuestaDAO.obtenerTodos();
            return Response.ok(respuestas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activas")
    public Response obtenerRespuestasActivas() {
        try {
            List<RespuestaEncuesta> respuestas = respuestaEncuestaDAO.obtenerNoBorrados();
            return Response.ok(respuestas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtenerRespuestaPorId(@PathParam("id") Long id) {
        try {
            RespuestaEncuesta respuesta = respuestaEncuestaDAO.obtenerPorId(id);
            if (respuesta == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(respuesta).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    public Response crearRespuesta(RespuestaEncuesta respuesta) {
        try {
            respuestaEncuestaDAO.crear(respuesta);
            return Response.status(Status.CREATED).entity(respuesta).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizarRespuesta(@PathParam("id") Long id, RespuestaEncuesta respuesta) {
        try {
            respuesta.setId(id);
            respuestaEncuestaDAO.actualizar(respuesta);
            return Response.ok(respuesta).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarRespuesta(@PathParam("id") Long id) {
        try {
            respuestaEncuestaDAO.eliminar(id);
            return Response.ok().entity("Respuesta eliminada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    public Response recuperarRespuesta(@PathParam("id") Long id) {
        try {
            respuestaEncuestaDAO.recuperar(id);
            return Response.ok().entity("Respuesta recuperada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}