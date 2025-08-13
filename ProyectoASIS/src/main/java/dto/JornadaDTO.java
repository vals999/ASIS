package dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar una Jornada sin referencias circulares")
public class JornadaDTO {
    
    @Schema(description = "ID único de la jornada", example = "1")
    private Long id;
    
    @Schema(description = "Fecha de la jornada", example = "2024-03-20")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;
    
    @Schema(description = "Campaña a la que pertenece la jornada")
    private CampañaSimpleDTO campaña;
    
    @Schema(description = "Lista de encuestas realizadas en la jornada")
    private List<EncuestaSimpleDTO> encuestas;

    // Constructores
    public JornadaDTO() {}

    public JornadaDTO(Long id, LocalDate fecha, LocalDateTime fechaCreacion, 
                     LocalDateTime fechaEditado) {
        this.id = id;
        this.fecha = fecha;
        this.fechaCreacion = fechaCreacion;
        this.fechaEditado = fechaEditado;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaEditado() {
        return fechaEditado;
    }

    public void setFechaEditado(LocalDateTime fechaEditado) {
        this.fechaEditado = fechaEditado;
    }

    public CampañaSimpleDTO getCampaña() {
        return campaña;
    }

    public void setCampaña(CampañaSimpleDTO campaña) {
        this.campaña = campaña;
    }

    public List<EncuestaSimpleDTO> getEncuestas() {
        return encuestas;
    }

    public void setEncuestas(List<EncuestaSimpleDTO> encuestas) {
        this.encuestas = encuestas;
    }
}
