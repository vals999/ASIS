package controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import dao_interfaces.I_ReporteDAO;
import dao_interfaces.I_UsuarioDAO;
import dto.DTOMapper;
import dto.ReporteDTO;
import dto.UsuarioSimpleDTO;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import model.Reporte;
import model.TipoVisibilidad;
import model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@RequestScoped
@Path("/reportes")
@Tag(name = "Reportes", description = "Operaciones relacionadas con reportes")
public class ReporteController {

    @Inject
    private I_ReporteDAO reporteDAO;
    
    @Inject
    private I_UsuarioDAO usuarioDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtener todos los reportes", 
               description = "Devuelve una lista de todos los reportes disponibles para usuarios autenticados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reportes obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ReporteDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerReportes() {
        try {
            List<Reporte> reportes = reporteDAO.obtenerNoBorrados();
            List<ReporteDTO> reportesDTO = DTOMapper.toReportesDTOList(reportes);
            return Response.ok(reportesDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"error\": \"Error al obtener reportes: " + e.getMessage() + "\"}")
                          .build();
        }
    }

    @GET
    @Path("/publicos")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtener reportes públicos", 
               description = "Devuelve una lista de reportes públicos accesibles desde la landing page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reportes públicos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ReporteDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerReportesPublicos() {
        try {
            List<Reporte> reportes = reporteDAO.obtenerReportesPublicos();
            List<ReporteDTO> reportesDTO = DTOMapper.toReportesDTOList(reportes);
            return Response.ok(reportesDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"error\": \"Error al obtener reportes públicos: " + e.getMessage() + "\"}")
                          .build();
        }
    }

    @GET
    @Path("/privados")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtener reportes privados", 
               description = "Devuelve una lista de reportes privados solo para usuarios autenticados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reportes privados obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ReporteDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerReportesPrivados() {
        try {
            List<Reporte> reportes = reporteDAO.obtenerReportesPrivados();
            List<ReporteDTO> reportesDTO = DTOMapper.toReportesDTOList(reportes);
            return Response.ok(reportesDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"error\": \"Error al obtener reportes privados: " + e.getMessage() + "\"}")
                          .build();
        }
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Subir un archivo de reporte", 
               description = "Sube un archivo y lo almacena como reporte en la base de datos. Solo usuarios de PERSONAL_SALUD pueden subir archivos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Archivo subido exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ReporteDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "No tienes permisos para subir archivos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response subirArchivo(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDisposition,
            @FormDataParam("creadorId") Long creadorId,
            @FormDataParam("visibilidad") String visibilidad) {
        try {
            // Validar datos de entrada
            if (fileInputStream == null || fileDisposition == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                              .entity("{\"error\": \"No se encontró ningún archivo\"}")
                              .build();
            }

            if (creadorId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                              .entity("{\"error\": \"ID del creador requerido\"}")
                              .build();
            }

            // Buscar el usuario creador
            Usuario usuario = usuarioDAO.obtenerPorId(creadorId);
            if (usuario == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                              .entity("{\"error\": \"Usuario no encontrado\"}")
                              .build();
            }

            // Verificar que el usuario tenga perfil de PERSONAL_SALUD para poder subir archivos
            if (usuario.getPerfil() != model.Perfil.PERSONAL_SALUD && usuario.getPerfil() != model.Perfil.ADMINISTRADOR) {
                return Response.status(Response.Status.FORBIDDEN)
                              .entity("{\"error\": \"Solo los usuarios de Personal de Salud y Administradores pueden subir archivos de reportes\"}")
                              .build();
            }

            String fileName = fileDisposition.getFileName();
            String contentType = fileDisposition.getType();
            
            // Leer el contenido del archivo
            byte[] fileContent = fileInputStream.readAllBytes();
            
            if (fileContent.length == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                              .entity("{\"error\": \"El archivo está vacío\"}")
                              .build();
            }

            // Determinar la visibilidad (por defecto PRIVADO)
            TipoVisibilidad tipoVisibilidad = TipoVisibilidad.PRIVADO;
            if (visibilidad != null) {
                try {
                    tipoVisibilidad = TipoVisibilidad.valueOf(visibilidad.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                                  .entity("{\"error\": \"Visibilidad inválida. Use PUBLICO o PRIVADO\"}")
                                  .build();
                }
            }

            // Crear el reporte
            Reporte reporte = new Reporte();
            reporte.setNombre(fileName);
            reporte.setFecha(new Date());
            reporte.setCreador(usuario);
            reporte.setVisibilidad(tipoVisibilidad);
            reporte.setContenidoArchivo(fileContent);
            reporte.setTipoMime(contentType != null ? contentType : "application/octet-stream");
            reporte.setTamanoArchivo((long) fileContent.length);
            reporte.setNombreArchivoOriginal(fileName);

            reporteDAO.crear(reporte);
            ReporteDTO reporteDTO = DTOMapper.toReporteDTO(reporte);

            return Response.status(Response.Status.CREATED).entity(reporteDTO).build();

        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"error\": \"Error al procesar el archivo: " + e.getMessage() + "\"}")
                          .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"error\": \"Error al subir archivo: " + e.getMessage() + "\"}")
                          .build();
        }
    }

    @PUT
    @Path("/{id}/visibilidad")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Cambiar visibilidad de un reporte", 
               description = "Cambia la visibilidad de un reporte entre PUBLICO y PRIVADO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Visibilidad actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "No tienes permisos para cambiar la visibilidad"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response cambiarVisibilidad(@PathParam("id") Long reporteId, 
                                     @QueryParam("visibilidad") String visibilidad,
                                     @QueryParam("usuarioId") Long usuarioId) {
        try {
            if (visibilidad == null || usuarioId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                              .entity("{\"error\": \"Visibilidad y usuarioId son requeridos\"}")
                              .build();
            }

            Reporte reporte = reporteDAO.obtenerPorId(reporteId);
            if (reporte == null) {
                return Response.status(Response.Status.NOT_FOUND)
                              .entity("{\"error\": \"Reporte no encontrado\"}")
                              .build();
            }

            // Verificar permisos
            Usuario usuario = usuarioDAO.obtenerPorId(usuarioId);
            if (usuario == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                              .entity("{\"error\": \"Usuario no encontrado\"}")
                              .build();
            }

            boolean esCreador = reporte.getCreador() != null && 
                               reporte.getCreador().getId().equals(usuarioId);
            boolean esAdmin = usuario.getPerfil() == model.Perfil.ADMINISTRADOR;

            if (!esCreador && !esAdmin) {
                return Response.status(Response.Status.FORBIDDEN)
                              .entity("{\"error\": \"Solo el creador o un administrador pueden cambiar la visibilidad\"}")
                              .build();
            }

            // Validar y establecer la nueva visibilidad
            try {
                TipoVisibilidad nuevaVisibilidad = TipoVisibilidad.valueOf(visibilidad.toUpperCase());
                reporte.setVisibilidad(nuevaVisibilidad);
                reporteDAO.actualizar(reporte);
                
                ReporteDTO reporteDTO = DTOMapper.toReporteDTO(reporte);
                return Response.ok(reporteDTO).build();
                
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                              .entity("{\"error\": \"Visibilidad inválida. Use PUBLICO o PRIVADO\"}")
                              .build();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"error\": \"Error al cambiar visibilidad: " + e.getMessage() + "\"}")
                          .build();
        }
    }

    @GET
    @Path("/{id}/download")
    @Operation(summary = "Descargar un archivo de reporte", 
               description = "Descarga el archivo asociado a un reporte específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Archivo descargado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response descargarArchivo(@PathParam("id") Long id) {
        try {
            Reporte reporte = reporteDAO.obtenerPorId(id);
            
            if (reporte == null || reporte.getContenidoArchivo() == null) {
                return Response.status(Response.Status.NOT_FOUND)
                              .entity("{\"error\": \"Reporte o archivo no encontrado\"}")
                              .build();
            }

            return Response.ok(reporte.getContenidoArchivo())
                          .header("Content-Disposition", 
                                 "attachment; filename=\"" + reporte.getNombreArchivoOriginal() + "\"")
                          .header("Content-Type", reporte.getTipoMime())
                          .header("Content-Length", reporte.getTamanoArchivo())
                          .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"error\": \"Error al descargar archivo: " + e.getMessage() + "\"}")
                          .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Eliminar un reporte", 
               description = "Elimina un reporte específico. Solo el creador o un administrador pueden eliminar reportes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte eliminado exitosamente"),
        @ApiResponse(responseCode = "403", description = "No tienes permisos para eliminar este reporte"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response eliminarReporte(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        try {
            Reporte reporte = reporteDAO.obtenerPorId(id);
            
            if (reporte == null) {
                return Response.status(Response.Status.NOT_FOUND)
                              .entity("{\"error\": \"Reporte no encontrado\"}")
                              .build();
            }

            // Obtener información del usuario actual desde el contexto de seguridad
            // Por ahora usaremos un parámetro adicional para el ID del usuario
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity("{\"error\": \"Debe proporcionar el ID del usuario actual\"}")
                          .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"error\": \"Error al eliminar reporte: " + e.getMessage() + "\"}")
                          .build();
        }
    }

    @DELETE
    @Path("/{id}/usuario/{usuarioId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Eliminar un reporte con validación de permisos", 
               description = "Elimina un reporte específico validando que solo el creador o admin puedan hacerlo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte eliminado exitosamente"),
        @ApiResponse(responseCode = "403", description = "No tienes permisos para eliminar este reporte"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response eliminarReporteConValidacion(@PathParam("id") Long reporteId, @PathParam("usuarioId") Long usuarioId) {
        try {
            Reporte reporte = reporteDAO.obtenerPorId(reporteId);
            
            if (reporte == null) {
                return Response.status(Response.Status.NOT_FOUND)
                              .entity("{\"error\": \"Reporte no encontrado\"}")
                              .build();
            }

            // Obtener el usuario actual
            Usuario usuarioActual = usuarioDAO.obtenerPorId(usuarioId);
            if (usuarioActual == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                              .entity("{\"error\": \"Usuario no encontrado\"}")
                              .build();
            }

            // Verificar permisos: solo el creador o un admin pueden eliminar
            boolean esCreador = reporte.getCreador() != null && 
                               reporte.getCreador().getId().equals(usuarioId);
            boolean esAdmin = usuarioActual.getPerfil() == model.Perfil.ADMINISTRADOR;

            if (!esCreador && !esAdmin) {
                return Response.status(Response.Status.FORBIDDEN)
                              .entity("{\"error\": \"No tienes permisos para eliminar este reporte. Solo el creador o un administrador pueden eliminarlo.\"}")
                              .build();
            }

            // Si llegamos aquí, el usuario tiene permisos para eliminar
            reporteDAO.eliminar(reporteId);
            
            return Response.ok("{\"message\": \"Reporte eliminado exitosamente\"}").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("{\"error\": \"Error al eliminar reporte: " + e.getMessage() + "\"}")
                          .build();
        }
    }

    private ReporteDTO convertirAReporteDTO(Reporte reporte) {
        ReporteDTO dto = new ReporteDTO();
        dto.setId(reporte.getId());
        dto.setNombre(reporte.getNombre());
        dto.setFecha(reporte.getFecha());
        dto.setTipoMime(reporte.getTipoMime());
        dto.setTamanoArchivo(reporte.getTamanoArchivo());
        dto.setNombreArchivoOriginal(reporte.getNombreArchivoOriginal());
        dto.setFechaCreacion(reporte.getFechaCreacion());
        dto.setFechaEditado(reporte.getFechaEditado());
        
        if (reporte.getCreador() != null) {
            UsuarioSimpleDTO creadorDTO = new UsuarioSimpleDTO();
            creadorDTO.setId(reporte.getCreador().getId());
            creadorDTO.setNombreUsuario(reporte.getCreador().getNombreUsuario());
            creadorDTO.setEmail(reporte.getCreador().getEmail());
            dto.setCreador(creadorDTO);
        }
        
        return dto;
    }
}