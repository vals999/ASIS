package dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simple para representar una Zona con información básica")
public class ZonaSimpleDTO {
    
    @Schema(description = "ID único de la zona", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la zona", example = "Zona Norte")
    private String nombre;
    
    @Schema(description = "Geolocalización de la zona", example = "-34.6037, -58.3816")
    private String geolocalizacion;

    // Constructores
    public ZonaSimpleDTO() {}

    public ZonaSimpleDTO(Long id, String nombre, String geolocalizacion) {
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
