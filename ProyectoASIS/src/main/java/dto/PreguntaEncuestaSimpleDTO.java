package dto;

import model.TipoCategoria;
import model.TipoRespuesta;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simple para representar una Pregunta de Encuesta con información básica")
public class PreguntaEncuestaSimpleDTO {
    
    @Schema(description = "ID único de la pregunta", example = "1")
    private Long id;
    
    @Schema(description = "Texto de la pregunta", example = "¿Cuál es su edad?")
    private String texto;
    
    @Schema(description = "Categoría de la pregunta")
    private TipoCategoria categoria;
    
    @Schema(description = "Tipo de respuesta esperado")
    private TipoRespuesta tipoRespuesta;

    // Constructores
    public PreguntaEncuestaSimpleDTO() {}

    public PreguntaEncuestaSimpleDTO(Long id, String texto, TipoCategoria categoria, 
                                    TipoRespuesta tipoRespuesta) {
        this.id = id;
        this.texto = texto;
        this.categoria = categoria;
        this.tipoRespuesta = tipoRespuesta;
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
}
