package dto;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simple para representar un Reporte con información básica")
public class ReporteSimpleDTO {
    
    @Schema(description = "ID único del reporte", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del reporte", example = "Reporte de Encuestas Q1 2024")
    private String nombre;
    
    @Schema(description = "Fecha del reporte")
    private Date fecha;
    
    @Schema(description = "Visibilidad del reporte", example = "PUBLICO", allowableValues = {"PRIVADO", "PUBLICO"})
    private String visibilidad;

    // Constructores
    public ReporteSimpleDTO() {}

    public ReporteSimpleDTO(Long id, String nombre, Date fecha, String visibilidad) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.visibilidad = visibilidad;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getVisibilidad() {
        return visibilidad;
    }

    public void setVisibilidad(String visibilidad) {
        this.visibilidad = visibilidad;
    }
}
