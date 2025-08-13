package dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simple para representar una Respuesta de Encuesta con información básica")
public class RespuestaEncuestaSimpleDTO {
    
    @Schema(description = "ID único de la respuesta", example = "1")
    private Long id;
    
    @Schema(description = "Valor de la respuesta", example = "25")
    private String valor;

    // Constructores
    public RespuestaEncuestaSimpleDTO() {}

    public RespuestaEncuestaSimpleDTO(Long id, String valor) {
        this.id = id;
        this.valor = valor;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
