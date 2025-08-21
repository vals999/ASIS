package dto;

import model.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase utilitaria para convertir entre entidades de modelo y DTOs
 * Esto resuelve el problema de referencias circulares al exponer datos al frontend
 */
public class DTOMapper {

    // Método para convertir Barrio a BarrioDTO
    public static BarrioDTO toBarrioDTO(Barrio barrio) {
        if (barrio == null) return null;
        
        BarrioDTO dto = new BarrioDTO(
            barrio.getId(),
            barrio.getNombre(),
            barrio.getGeolocalizacion(),
            barrio.getFechaCreacion(),
            barrio.getFechaEditado()
        );
        
        // Mapear relaciones usando DTOs simples para evitar ciclos
        if (barrio.getZonas() != null) {
            dto.setZonas(barrio.getZonas().stream()
                .map(DTOMapper::toZonaSimpleDTO)
                .collect(Collectors.toList()));
        }
        
        if (barrio.getUsuarios() != null) {
            dto.setUsuarios(barrio.getUsuarios().stream()
                .map(DTOMapper::toUsuarioSimpleDTO)
                .collect(Collectors.toList()));
        }
        
        if (barrio.getCampaña() != null) {
            dto.setCampañas(barrio.getCampaña().stream()
                .map(DTOMapper::toCampañaSimpleDTO)
                .collect(Collectors.toList()));
        }
        
        if (barrio.getOrganizacionesSociales() != null) {
            dto.setOrganizacionesSociales(barrio.getOrganizacionesSociales().stream()
                .map(DTOMapper::toOrganizacionSocialSimpleDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    // Método para convertir Zona a ZonaDTO
    public static ZonaDTO toZonaDTO(Zona zona) {
        if (zona == null) return null;
        
        ZonaDTO dto = new ZonaDTO(
            zona.getId(),
            zona.getNombre(),
            zona.getGeolocalizacion(),
            zona.getFechaCreacion(),
            zona.getFechaEditado()
        );
        
        dto.setBarrio(toBarrioSimpleDTO(zona.getBarrio()));
        
        if (zona.getEncuesta() != null) {
            dto.setEncuestas(zona.getEncuesta().stream()
                .map(DTOMapper::toEncuestaSimpleDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    // Método para convertir Usuario a UsuarioDTO
    public static UsuarioDTO toUsuarioDTO(Usuario usuario) {
        if (usuario == null) return null;
        
        UsuarioDTO dto = new UsuarioDTO(
            usuario.getId(),
            usuario.getNombreUsuario(),
            usuario.getEmail(),
            usuario.isHabilitado(),
            usuario.getPerfil(),
            usuario.getFechaCreacion(),
            usuario.getFechaEditado()
        );
        
        dto.setDatosPersonales(toDatosPersonalesDTO(usuario.getDatosPersonales()));
        dto.setEncuestador(toEncuestadorSimpleDTO(usuario.getEscuestador()));
        
        if (usuario.getCampañas() != null) {
            dto.setCampañas(usuario.getCampañas().stream()
                .map(DTOMapper::toCampañaSimpleDTO)
                .collect(Collectors.toList()));
        }
        
        if (usuario.getBarrios() != null) {
            dto.setBarrios(usuario.getBarrios().stream()
                .map(DTOMapper::toBarrioSimpleDTO)
                .collect(Collectors.toList()));
        }
        
        if (usuario.getOrganizacionesSociales() != null) {
            dto.setOrganizacionesSociales(usuario.getOrganizacionesSociales().stream()
                .map(DTOMapper::toOrganizacionSocialSimpleDTO)
                .collect(Collectors.toList()));
        }
        
        if (usuario.getReportes() != null) {
            dto.setReportes(usuario.getReportes().stream()
                .map(DTOMapper::toReporteSimpleDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    // Métodos para DTOs simples
    public static BarrioSimpleDTO toBarrioSimpleDTO(Barrio barrio) {
        if (barrio == null) return null;
        return new BarrioSimpleDTO(
            barrio.getId(),
            barrio.getNombre(),
            barrio.getGeolocalizacion()
        );
    }

    public static ZonaSimpleDTO toZonaSimpleDTO(Zona zona) {
        if (zona == null) return null;
        return new ZonaSimpleDTO(
            zona.getId(),
            zona.getNombre(),
            zona.getGeolocalizacion()
        );
    }

    public static UsuarioSimpleDTO toUsuarioSimpleDTO(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioSimpleDTO(
            usuario.getId(),
            usuario.getNombreUsuario(),
            usuario.getEmail(),
            usuario.isHabilitado(),
            usuario.getPerfil()
        );
    }

    public static DatosPersonalesDTO toDatosPersonalesDTO(DatosPersonales datos) {
        if (datos == null) return null;
        return new DatosPersonalesDTO(
            datos.getId(),
            datos.getNombre(),
            datos.getApellido(),
            datos.getDni(),
            datos.getEdad(),
            datos.getGenero(),
            datos.getFechaCreacion(),
            datos.getFechaEditado()
        );
    }

    public static CampañaSimpleDTO toCampañaSimpleDTO(Campaña campaña) {
        if (campaña == null) return null;
        return new CampañaSimpleDTO(
            campaña.getId(),
            campaña.getNombre(),
            campaña.getFechaInicio(),
            campaña.getFechaFin()
        );
    }

    public static EncuestadorSimpleDTO toEncuestadorSimpleDTO(Encuestador encuestador) {
        if (encuestador == null) return null;
        return new EncuestadorSimpleDTO(
            encuestador.getId(),
            encuestador.getOcupacion()
        );
    }

    public static OrganizacionSocialSimpleDTO toOrganizacionSocialSimpleDTO(OrganizacionSocial org) {
        if (org == null) return null;
        return new OrganizacionSocialSimpleDTO(
            org.getId(),
            org.getNombre(),
            org.getDireccion(),
            org.getActividad()
        );
    }

    public static ReporteSimpleDTO toReporteSimpleDTO(Reporte reporte) {
        if (reporte == null) return null;
        return new ReporteSimpleDTO(
            reporte.getId(),
            reporte.getNombre(),
            reporte.getFecha()
        );
    }

    public static EncuestaSimpleDTO toEncuestaSimpleDTO(Encuesta encuesta) {
        if (encuesta == null) return null;
        return new EncuestaSimpleDTO(
            encuesta.getId(),
            encuesta.getFecha()
        );
    }

    public static JornadaSimpleDTO toJornadaSimpleDTO(Jornada jornada) {
        if (jornada == null) return null;
        return new JornadaSimpleDTO(
            jornada.getId(),
            jornada.getFecha()
        );
    }

    public static PersonaEncuestadaSimpleDTO toPersonaEncuestadaSimpleDTO(PersonaEncuestada persona) {
        if (persona == null) return null;
        return new PersonaEncuestadaSimpleDTO(
            persona.getId(),
            persona.isTieneObraSocial()
        );
    }

    public static PreguntaEncuestaSimpleDTO toPreguntaEncuestaSimpleDTO(PreguntaEncuesta pregunta) {
        if (pregunta == null) return null;
        return new PreguntaEncuestaSimpleDTO(
            pregunta.getId(),
            pregunta.getTexto(),
            pregunta.getCategoria(),
            pregunta.getTipoRespuesta()
        );
    }

    public static RespuestaEncuestaSimpleDTO toRespuestaEncuestaSimpleDTO(RespuestaEncuesta respuesta) {
        if (respuesta == null) return null;
        return new RespuestaEncuestaSimpleDTO(
            respuesta.getId(),
            respuesta.getValor()
        );
    }

    // Método para convertir RespuestaEncuesta a RespuestaEncuestaDTO completo
    public static RespuestaEncuestaDTO toRespuestaEncuestaDTO(RespuestaEncuesta respuesta) {
        if (respuesta == null) return null;
        
        RespuestaEncuestaDTO dto = new RespuestaEncuestaDTO(
            respuesta.getId(),
            respuesta.getValor(),
            respuesta.getFechaCreacion(),
            respuesta.getFechaEditado()
        );
        
        // Mapear relaciones usando DTOs simples para evitar ciclos
        dto.setPreguntaEncuesta(toPreguntaEncuestaSimpleDTO(respuesta.getPregunta()));
        dto.setEncuesta(toEncuestaSimpleDTO(respuesta.getEncuesta()));
        
        return dto;
    }

    // Método para convertir PreguntaEncuesta a PreguntaEncuestaDTO completo
    public static PreguntaEncuestaDTO toPreguntaEncuestaDTO(PreguntaEncuesta pregunta) {
        if (pregunta == null) return null;
        
        PreguntaEncuestaDTO dto = new PreguntaEncuestaDTO(
            pregunta.getId(),
            pregunta.getTexto(),
            pregunta.getCategoria(),
            pregunta.getTipoRespuesta(),
            pregunta.getFechaCreacion(),
            pregunta.getFechaEditado()
        );
        
        // Mapear respuestas usando DTOs simples para evitar ciclos
        if (pregunta.getRespuestaEncuesta() != null) {
            dto.setRespuestasEncuesta(pregunta.getRespuestaEncuesta().stream()
                .map(DTOMapper::toRespuestaEncuestaSimpleDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    // Método para convertir listas
    public static List<BarrioDTO> toBarriosDTOList(List<Barrio> barrios) {
        if (barrios == null) return null;
        return barrios.stream()
            .map(DTOMapper::toBarrioDTO)
            .collect(Collectors.toList());
    }

    public static List<ZonaDTO> toZonasDTOList(List<Zona> zonas) {
        if (zonas == null) return null;
        return zonas.stream()
            .map(DTOMapper::toZonaDTO)
            .collect(Collectors.toList());
    }

    public static List<UsuarioDTO> toUsuariosDTOList(List<Usuario> usuarios) {
        if (usuarios == null) return null;
        return usuarios.stream()
            .map(DTOMapper::toUsuarioDTO)
            .collect(Collectors.toList());
    }
}
