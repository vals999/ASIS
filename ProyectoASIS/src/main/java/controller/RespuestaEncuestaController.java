// RespuestaEncuestaController.java
package controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

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
            List<RespuestaEncuesta> respuestasCoordenas = respuestas.stream()
                .filter(r -> r.getPregunta() != null && 
                           (r.getPregunta().getId() == 1 || r.getPregunta().getId() == 2))
                .collect(Collectors.toList());
                
            System.out.println("Respuestas con coordenadas encontradas: " + respuestasCoordenas.size());
            
            // Separar latitudes y longitudes
            List<RespuestaEncuesta> latitudes = respuestasCoordenas.stream()
                .filter(r -> r.getPregunta().getId() == 1)
                .sorted((a, b) -> a.getId().compareTo(b.getId())) // Ordenar por ID
                .collect(Collectors.toList());
                
            List<RespuestaEncuesta> longitudes = respuestasCoordenas.stream()
                .filter(r -> r.getPregunta().getId() == 2)
                .sorted((a, b) -> a.getId().compareTo(b.getId())) // Ordenar por ID
                .collect(Collectors.toList());
            
            System.out.println("Latitudes encontradas: " + latitudes.size());
            System.out.println("Longitudes encontradas: " + longitudes.size());
            
            // Crear coordenadas válidas emparejando secuencialmente
            List<CoordenadaMapaDTO> coordenadasCompletas = new ArrayList<>();
            
            // Emparejar secuencialmente: latitud[i] con longitud[i]
            int minSize = Math.min(latitudes.size(), longitudes.size());
            
            for (int i = 0; i < minSize; i++) {
                try {
                    RespuestaEncuesta respuestaLatitud = latitudes.get(i);
                    RespuestaEncuesta respuestaLongitud = longitudes.get(i);
                    
                    String latitudStr = respuestaLatitud.getValor();
                    String longitudStr = respuestaLongitud.getValor();
                    
                    Double latitud = Double.parseDouble(latitudStr);
                    Double longitud = Double.parseDouble(longitudStr);
                    
                    // Validar rango de coordenadas
                    if (latitud >= -90 && latitud <= 90 && longitud >= -180 && longitud <= 180) {
                        CoordenadaMapaDTO coordenada = new CoordenadaMapaDTO(
                            (long) (i + 1), // ID único secuencial
                            respuestaLatitud.getId(), // ID de respuesta de latitud
                            latitud + "," + longitud,
                            1L, // ID de pregunta de latitud
                            "Ubicación " + (i + 1)
                        );
                        coordenadasCompletas.add(coordenada);
                        
                        System.out.println("Coordenada " + (i + 1) + " emparejada secuencialmente:");
                        System.out.println("  Latitud: " + latitud + " (Respuesta ID: " + respuestaLatitud.getId() + ")");
                        System.out.println("  Longitud: " + longitud + " (Respuesta ID: " + respuestaLongitud.getId() + ")");
                    } else {
                        System.out.println("Coordenada fuera de rango ignorada - Lat: " + latitud + ", Lng: " + longitud);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error al parsear coordenadas en índice " + i + ": " + e.getMessage());
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