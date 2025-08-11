package dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar una Persona Encuestada sin referencias circulares")
public class PersonaEncuestadaDTO {
    
    @Schema(description = "ID único de la persona encuestada", example = "1")
    private Long id;
    
    @Schema(description = "Indica si tiene obra social", example = "true")
    private boolean tieneObraSocial;
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;
    
    @Schema(description = "Datos personales de la persona encuestada")
    private DatosPersonalesDTO datosPersonales;
    
    @Schema(description = "Lista de encuestas en las que participó")
    private List<EncuestaSimpleDTO> encuestas;

    // Constructores
    public PersonaEncuestadaDTO() {}

    public PersonaEncuestadaDTO(Long id, boolean tieneObraSocial, 
                               LocalDateTime fechaCreacion, LocalDateTime fechaEditado) {
        this.id = id;
        this.tieneObraSocial = tieneObraSocial;
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

    public boolean isTieneObraSocial() {
        return tieneObraSocial;
    }

    public void setTieneObraSocial(boolean tieneObraSocial) {
        this.tieneObraSocial = tieneObraSocial;
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

    public List<EncuestaSimpleDTO> getEncuestas() {
        return encuestas;
    }

    public void setEncuestas(List<EncuestaSimpleDTO> encuestas) {
        this.encuestas = encuestas;
    }
}
