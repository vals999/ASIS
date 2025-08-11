package model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import dao_interfaces.EliminableLogico;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Entity
@Table (name = "CAMPAÑAS")
@Schema(
	    description = "Representa una campaña en el sistema",
	    example = """
	        {
	            "nombre": "Campaña de Salud 2024",
	            "fechaInicio": "2024-03-01",
	            "fechaFin": "2024-06-30"
	        }
	        """
	)
public class Campaña implements EliminableLogico{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name = "nombre")
    private String nombre;
	
	@Column(name = "fecha_inicio")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
	
	@Column(name = "fecha_fin")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
	
	@Column(name = "fecha_creacion", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaCreacion;
	
	@Column(name = "fecha_editado", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEditado;
	
	@Column(name = "fecha_eliminacion", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEliminacion;

    @OneToMany(mappedBy = "campaña", fetch = FetchType.EAGER)
    private List<Jornada> jornadas = new ArrayList<Jornada>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="barrio_id")
    private Barrio barrio;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ENCUESTADOR_CAMPAÑA", joinColumns = @JoinColumn(name="CAMP_ID"), inverseJoinColumns = @JoinColumn(name="ENC_ID"))
    private List<Encuestador> encuestador = new ArrayList<Encuestador>();

    @ManyToMany(mappedBy="campañas", fetch = FetchType.LAZY)
    private List <Usuario> usuarios = new ArrayList<Usuario>();

	public Campaña() {
		super();
	}

	public Campaña(Long id, String nombre, LocalDate fechaInicio, LocalDate fechaFin, Barrio barrio) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.barrio = barrio;
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

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Barrio getBarrio() {
		return barrio;
	}

	public void setBarrio(Barrio barrio) {
		this.barrio = barrio;
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
	
	public List<Jornada> getJornada() {
		return jornadas;
	}
	    
    public void setJornada(List<Jornada> jornada) {
        this.jornadas = jornada;
    }
	
	
	public void agregarJornada(Jornada jornada) {
        if (jornada != null && !this.jornadas.contains(jornada)) {
            this.jornadas.add(jornada);
            jornada.setCampaña(this);
	    }
	}
	 
	public void quitarJornada(Jornada jornada) {
        if (jornada != null) {
            this.jornadas.remove(jornada);
            jornada.setCampaña(null);
        }
    }
	
	public boolean tieneJornada(Jornada jornada) {
	    return jornada != null && this.jornadas.contains(jornada);
	}
	
	public List<Usuario> getUsuarios() {
		return usuarios;
	}
	    
    public void setUsuarios(List<Usuario> usuario) {
        this.usuarios = usuario;
    }
	
	
	public void agregarUsuario(Usuario usuario) {
        if (usuario != null && !this.usuarios.contains(usuario)) {
            this.usuarios.add(usuario);
            if (!usuario.getCampañas().contains(this)) {
                usuario.getCampañas().add(this);
	        }
	    }
	}
	 
	public void quitarUsuario(Usuario usuario) {
        if (usuario != null) {
            this.usuarios.remove(usuario);
            usuario.getCampañas().remove(this);
        }
    }
	
	public boolean tieneUsuario(Usuario usuario) {
	    return usuario != null && this.usuarios.contains(usuario);
	}
	
	
	public List<Encuestador> getEncuestador() {
		return encuestador;
	}
	    
    public void setEncuestador(List<Encuestador> enc) {
        this.encuestador = enc;
    }
	
	
	public void agregarEncuestador(Encuestador enc) {
        if (enc != null && !this.encuestador.contains(enc)) {
            this.encuestador.add(enc);
            if (!enc.getCampaña().contains(this)) {
                enc.getCampaña().add(this);
	        }
	    }
	}
	 
	public void quitarEncuestador(Encuestador enc) {
        if (enc != null) {
            this.encuestador.remove(enc);
            enc.getCampaña().remove(this);
        }
    }
	
	public boolean tieneEncuestador(Encuestador enc) {
	    return enc != null && this.encuestador.contains(enc);
	}
}
