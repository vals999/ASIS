package dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar una Respuesta de Encuesta sin referencias circulares")
public class RespuestaEncuestaDTO {
    
    @Schema(description = "ID único de la respuesta", example = "1")
    private Long id;
    
    @Schema(description = "Valor de la respuesta", example = "25")
    private String valor;
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;
    
    @Schema(description = "Pregunta a la que corresponde esta respuesta")
    private PreguntaEncuestaSimpleDTO preguntaEncuesta;
    
    @Schema(description = "Encuesta a la que pertenece esta respuesta")
    private EncuestaSimpleDTO encuesta;

    // Constructores
    public RespuestaEncuestaDTO() {}

    public RespuestaEncuestaDTO(Long id, String valor, LocalDateTime fechaCreacion, 
                               LocalDateTime fechaEditado) {
        this.id = id;
        this.valor = valor;
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

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
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

    public PreguntaEncuestaSimpleDTO getPreguntaEncuesta() {
        return preguntaEncuesta;
    }

    public void setPreguntaEncuesta(PreguntaEncuestaSimpleDTO preguntaEncuesta) {
        this.preguntaEncuesta = preguntaEncuesta;
    }

    public EncuestaSimpleDTO getEncuesta() {
        return encuesta;
    }

    public void setEncuesta(EncuestaSimpleDTO encuesta) {
        this.encuesta = encuesta;
    }
}
