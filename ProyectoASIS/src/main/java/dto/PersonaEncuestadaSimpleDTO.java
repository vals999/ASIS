package dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simple para representar una Persona Encuestada con información básica")
public class PersonaEncuestadaSimpleDTO {
    
    @Schema(description = "ID único de la persona encuestada", example = "1")
    private Long id;
    
    @Schema(description = "Indica si tiene obra social", example = "true")
    private boolean tieneObraSocial;

    // Constructores
    public PersonaEncuestadaSimpleDTO() {}

    public PersonaEncuestadaSimpleDTO(Long id, boolean tieneObraSocial) {
        this.id = id;
        this.tieneObraSocial = tieneObraSocial;
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
}
