package dto;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simple para representar una Encuesta con información básica")
public class EncuestaSimpleDTO {
    
    @Schema(description = "ID único de la encuesta", example = "1")
    private Long id;
    
    @Schema(description = "Fecha de realización de la encuesta")
    private Date fecha;

    // Constructores
    public EncuestaSimpleDTO() {}

    public EncuestaSimpleDTO(Long id, Date fecha) {
        this.id = id;
        this.fecha = fecha;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
