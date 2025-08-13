package model;

import java.time.LocalDateTime;
import java.util.List;

import dao_interfaces.EliminableLogico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity	
@Table(name = "PERSONAS_ENCUESTADA")
public class PersonaEncuestada implements EliminableLogico{

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(mappedBy="personaEncuestada", fetch = FetchType.EAGER)
	private DatosPersonales datosPersonales;
	
	@Column(name = "tiene_obra_social")
	private boolean tieneObraSocial;

	@ManyToMany(mappedBy = "personasEncuestadas", fetch = FetchType.LAZY)	
	private List<Encuesta> encuestas;
	
	@Column(name = "fecha_creacion", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaCreacion;
	
	@Column(name = "fecha_editado", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEditado;
	
	@Column(name = "fecha_eliminacion", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEliminacion;
	
	public PersonaEncuestada() {
		super();
	}

	public PersonaEncuestada(boolean tieneObraSocial) {
		super();
		this.tieneObraSocial = tieneObraSocial;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isTieneObraSocial() {
		return tieneObraSocial;
	}

	public void setTieneObraSocial(boolean tieneObraSocial) {
		this.tieneObraSocial = tieneObraSocial;
	}
	
	public List<Encuesta> getEncuestas() {
		return encuestas;
	}
	
	public void setEncuestas(List<Encuesta> encuestas) {
		this.encuestas = encuestas;
	}
	
	public void agregarEncuesta(Encuesta encuesta) {
        if (encuesta != null && !this.encuestas.contains(encuesta)) {
            this.encuestas.add(encuesta);
            if (!encuesta.getPersonasEncuestadas().contains(this)) {
            	encuesta.getPersonasEncuestadas().add(this);
            }
        }
    }

    public void quitarEncuesta(Encuesta encuesta) {
        if (encuesta != null && this.encuestas.remove(encuesta)) {
        	encuesta.getPersonasEncuestadas().remove(this);
        }
    }

    public boolean tieneEncuesta(Encuesta encuesta) {
    	return encuesta != null && this.encuestas.contains(encuesta);
    }

	public DatosPersonales getDatosPersonales() {
		return datosPersonales;
	}

	public void setDatosPersonales(DatosPersonales datosPersonales) {
		this.datosPersonales = datosPersonales;
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
	
}
