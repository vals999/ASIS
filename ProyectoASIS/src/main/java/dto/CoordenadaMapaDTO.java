package dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO específico para las coordenadas del mapa")
public class CoordenadaMapaDTO {
    
    @Schema(description = "ID de la encuesta", example = "1")
    private Long encuestaId;
    
    @Schema(description = "ID de la respuesta", example = "1")
    private Long respuestaId;
    
    @Schema(description = "Valor de la respuesta (coordenada)", example = "-34.9215")
    private String valor;
    
    @Schema(description = "ID de la pregunta", example = "1")
    private Long preguntaId;
    
    @Schema(description = "Texto de la pregunta", example = "Latitud")
    private String textoPregunta;

    // Constructores
    public CoordenadaMapaDTO() {}

    public CoordenadaMapaDTO(Long encuestaId, Long respuestaId, String valor, 
                            Long preguntaId, String textoPregunta) {
        this.encuestaId = encuestaId;
        this.respuestaId = respuestaId;
        this.valor = valor;
        this.preguntaId = preguntaId;
        this.textoPregunta = textoPregunta;
    }

    // Getters y Setters
    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }

    public Long getRespuestaId() {
        return respuestaId;
    }

    public void setRespuestaId(Long respuestaId) {
        this.respuestaId = respuestaId;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Long getPreguntaId() {
        return preguntaId;
    }

    public void setPreguntaId(Long preguntaId) {
        this.preguntaId = preguntaId;
    }

    public String getTextoPregunta() {
        return textoPregunta;
    }

    public void setTextoPregunta(String textoPregunta) {
        this.textoPregunta = textoPregunta;
    }
}