package dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simple para representar un Encuestador con información básica")
public class EncuestadorSimpleDTO {
    
    @Schema(description = "ID único del encuestador", example = "1")
    private Long id;
    
    @Schema(description = "Ocupación del encuestador", example = "Estudiante de Sociología")
    private String ocupacion;

    // Constructores
    public EncuestadorSimpleDTO() {}

    public EncuestadorSimpleDTO(Long id, String ocupacion) {
        this.id = id;
        this.ocupacion = ocupacion;
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
}
