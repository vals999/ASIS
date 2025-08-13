package dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar un Encuestador sin referencias circulares")
public class EncuestadorDTO {
    
    @Schema(description = "ID único del encuestador", example = "1")
    private Long id;
    
    @Schema(description = "Ocupación del encuestador", example = "Estudiante de Sociología")
    private String ocupacion;
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;
    
    @Schema(description = "Datos personales del encuestador")
    private DatosPersonalesDTO datosPersonales;
    
    @Schema(description = "Usuario asociado al encuestador")
    private UsuarioSimpleDTO usuario;
    
    @Schema(description = "Lista de campañas asignadas al encuestador")
    private List<CampañaSimpleDTO> campañas;

    // Constructores
    public EncuestadorDTO() {}

    public EncuestadorDTO(Long id, String ocupacion, LocalDateTime fechaCreacion, 
                         LocalDateTime fechaEditado) {
        this.id = id;
        this.ocupacion = ocupacion;
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

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
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

    public UsuarioSimpleDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSimpleDTO usuario) {
        this.usuario = usuario;
    }

    public List<CampañaSimpleDTO> getCampañas() {
        return campañas;
    }

    public void setCampañas(List<CampañaSimpleDTO> campañas) {
        this.campañas = campañas;
    }
}
