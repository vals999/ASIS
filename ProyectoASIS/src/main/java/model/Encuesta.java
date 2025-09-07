package model;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import dao_interfaces.EliminableLogico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "ENCUESTAS")
public class Encuesta implements EliminableLogico{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "fecha_encuesta")
	@Temporal(TemporalType.DATE)
    private Date fecha;
    
    @Column(name = "id_externo", length = 255)
    private String idExterno; // Campo para almacenar el identificador del CSV
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaCreacion;
	
	@Column(name = "fecha_editado", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEditado;
	
	@Column(name = "fecha_eliminacion", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEliminacion;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="zona_id")
    private Zona zona;
    
    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "jornada_id")
    private Jornada jornada;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "persona_encuesta", joinColumns = @JoinColumn(name = "persona_id"), inverseJoinColumns = @JoinColumn (name = "encuesta_id"))
    private List<PersonaEncuestada> personasEncuestadas;
        
    @OneToMany(mappedBy = "encuesta", fetch = FetchType.LAZY)
    private List<RespuestaEncuesta> respuestasEncuesta;
    
	public Encuesta() {
		
		super();
	}
	
	public Encuesta(Long id, Date fecha, Zona zona, List<PersonaEncuestada> encuestados) {
		super();
		this.id = id;
		this.fecha = fecha;
		this.zona = zona;
	}
	
	@PrePersist
	protected void onCreate() {
		this.fechaCreacion = LocalDateTime.now();
		this.fechaEditado = LocalDateTime.now();
	}
	
	@PreUpdate
    protected void onUpdate() {
        this.fechaEditado = LocalDateTime.now();
    }

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

	public String getIdExterno() {
		return idExterno;
	}

	public void setIdExterno(String idExterno) {
		this.idExterno = idExterno;
	}

	public Zona getZona() {
		return zona;
	}

	public void setZona(Zona zona) {
		this.zona = zona;
	}

	public Jornada getJornada() {
		return jornada;
	}
	
	public void setJornada(Jornada jornada) {
		this.jornada = jornada;
	}
	
	public List<PersonaEncuestada> getPersonasEncuestadas() {
		return personasEncuestadas;
	}

	public void setPersonasEncuestadas(List<PersonaEncuestada> personasEncuestadas) {
		this.personasEncuestadas = personasEncuestadas;
	}

	public List<RespuestaEncuesta> getRespuestaEncuesta() {
		return respuestasEncuesta;
	}

	public void setRespuestaEncuesta(List<RespuestaEncuesta> respuestaEncuesta) {
		this.respuestasEncuesta = respuestaEncuesta;
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

	public LocalDateTime getFechaEliminacion() {
		return fechaEliminacion;
	}

	public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
		this.fechaEliminacion = fechaEliminacion;
	}
	
	public void agregarPersonasEncuestadas(PersonaEncuestada personaEncuestada) {
        if (personaEncuestada != null && !this.personasEncuestadas.contains(personaEncuestada)) {
        	this.personasEncuestadas.add(personaEncuestada);
        	if (!personaEncuestada.getEncuestas().contains(this)) {
        		personaEncuestada.getEncuestas().add(this);
            }            
        }
    }

    public void quitarPersonasEncuestadas(PersonaEncuestada personaEncuestada) {
        if (personaEncuestada != null && this.personasEncuestadas.remove(personaEncuestada)) {
        	personaEncuestada.getEncuestas().remove(this);
        }
    }

    public boolean tienePersonasEncuestadas(PersonaEncuestada personaEncuestada) {
    	return personaEncuestada != null && this.personasEncuestadas.contains(personaEncuestada);
    }
    
    public void agregarRespuestaEncuesta(RespuestaEncuesta respuestaEncuesta) {
        if (respuestaEncuesta != null && !this.respuestasEncuesta.contains(respuestaEncuesta)) {
        	this.respuestasEncuesta.add(respuestaEncuesta);
        	respuestaEncuesta.setEncuesta(this);   
        }
    }

    public void quitarRespuestaEncuesta(RespuestaEncuesta respuestaEncuesta) {
        if (respuestaEncuesta != null && this.respuestasEncuesta.remove(respuestaEncuesta)) {
        	respuestaEncuesta.setEncuesta(null);
        }
    }

    public boolean tieneRespuestaEncuesta(RespuestaEncuesta respuestaEncuesta) {
    	return respuestaEncuesta != null && this.respuestasEncuesta.contains(respuestaEncuesta);
    }
	
}
