package dto;

public class PreguntaRespuestaCategoriaDTO {
    private String pregunta;
    private String respuesta;
    private String categoria;
    private Long encuestaId;

    public PreguntaRespuestaCategoriaDTO() {}

    public PreguntaRespuestaCategoriaDTO(String pregunta, String respuesta, String categoria) {
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.categoria = categoria;
    }

    public PreguntaRespuestaCategoriaDTO(String pregunta, String respuesta, String categoria, Long encuestaId) {
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.categoria = categoria;
        this.encuestaId = encuestaId;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }
}
