package model;

/**
 * Enum para definir los tipos de visibilidad de los reportes
 */
public enum TipoVisibilidad {
    /**
     * Reporte privado - Solo visible para usuarios autenticados en el sistema
     */
    PRIVADO,
    
    /**
     * Reporte público - Visible en la landing page para cualquier visitante
     */
    PUBLICO
}