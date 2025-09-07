package model;

/**
 * Enumeración que define los niveles de visibilidad para los reportes
 */
public enum VisibilidadReporte {
    PUBLICO("Público"),
    PRIVADO("Privado");
    
    private final String descripcion;
    
    VisibilidadReporte(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
}