package dto.examples;

import java.util.List;

import dao_interfaces.I_BarrioDAO;
import dao_interfaces.I_UsuarioDAO;
import dao_interfaces.I_ZonaDAO;
import dto.BarrioDTO;
import dto.DTOMapper;
import dto.UsuarioDTO;
import dto.ZonaDTO;
import dto.ZonaSimpleDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Barrio;
import model.Usuario;
import model.Zona;

/**
 * Ejemplo de cómo usar los DTOs en los controladores para evitar bucles infinitos
 * Este es un ejemplo de implementación - adapta según tus controladores existentes
 */
@Path("/api/examples")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExampleController {

    @Inject
    private I_BarrioDAO barrioDAO;
    
    @Inject
    private I_ZonaDAO zonaDAO;
    
    @Inject
    private I_UsuarioDAO usuarioDAO;

    /**
     * Ejemplo: Obtener todos los barrios usando DTOs
     */
    @GET
    @Path("/barrios")
    public Response getAllBarrios() {
        try {
            List<Barrio> barrios = barrioDAO.obtenerTodos();
            List<BarrioDTO> barriosDTO = DTOMapper.toBarriosDTOList(barrios);
            return Response.ok(barriosDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error al obtener barrios: " + e.getMessage()).build();
        }
    }

    /**
     * Ejemplo: Obtener un barrio específico usando DTO
     */
    @GET
    @Path("/barrios/{id}")
    public Response getBarrioById(@PathParam("id") Long id) {
        try {
            Barrio barrio = barrioDAO.obtenerPorId(id);
            if (barrio == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Barrio no encontrado").build();
            }
            BarrioDTO barrioDTO = DTOMapper.toBarrioDTO(barrio);
            return Response.ok(barrioDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error al obtener barrio: " + e.getMessage()).build();
        }
    }

    /**
     * Ejemplo: Obtener todas las zonas usando DTOs
     */
    @GET
    @Path("/zonas")
    public Response getAllZonas() {
        try {
            List<Zona> zonas = zonaDAO.obtenerTodos();
            List<ZonaDTO> zonasDTO = DTOMapper.toZonasDTOList(zonas);
            return Response.ok(zonasDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error al obtener zonas: " + e.getMessage()).build();
        }
    }

    /**
     * Ejemplo: Obtener zona específica usando DTO
     */
    @GET
    @Path("/zonas/{id}")
    public Response getZonaById(@PathParam("id") Long id) {
        try {
            Zona zona = zonaDAO.obtenerPorId(id);
            if (zona == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Zona no encontrada").build();
            }
            ZonaDTO zonaDTO = DTOMapper.toZonaDTO(zona);
            return Response.ok(zonaDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error al obtener zona: " + e.getMessage()).build();
        }
    }

    /**
     * Ejemplo: Obtener usuarios usando DTOs
     */
    @GET
    @Path("/usuarios")
    public Response getAllUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();
            List<UsuarioDTO> usuariosDTO = DTOMapper.toUsuariosDTOList(usuarios);
            return Response.ok(usuariosDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error al obtener usuarios: " + e.getMessage()).build();
        }
    }

    /**
     * Ejemplo: Obtener zonas de un barrio específico usando DTOs simples
     */
    @GET
    @Path("/barrios/{barrioId}/zonas")
    public Response getZonasByBarrio(@PathParam("barrioId") Long barrioId) {
        try {
            Barrio barrio = barrioDAO.obtenerPorId(barrioId);
            if (barrio == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Barrio no encontrado").build();
            }
            
            List<ZonaSimpleDTO> zonasDTO = barrio.getZonas().stream()
                .map(DTOMapper::toZonaSimpleDTO)
                .collect(java.util.stream.Collectors.toList());
                
            return Response.ok(zonasDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error al obtener zonas del barrio: " + e.getMessage()).build();
        }
    }
}
