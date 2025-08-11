package dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar Datos Personales sin referencias circulares")
public class DatosPersonalesDTO {
    
    @Schema(description = "ID único de los datos personales", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la persona", example = "Juan")
    private String nombre;
    
    @Schema(description = "Apellido de la persona", example = "Pérez")
    private String apellido;
    
    @Schema(description = "DNI de la persona", example = "12345678")
    private String dni;
    
    @Schema(description = "Edad de la persona", example = "25")
    private int edad;
    
    @Schema(description = "Género de la persona", example = "Masculino")
    private String genero;
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;

    // Constructores
    public DatosPersonalesDTO() {}

    public DatosPersonalesDTO(Long id, String nombre, String apellido, 
                             String dni, int edad, String genero,
                             LocalDateTime fechaCreacion, LocalDateTime fechaEditado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.edad = edad;
        this.genero = genero;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
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
}
