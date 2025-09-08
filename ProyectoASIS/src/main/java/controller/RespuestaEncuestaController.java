
package controller;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
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
                encuestaIdsFiltrados = new ArrayList<>();
                Set<Long> encuestasYaProcesadas = new HashSet<>(); // Para evitar duplicados
                
                // Cada encuesta_id representa UNA PERSONA, buscar su edad
                for (RespuestaEncuesta r : respuestas) {
                    if (r.getPregunta() != null && "Edad".equals(r.getPregunta().getTexto())) {
                        Long encuestaId = r.getEncuesta().getId();
                        
                        // Si ya procesamos esta encuesta, ignorar edades duplicadas
                        if (encuestasYaProcesadas.contains(encuestaId)) {
                            continue;
                        }
                        
                        encuestasYaProcesadas.add(encuestaId);
                        
                        try {
                            Integer edad = Integer.parseInt(r.getValor());
                            boolean desde = filtros.getEdadDesde() == null || edad >= filtros.getEdadDesde();
                            boolean hasta = filtros.getEdadHasta() == null || edad <= filtros.getEdadHasta();
                            boolean cumple = desde && hasta;
                            
                            if (cumple) {
                                encuestaIdsFiltrados.add(encuestaId);
                            }
                        } catch (NumberFormatException ex) {
                            // Ignorar valores de edad no numéricos
                        }
                    }
                }
            } else {
                encuestaIdsFiltrados = null;
            }
            var lista = respuestas.stream()
                .filter(r -> r.getPregunta() != null)
                .filter(r -> filtros.getCategoria() == null || (r.getPregunta().getCategoria() != null && r.getPregunta().getCategoria().name().equalsIgnoreCase(filtros.getCategoria())))
                .filter(r -> filtros.getTipoRespuesta() == null || (r.getPregunta().getTipoRespuesta() != null && r.getPregunta().getTipoRespuesta().name().equalsIgnoreCase(filtros.getTipoRespuesta())))
                .filter(r -> filtros.getPregunta() == null || r.getPregunta().getTexto().equalsIgnoreCase(filtros.getPregunta()))
                .filter(r -> {
                    // Si hay filtro de edad, solo incluir respuestas de personas (encuesta_ids) válidas
                    if (encuestaIdsFiltrados != null) {
                        return encuestaIdsFiltrados.contains(r.getEncuesta().getId());
                    }
                    return true; // Si no hay filtro de edad, incluir todas
                })
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
            
            // Filtrar solo las respuestas de preguntas 1 y 2 (latitud y longitud)
            List<RespuestaEncuesta> respuestasCoordenas = respuestas.stream()
                .filter(r -> r.getPregunta() != null && 
                           (r.getPregunta().getId() == 1 || r.getPregunta().getId() == 2))
                .collect(Collectors.toList());
            
            // Separar latitudes y longitudes
            List<RespuestaEncuesta> latitudes = respuestasCoordenas.stream()
                .filter(r -> r.getPregunta().getId() == 1)
                .sorted((a, b) -> a.getId().compareTo(b.getId())) // Ordenar por ID
                .collect(Collectors.toList());
                
            List<RespuestaEncuesta> longitudes = respuestasCoordenas.stream()
                .filter(r -> r.getPregunta().getId() == 2)
                .sorted((a, b) -> a.getId().compareTo(b.getId())) // Ordenar por ID
                .collect(Collectors.toList());
            
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
                    }
                } catch (NumberFormatException e) {
                    // Ignorar coordenadas con formato inválido
                }
            }
            
            return Response.ok(coordenadasCompletas).build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/coordenadas-filtradas")
    public Response obtenerCoordenadasFiltradas(@QueryParam("preguntaCodigo") String preguntaCodigo, 
                                               @QueryParam("respuestaValor") String respuestaValor) {
        try {
            // Obtener respuestas que coincidan con la pregunta y valor específicos
            List<RespuestaEncuesta> respuestasFiltradas = respuestaEncuestaDAO.obtenerRespuestasPorPreguntaYValor(preguntaCodigo, respuestaValor);
            
            // Obtener todas las respuestas de latitud y longitud
            List<RespuestaEncuesta> todasLatitudes = respuestaEncuestaDAO.obtenerRespuestasPorPreguntaCodigo("lat_1_Presione_actualiza");
            List<RespuestaEncuesta> todasLongitudes = respuestaEncuestaDAO.obtenerRespuestasPorPreguntaCodigo("long_1_Presione_actualiza");
            
            List<CoordenadaMapaDTO> coordenadasFiltradas = new ArrayList<>();
            
            // Crear un índice simple basado en las respuestas filtradas
            // Asumiendo que las respuestas están en el mismo orden que las coordenadas
            for (int i = 0; i < respuestasFiltradas.size() && i < todasLatitudes.size() && i < todasLongitudes.size(); i++) {
                try {
                    RespuestaEncuesta latitud = todasLatitudes.get(i);
                    RespuestaEncuesta longitud = todasLongitudes.get(i);
                    
                    Double lat = Double.parseDouble(latitud.getValor());
                    Double lng = Double.parseDouble(longitud.getValor());
                    
                    if (lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
                        CoordenadaMapaDTO coordenada = new CoordenadaMapaDTO(
                            (long)(i + 1), // ID secuencial
                            latitud.getId(),
                            lat + "," + lng,
                            1L,
                            "Filtro: " + respuestaValor
                        );
                        coordenadasFiltradas.add(coordenada);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error al parsear coordenadas en índice " + i);
                }
            }
            
            System.out.println("Coordenadas válidas encontradas: " + coordenadasFiltradas.size());
            return Response.ok(coordenadasFiltradas).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/existe-datos")
    public Response verificarSiExistenDatos() {
        try {
            List<RespuestaEncuesta> respuestas = respuestaEncuestaDAO.obtenerNoBorrados();
            boolean existenDatos = !respuestas.isEmpty();
            
            return Response.ok()
                .entity("{\"existenDatos\": " + existenDatos + ", \"totalRespuestas\": " + respuestas.size() + "}")
                .build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/respuestas-unicas/{preguntaCodigo}")
    public Response obtenerRespuestasUnicasPorPregunta(@PathParam("preguntaCodigo") String preguntaCodigo) {
        try {
            List<String> respuestasUnicas = respuestaEncuestaDAO.obtenerRespuestasUnicasPorPreguntaCodigo(preguntaCodigo);
            return Response.ok(respuestasUnicas).build();
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