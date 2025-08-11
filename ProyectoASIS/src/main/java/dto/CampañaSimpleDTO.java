package dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simple para representar una Campaña con información básica")
public class CampañaSimpleDTO {
    
    @Schema(description = "ID único de la campaña", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la campaña", example = "Campaña de Salud 2024")
    private String nombre;
    
    @Schema(description = "Fecha de inicio de la campaña", example = "2024-03-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    
    @Schema(description = "Fecha de fin de la campaña", example = "2024-06-30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    // Constructores
    public CampañaSimpleDTO() {}

    public CampañaSimpleDTO(Long id, String nombre, LocalDate fechaInicio, 
                           LocalDate fechaFin) {
        this.id = id;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
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

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}
