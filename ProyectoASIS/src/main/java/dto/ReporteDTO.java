package dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar un Reporte sin referencias circulares")
public class ReporteDTO {
    
    @Schema(description = "ID único del reporte", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del reporte", example = "Reporte de Encuestas Q1 2024")
    private String nombre;
    
    @Schema(description = "Fecha del reporte")
    private Date fecha;
    
    @Schema(description = "Tipo MIME del archivo")
    private String tipoMime;
    
    @Schema(description = "Tamaño del archivo en bytes")
    private Long tamanoArchivo;
    
    @Schema(description = "Nombre original del archivo")
    private String nombreArchivoOriginal;
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;
    
    @Schema(description = "Usuario que creó el reporte")
    private UsuarioSimpleDTO creador;
    
    @Schema(description = "Lista de usuarios con acceso al reporte")
    private List<UsuarioSimpleDTO> usuarios;

    // Constructores
    public ReporteDTO() {}

    public ReporteDTO(Long id, String nombre, Date fecha, 
                     LocalDateTime fechaCreacion, LocalDateTime fechaEditado) {
        this.id = id;
        this.nombre = nombre;
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

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public Long getTamanoArchivo() {
        return tamanoArchivo;
    }

    public void setTamanoArchivo(Long tamanoArchivo) {
        this.tamanoArchivo = tamanoArchivo;
    }

    public String getNombreArchivoOriginal() {
        return nombreArchivoOriginal;
    }

    public void setNombreArchivoOriginal(String nombreArchivoOriginal) {
        this.nombreArchivoOriginal = nombreArchivoOriginal;
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

    public UsuarioSimpleDTO getCreador() {
        return creador;
    }

    public void setCreador(UsuarioSimpleDTO creador) {
        this.creador = creador;
    }

    public List<UsuarioSimpleDTO> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioSimpleDTO> usuarios) {
        this.usuarios = usuarios;
    }
}
