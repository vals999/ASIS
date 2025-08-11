package model;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import dao_interfaces.EliminableLogico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "ZONAS")
@Schema(
	    description = "Representa una zona en el sistema",
	    example = """
	        {
	            "nombre": "Zona Norte",
	            "geolocalizacion": "-34.6037, -58.3816"
	        }
	        """
	)
public class Zona implements Serializable, EliminableLogico{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "nombre_zona")
	private String nombre;
	
	@Column(name = "geolocalizacion")
    private String geolocalizacion;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaCreacion;
	
	@Column(name = "fecha_editado", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEditado;
	
	@Column(name = "fecha_eliminacion", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEliminacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="barrio_id")
    private Barrio barrio;

    @OneToMany(mappedBy = "zona", fetch = FetchType.LAZY)
    private List <Encuesta> encuestas = new ArrayList<Encuesta>();
    
    public Zona() {
		super();
	}

	public Zona(Long id, String nombre, String geolocalizacion) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.geolocalizacion = geolocalizacion;
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
	
	public Barrio getBarrio() {
		return barrio;
	}
		
	public void setBarrio(Barrio barrio) {
		this.barrio = barrio;
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

	public LocalDateTime getFechaEliminacion() {
		return fechaEliminacion;
	}

	public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
		this.fechaEliminacion = fechaEliminacion;
	}

	
	@Override
	public String toString() {
		return "Zona [id=" + id + ", nombre=" + nombre + ", geolocalizacion=" + geolocalizacion + "]";
	}

	
	public List<Encuesta> getEncuesta() {
		return encuestas;
	}
	    
    public void setEncuesta(List<Encuesta> encuesta) {
        this.encuestas = encuesta;
    }
	
	
	public void agregarEncuesta(Encuesta encuesta) {
        if (encuesta != null && !this.encuestas.contains(encuesta)) {
            this.encuestas.add(encuesta);
            encuesta.setZona(this);
	    }
	}
	 

	public void quitarEncuesta(Encuesta encuesta) {
        if (encuesta != null) {
            this.encuestas.remove(encuesta);
            encuesta.setZona(null);
        }
    }
	
	public boolean tieneEncuesta(Encuesta encuesta) {
	    return encuesta != null && this.encuestas.contains(encuesta);
	}
    
}
