package dto;

import java.time.LocalDateTime;
import java.util.List;

import model.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar un Usuario sin referencias circulares")
public class UsuarioDTO {
    
    @Schema(description = "ID único del usuario", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de usuario", example = "juan.perez")
    private String nombreUsuario;
    
    @Schema(description = "Email del usuario", example = "juan.perez@example.com")
    private String email;
    
    @Schema(description = "Indica si el usuario está habilitado", example = "true")
    private boolean habilitado;
    
    @Schema(description = "Perfil del usuario")
    private Perfil perfil;
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;
    
    @Schema(description = "Datos personales del usuario")
    private DatosPersonalesDTO datosPersonales;
    
    @Schema(description = "Información del encuestador si aplica")
    private EncuestadorSimpleDTO encuestador;
    
    @Schema(description = "Lista de campañas asignadas al usuario")
    private List<CampañaSimpleDTO> campañas;
    
    @Schema(description = "Lista de barrios asignados al usuario")
    private List<BarrioSimpleDTO> barrios;
    
    @Schema(description = "Lista de organizaciones sociales del usuario")
    private List<OrganizacionSocialSimpleDTO> organizacionesSociales;
    
    @Schema(description = "Lista de reportes del usuario")
    private List<ReporteSimpleDTO> reportes;

    // Constructores
    public UsuarioDTO() {}

    public UsuarioDTO(Long id, String nombreUsuario, String email, 
                      boolean habilitado, Perfil perfil, 
                      LocalDateTime fechaCreacion, LocalDateTime fechaEditado) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.habilitado = habilitado;
        this.perfil = perfil;
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

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
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

    public DatosPersonalesDTO getDatosPersonales() {
        return datosPersonales;
    }

    public void setDatosPersonales(DatosPersonalesDTO datosPersonales) {
        this.datosPersonales = datosPersonales;
    }

    public EncuestadorSimpleDTO getEncuestador() {
        return encuestador;
    }

    public void setEncuestador(EncuestadorSimpleDTO encuestador) {
        this.encuestador = encuestador;
    }

    public List<CampañaSimpleDTO> getCampañas() {
        return campañas;
    }

    public void setCampañas(List<CampañaSimpleDTO> campañas) {
        this.campañas = campañas;
    }

    public List<BarrioSimpleDTO> getBarrios() {
        return barrios;
    }

    public void setBarrios(List<BarrioSimpleDTO> barrios) {
        this.barrios = barrios;
    }

    public List<OrganizacionSocialSimpleDTO> getOrganizacionesSociales() {
        return organizacionesSociales;
    }

    public void setOrganizacionesSociales(List<OrganizacionSocialSimpleDTO> organizacionesSociales) {
        this.organizacionesSociales = organizacionesSociales;
    }

    public List<ReporteSimpleDTO> getReportes() {
        return reportes;
    }

    public void setReportes(List<ReporteSimpleDTO> reportes) {
        this.reportes = reportes;
    }
}
