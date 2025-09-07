package dto;

public class PreguntaRespuestaCategoriaDTO {
    private String pregunta;
    private String respuesta;
    private String categoria;

    public PreguntaRespuestaCategoriaDTO() {}

    public PreguntaRespuestaCategoriaDTO(String pregunta, String respuesta, String categoria) {
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.categoria = categoria;
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
}
