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

    // Constructores
    public ReporteSimpleDTO() {}

    public ReporteSimpleDTO(Long id, String nombre, Date fecha) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
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
}
