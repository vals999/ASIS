// RespuestaEncuestaController.java
package controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dao_interfaces.I_RespuestaEncuestaDAO;
import dto.CoordenadaMapaDTO;
import dto.RespuestaEncuestaDTO;
import dto.DTOMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import model.RespuestaEncuesta;

@Path("/respuestas-encuesta")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Respuestas Encuestas", description = "Operaciones ABML para la gestión de respuestas encuestas")
public class RespuestaEncuestaController {

    @Inject
    private I_RespuestaEncuestaDAO respuestaEncuestaDAO;

    @GET
    public Response obtenerTodasLasRespuestas() {
        try {
            List<RespuestaEncuesta> respuestas = respuestaEncuestaDAO.obtenerTodos();
            List<RespuestaEncuestaDTO> respuestasDTO = respuestas.stream()
                .map(DTOMapper::toRespuestaEncuestaDTO)
                .collect(Collectors.toList());
            return Response.ok(respuestasDTO).build();
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
            List<RespuestaEncuestaDTO> respuestasDTO = respuestas.stream()
                .map(DTOMapper::toRespuestaEncuestaDTO)
                .collect(Collectors.toList());
            return Response.ok(respuestasDTO).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/coordenadas-mapa")
    public Response obtenerCoordenadasParaMapa() {
        try {
            List<RespuestaEncuesta> respuestas = respuestaEncuestaDAO.obtenerNoBorrados();
            
            System.out.println("Total respuestas encontradas: " + respuestas.size());
            
            // Filtrar solo las respuestas de preguntas 1 y 2 (latitud y longitud)
            // Sin depender de encuestas ya que están en NULL
            List<RespuestaEncuesta> respuestasCoordenas = respuestas.stream()
                .filter(r -> r.getPregunta() != null && 
                           (r.getPregunta().getId() == 1 || r.getPregunta().getId() == 2))
                .collect(Collectors.toList());
                
            System.out.println("Respuestas con coordenadas encontradas: " + respuestasCoordenas.size());
            
            // Como las encuestas están en NULL, vamos a agrupar por respuesta individual
            // y crear un marcador por cada par de coordenadas que encontremos
            Map<String, String> coordenadasValores = new HashMap<>();
            
            for (RespuestaEncuesta respuesta : respuestasCoordenas) {
                Long preguntaId = respuesta.getPregunta().getId();
                String valor = respuesta.getValor();
                
                System.out.println("Pregunta ID: " + preguntaId + ", Valor: " + valor);
                
                // Usar la respuesta ID como clave temporal para agrupar
                String clave = preguntaId == 1 ? "latitud_" + respuesta.getId() : "longitud_" + respuesta.getId();
                coordenadasValores.put(clave, valor);
            }
            
            // Crear coordenadas válidas
            List<CoordenadaMapaDTO> coordenadasCompletas = new ArrayList<>();
            
            // Buscar pares de latitud/longitud
            for (RespuestaEncuesta respuesta : respuestasCoordenas) {
                if (respuesta.getPregunta().getId() == 1) { // Si es latitud
                    String latitudStr = respuesta.getValor();
                    
                    // Buscar si hay una respuesta de longitud cerca (mismo rango de IDs)
                    for (RespuestaEncuesta respuesta2 : respuestasCoordenas) {
                        if (respuesta2.getPregunta().getId() == 2) { // Si es longitud
                            String longitudStr = respuesta2.getValor();
                            
                            try {
                                Double latitud = Double.parseDouble(latitudStr);
                                Double longitud = Double.parseDouble(longitudStr);
                                
                                // Validar rango de coordenadas
                                if (latitud >= -90 && latitud <= 90 && longitud >= -180 && longitud <= 180) {
                                    CoordenadaMapaDTO coordenada = new CoordenadaMapaDTO(
                                        (long) coordenadasCompletas.size() + 1, // ID único temporal
                                        respuesta.getId(),
                                        latitud + "," + longitud,
                                        respuesta.getPregunta().getId(),
                                        "Ubicación " + (coordenadasCompletas.size() + 1)
                                    );
                                    coordenadasCompletas.add(coordenada);
                                    
                                    System.out.println("Coordenada válida agregada - Lat: " + latitud + ", Lng: " + longitud);
                                    break; // Solo una vez por latitud
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Valor no numérico ignorado - Lat: " + latitudStr + ", Lng: " + longitudStr);
                            }
                        }
                    }
                }
            }
            
            System.out.println("Coordenadas completas generadas: " + coordenadasCompletas.size());
            return Response.ok(coordenadasCompletas).build();
            
        } catch (Exception e) {
            e.printStackTrace();
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