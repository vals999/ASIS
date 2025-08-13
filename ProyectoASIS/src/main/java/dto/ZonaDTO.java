package dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar una Zona sin referencias circulares")
public class ZonaDTO {
    
    @Schema(description = "ID único de la zona", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la zona", example = "Zona Norte")
    private String nombre;
    
    @Schema(description = "Geolocalización de la zona", example = "-34.6037, -58.3816")
    private String geolocalizacion;
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;
    
    @Schema(description = "Barrio al que pertenece esta zona")
    private BarrioSimpleDTO barrio;
    
    @Schema(description = "Lista de encuestas realizadas en esta zona")
    private List<EncuestaSimpleDTO> encuestas;

    // Constructores
    public ZonaDTO() {}

    public ZonaDTO(Long id, String nombre, String geolocalizacion, 
                   LocalDateTime fechaCreacion, LocalDateTime fechaEditado) {
        this.id = id;
        this.nombre = nombre;
        this.geolocalizacion = geolocalizacion;
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

    public String getGeolocalizacion() {
        return geolocalizacion;
    }

    public void setGeolocalizacion(String geolocalizacion) {
        this.geolocalizacion = geolocalizacion;
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

    public List<EncuestaSimpleDTO> getEncuestas() {
        return encuestas;
    }

    public void setEncuestas(List<EncuestaSimpleDTO> encuestas) {
        this.encuestas = encuestas;
    }
}
