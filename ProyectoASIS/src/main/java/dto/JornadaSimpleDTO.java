package dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simple para representar una Jornada con información básica")
public class JornadaSimpleDTO {
    
    @Schema(description = "ID único de la jornada", example = "1")
    private Long id;
    
    @Schema(description = "Fecha de la jornada", example = "2024-03-20")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    // Constructores
    public JornadaSimpleDTO() {}

    public JornadaSimpleDTO(Long id, LocalDate fecha) {
        this.id = id;
        this.fecha = fecha;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
