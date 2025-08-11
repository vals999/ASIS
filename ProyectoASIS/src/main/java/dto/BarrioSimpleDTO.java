package dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simple para representar un Barrio con información básica")
public class BarrioSimpleDTO {
    
    @Schema(description = "ID único del barrio", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del barrio", example = "Centro")
    private String nombre;
    
    @Schema(description = "Geolocalización del barrio", example = "Barrio céntrico de la ciudad")
    private String geolocalizacion;

    // Constructores
    public BarrioSimpleDTO() {}

    public BarrioSimpleDTO(Long id, String nombre, String geolocalizacion) {
        this.id = id;
        this.nombre = nombre;
        this.geolocalizacion = geolocalizacion;
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
}
