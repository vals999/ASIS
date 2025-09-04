package model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import dao_interfaces.EliminableLogico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbTransient;
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
@Table(name = "JORNADAS")
@Schema(
	    description = "Representa una jornada en el sistema",
	    example = """
	        {
	            "fecha": "2024-03-20"
	        }
	        """
	)
public class Jornada implements EliminableLogico{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "fecha_jornada")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaCreacion;
	
	@Column(name = "fecha_editado", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEditado;
	
	@Column(name = "fecha_eliminacion", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEliminacion; 

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campaña_id")
    private Campaña campaña;

    @OneToMany(mappedBy = "jornada", fetch = FetchType.LAZY)
    private List <Encuesta> encuestas = new ArrayList<Encuesta>();
    
    
	public Jornada() {
		super();
	}

	public Jornada(Long id, LocalDate fecha, List<DatosPersonales> encuestadores, List<Zona> zonas, List<Encuesta> encuestas) {
		super();
		this.id = id;
		this.fecha = fecha;
		this.encuestas = encuestas;
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

	public Campaña getCampaña() {
		return campaña;
	}

	public void setCampaña(Campaña campaña) {
		this.campaña = campaña;
	}


	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
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

	public LocalDateTime getFechaEliminacion() {
		return fechaEliminacion;
	}

	public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
		this.fechaEliminacion = fechaEliminacion;
	}
	
	@Override
	public String toString() {
		return "Jornada [id=" + id + ", fecha=" + fecha + ", encuestas=" + encuestas + "]";
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
            encuesta.setJornada(this);
	    }
	}
	 

	public void quitarEncuesta(Encuesta encuesta) {
        if (encuesta != null) {
            this.encuestas.remove(encuesta);
            encuesta.setJornada(null);
        }
    }
	
	public boolean tieneEncuesta(Encuesta encuesta) {
	    return encuesta != null && this.encuestas.contains(encuesta);
	}
	
	
}
