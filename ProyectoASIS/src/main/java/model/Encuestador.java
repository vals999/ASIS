package model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dao_interfaces.EliminableLogico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "ENCUESTADORES")
@Schema(
	    description = "Representa un encuestador en el sistema",
	    example = """
	        {
	            "ocupacion": "Estudiante de Sociología"
	        }
	        """
	)
public class Encuestador  implements EliminableLogico{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(hidden = true)  // Oculta el ID en la documentación
	private Long id;
	
	@OneToOne(mappedBy="encuestador", fetch = FetchType.EAGER)
	private DatosPersonales datosPersonales;
	
	@Column(name = "ocupacion")
	private String ocupacion;
	
	@ManyToMany(mappedBy="encuestador", fetch = FetchType.LAZY)
	private List<Campaña> campañas = new ArrayList<Campaña>();

	@OneToOne(optional=true, fetch = FetchType.EAGER)
	@JoinColumn(name="usuario_id")
	private Usuario usuario;
	
	@Column(name = "fecha_creacion", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
	@Schema(hidden = true)
	private LocalDateTime fechaCreacion;
	
	@Column(name = "fecha_editado", columnDefinition = "TIMESTAMP(0)")
	@Schema(hidden = true)
	private LocalDateTime fechaEditado;
	
	@Column(name = "fecha_eliminacion", columnDefinition = "TIMESTAMP(0)")
	@Schema(hidden = true)
	private LocalDateTime fechaEliminacion;
	
	public Encuestador() {
		super();
	}

	public Encuestador(String ocupacion) {
		super();
		this.ocupacion = ocupacion;
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

	public String getOcupacion() {
		return ocupacion;
	}

	public void setOcupacion(String ocupacion) {
		this.ocupacion = ocupacion;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public List<Campaña> getCampaña() {
		return campañas;
	}
	    
    public void setCampaña(List<Campaña> campaña) {
        this.campañas = campaña;
    }
	
	
	public void agregarCampaña(Campaña campaña) {
        if (campaña != null && !this.campañas.contains(campaña)) {
            this.campañas.add(campaña);
            if (!campaña.getEncuestador().contains(this)) {
                campaña.getEncuestador().add(this);
	        }
	    }
	}
	 
	public void quitarCampaña(Campaña campaña) {
        if (campaña != null) {
            this.campañas.remove(campaña);
            campaña.getEncuestador().remove(this);
        }
    }
	
	public boolean tieneCampaña(Campaña campaña) {
	    return campaña != null && this.campañas.contains(campaña);
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

	public DatosPersonales getDatosPersonales() {
		return datosPersonales;
	}

	public void setDatosPersonales(DatosPersonales datosPersonales) {
		this.datosPersonales = datosPersonales;
	}
	
}
