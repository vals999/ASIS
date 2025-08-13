package dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar una Organización Social sin referencias circulares")
public class OrganizacionSocialDTO {
    
    @Schema(description = "ID único de la organización social", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la organización social", example = "Centro Vecinal San Juan")
    private String nombre;
    
    @Schema(description = "Dirección de la organización", example = "Av. Principal 123")
    private String direccion;
    
    @Schema(description = "Actividad principal de la organización", example = "Trabajo comunitario")
    private String actividad;
    
    @Schema(description = "Información de contacto", example = "Tel: 123-456-7890")
    private String infoContacto;
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;
    
    @Schema(description = "Lista de usuarios asociados a la organización")
    private List<UsuarioSimpleDTO> usuarios;
    
    @Schema(description = "Lista de barrios donde opera la organización")
    private List<BarrioSimpleDTO> barrios;

    // Constructores
    public OrganizacionSocialDTO() {}

    public OrganizacionSocialDTO(Long id, String nombre, String direccion, 
                                String actividad, String infoContacto,
                                LocalDateTime fechaCreacion, LocalDateTime fechaEditado) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.actividad = actividad;
        this.infoContacto = infoContacto;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getInfoContacto() {
        return infoContacto;
    }

    public void setInfoContacto(String infoContacto) {
        this.infoContacto = infoContacto;
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

    public List<UsuarioSimpleDTO> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioSimpleDTO> usuarios) {
        this.usuarios = usuarios;
    }

    public List<BarrioSimpleDTO> getBarrios() {
        return barrios;
    }

    public void setBarrios(List<BarrioSimpleDTO> barrios) {
        this.barrios = barrios;
    }
}
