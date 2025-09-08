package dto;

/**
 * DTO para manejar filtros múltiples con categoría, pregunta y respuesta
 */
public class FiltroMultiple {
    private String categoria;
    private String pregunta;
    private String respuesta;
    
    public FiltroMultiple() {}
    
    public FiltroMultiple(String categoria, String pregunta, String respuesta) {
        this.categoria = categoria;
        this.pregunta = pregunta;
        this.respuesta = respuesta;
    }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getPregunta() { return pregunta; }
    public void setPregunta(String pregunta) { this.pregunta = pregunta; }
    
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
}
