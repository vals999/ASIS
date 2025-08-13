package dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar una Campaña sin referencias circulares")
public class CampañaDTO {
    
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
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;
    
    @Schema(description = "Barrio donde se realiza la campaña")
    private BarrioSimpleDTO barrio;
    
    @Schema(description = "Lista de jornadas de la campaña")
    private List<JornadaSimpleDTO> jornadas;
    
    @Schema(description = "Lista de encuestadores asignados a la campaña")
    private List<EncuestadorSimpleDTO> encuestadores;
    
    @Schema(description = "Lista de usuarios asignados a la campaña")
    private List<UsuarioSimpleDTO> usuarios;

    // Constructores
    public CampañaDTO() {}

    public CampañaDTO(Long id, String nombre, LocalDate fechaInicio, 
                      LocalDate fechaFin, LocalDateTime fechaCreacion, 
                      LocalDateTime fechaEditado) {
        this.id = id;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
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

    public BarrioSimpleDTO getBarrio() {
        return barrio;
    }

    public void setBarrio(BarrioSimpleDTO barrio) {
        this.barrio = barrio;
    }

    public List<JornadaSimpleDTO> getJornadas() {
        return jornadas;
    }

    public void setJornadas(List<JornadaSimpleDTO> jornadas) {
        this.jornadas = jornadas;
    }

    public List<EncuestadorSimpleDTO> getEncuestadores() {
        return encuestadores;
    }

    public void setEncuestadores(List<EncuestadorSimpleDTO> encuestadores) {
        this.encuestadores = encuestadores;
    }

    public List<UsuarioSimpleDTO> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioSimpleDTO> usuarios) {
        this.usuarios = usuarios;
    }
}
