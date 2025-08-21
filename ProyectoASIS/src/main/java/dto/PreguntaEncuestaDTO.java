package dto;

import java.time.LocalDateTime;
import java.util.List;

import model.TipoCategoria;
import model.TipoRespuesta;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar una Pregunta de Encuesta sin referencias circulares")
public class PreguntaEncuestaDTO {
    
    @Schema(description = "ID único de la pregunta", example = "1")
    private Long id;
    
    @Schema(description = "Texto de la pregunta", example = "¿Cuál es su edad?")
    private String texto;
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;
    
    @Schema(description = "Categoría de la pregunta")
    private TipoCategoria categoria;
    
    @Schema(description = "Tipo de respuesta esperado")
    private TipoRespuesta tipoRespuesta;
    
    @Schema(description = "Lista de respuestas a esta pregunta")
    private List<RespuestaEncuestaSimpleDTO> respuestasEncuesta;

    @Schema(description = "Texto de la pregunta para exportación CSV", example = "Edad")
    private String preguntaCsv;

    // Constructores
    public PreguntaEncuestaDTO() {}

    public PreguntaEncuestaDTO(Long id, String texto, TipoCategoria categoria, 
                              TipoRespuesta tipoRespuesta, LocalDateTime fechaCreacion, 
                              LocalDateTime fechaEditado) {
        this.id = id;
        this.texto = texto;
        this.categoria = categoria;
        this.tipoRespuesta = tipoRespuesta;
        this.fechaCreacion = fechaCreacion;
        this.fechaEditado = fechaEditado;
    }

    public PreguntaEncuestaDTO(Long id, String texto, TipoCategoria categoria, 
                              TipoRespuesta tipoRespuesta, LocalDateTime fechaCreacion, 
                              LocalDateTime fechaEditado, String preguntaCsv) {
        this.id = id;
        this.texto = texto;
        this.categoria = categoria;
        this.tipoRespuesta = tipoRespuesta;
        this.fechaCreacion = fechaCreacion;
        this.fechaEditado = fechaEditado;
        this.preguntaCsv = preguntaCsv;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
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

    public TipoCategoria getCategoria() {
        return categoria;
    }

    public void setCategoria(TipoCategoria categoria) {
        this.categoria = categoria;
    }

    public TipoRespuesta getTipoRespuesta() {
        return tipoRespuesta;
    }

    public void setTipoRespuesta(TipoRespuesta tipoRespuesta) {
        this.tipoRespuesta = tipoRespuesta;
    }

    public List<RespuestaEncuestaSimpleDTO> getRespuestasEncuesta() {
        return respuestasEncuesta;
    }

    public void setRespuestasEncuesta(List<RespuestaEncuestaSimpleDTO> respuestasEncuesta) {
        this.respuestasEncuesta = respuestasEncuesta;
    }

    public String getPreguntaCsv() {
        return preguntaCsv;
    }

    public void setPreguntaCsv(String preguntaCsv) {
        this.preguntaCsv = preguntaCsv;
    }
}
