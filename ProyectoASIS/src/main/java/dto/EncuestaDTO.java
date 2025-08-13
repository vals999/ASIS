package dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar una Encuesta sin referencias circulares")
public class EncuestaDTO {
    
    @Schema(description = "ID único de la encuesta", example = "1")
    private Long id;
    
    @Schema(description = "Fecha de realización de la encuesta")
    private Date fecha;
    
    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Fecha de última edición del registro")
    private LocalDateTime fechaEditado;
    
    @Schema(description = "Zona donde se realizó la encuesta")
    private ZonaSimpleDTO zona;
    
    @Schema(description = "Jornada a la que pertenece la encuesta")
    private JornadaSimpleDTO jornada;
    
    @Schema(description = "Lista de personas encuestadas")
    private List<PersonaEncuestadaSimpleDTO> personasEncuestadas;
    
    @Schema(description = "Lista de respuestas de la encuesta")
    private List<RespuestaEncuestaSimpleDTO> respuestasEncuesta;

    // Constructores
    public EncuestaDTO() {}

    public EncuestaDTO(Long id, Date fecha, LocalDateTime fechaCreacion, 
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
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

    public ZonaSimpleDTO getZona() {
        return zona;
    }

    public void setZona(ZonaSimpleDTO zona) {
        this.zona = zona;
    }

    public JornadaSimpleDTO getJornada() {
        return jornada;
    }

    public void setJornada(JornadaSimpleDTO jornada) {
        this.jornada = jornada;
    }

    public List<PersonaEncuestadaSimpleDTO> getPersonasEncuestadas() {
        return personasEncuestadas;
    }

    public void setPersonasEncuestadas(List<PersonaEncuestadaSimpleDTO> personasEncuestadas) {
        this.personasEncuestadas = personasEncuestadas;
    }

    public List<RespuestaEncuestaSimpleDTO> getRespuestasEncuesta() {
        return respuestasEncuesta;
    }

    public void setRespuestasEncuesta(List<RespuestaEncuestaSimpleDTO> respuestasEncuesta) {
        this.respuestasEncuesta = respuestasEncuesta;
    }
}
