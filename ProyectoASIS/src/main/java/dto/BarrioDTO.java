package dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar un Barrio sin referencias circulares")
public class BarrioDTO {
    
    @Schema(description = "ID único del barrio", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del barrio", example = "Centro")
    private String nombre;
    
    @Schema(description = "Geolocalización del barrio", example = "Barrio céntrico de la ciudad")
    private String geolocalizacion;
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;
    
    @Schema(description = "Lista de zonas que pertenecen a este barrio")
    private List<ZonaSimpleDTO> zonas;
    
    @Schema(description = "Lista de usuarios asignados a este barrio")
    private List<UsuarioSimpleDTO> usuarios;
    
    @Schema(description = "Lista de campañas realizadas en este barrio")
    private List<CampañaSimpleDTO> campañas;
    
    @Schema(description = "Lista de organizaciones sociales del barrio")
    private List<OrganizacionSocialSimpleDTO> organizacionesSociales;

    // Constructores
    public BarrioDTO() {}

    public BarrioDTO(Long id, String nombre, String geolocalizacion, 
                     LocalDateTime fechaCreacion, LocalDateTime fechaEditado) {
        this.id = id;
        this.nombre = nombre;
        this.geolocalizacion = geolocalizacion;
        this.fechaCreacion = fechaCreacion;
        this.fechaEditado = fechaEditado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGeolocalizacion() {
        return geolocalizacion;
    }

    public void setGeolocalizacion(String geolocalizacion) {
        this.geolocalizacion = geolocalizacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaEditado() {
        return fechaEditado;
    }

    public void setFechaEditado(LocalDateTime fechaEditado) {
        this.fechaEditado = fechaEditado;
    }

    public List<ZonaSimpleDTO> getZonas() {
        return zonas;
    }

    public void setZonas(List<ZonaSimpleDTO> zonas) {
        this.zonas = zonas;
    }

    public List<UsuarioSimpleDTO> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioSimpleDTO> usuarios) {
        this.usuarios = usuarios;
    }

    public List<CampañaSimpleDTO> getCampañas() {
        return campañas;
    }

    public void setCampañas(List<CampañaSimpleDTO> campañas) {
        this.campañas = campañas;
    }

    public List<OrganizacionSocialSimpleDTO> getOrganizacionesSociales() {
        return organizacionesSociales;
    }

    public void setOrganizacionesSociales(List<OrganizacionSocialSimpleDTO> organizacionesSociales) {
        this.organizacionesSociales = organizacionesSociales;
    }
}
