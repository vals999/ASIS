package dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simple para representar una Organización Social con información básica")
public class OrganizacionSocialSimpleDTO {
    
    @Schema(description = "ID único de la organización social", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la organización social", example = "Centro Vecinal San Juan")
    private String nombre;
    
    @Schema(description = "Dirección de la organización", example = "Av. Principal 123")
    private String direccion;
    
    @Schema(description = "Actividad principal de la organización", example = "Trabajo comunitario")
    private String actividad;

    // Constructores
    public OrganizacionSocialSimpleDTO() {}

    public OrganizacionSocialSimpleDTO(Long id, String nombre, String direccion, 
                                      String actividad) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.actividad = actividad;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }
}
