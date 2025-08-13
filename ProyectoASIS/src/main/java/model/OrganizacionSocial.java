package model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dao_interfaces.EliminableLogico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORGANIZACIONES_SOCIALES")
public class OrganizacionSocial implements EliminableLogico{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "direccion")
    private String direccion;
	
	@Column(name = "actividad")
    private String actividad;
	
	@Column(name = "info_contacto")
    private String infoContacto;
    
	@Column(name = "fecha_creacion", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaCreacion;
	
	@Column(name = "fecha_editado", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEditado;
	
	@Column(name = "fecha_eliminacion", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEliminacion;

    @ManyToMany(mappedBy="organizacionesSociales", fetch = FetchType.LAZY)
    private List <Usuario> usuarios = new ArrayList<Usuario>();

    @ManyToMany(mappedBy="organizacionesSociales", fetch = FetchType.LAZY)
    private List <Barrio> barrios = new ArrayList<Barrio>();
    
	public OrganizacionSocial() {
		super();
	}

	public OrganizacionSocial(String nombre, String direccion, String actividad, String infoContacto) {
		super();
		this.nombre = nombre;
		this.direccion = direccion;
		this.actividad = actividad;
		this.infoContacto = infoContacto;
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

	public String getInfoContacto() {
		return infoContacto;
	}

	public void setInfoContacto(String infoContacto) {
		this.infoContacto = infoContacto;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Barrio> getBarrios() {
		return barrios;
	}

	public void setBarrios(List<Barrio> barrios) {
		this.barrios = barrios;
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
	
	public void agregarUsuario(Usuario usuario) {
        if (usuario != null && !this.usuarios.contains(usuario)) {
        	this.usuarios.add(usuario);
        	if (!usuario.getOrganizacionesSociales().contains(this)) {
        		usuario.getOrganizacionesSociales().add(this);
            }
        }
    }

    public void quitarUsuario(Usuario usuario) {
        if (usuario != null && this.usuarios.remove(usuario)) {
        	usuario.getOrganizacionesSociales().remove(this);
        }
    }

    public boolean tieneCampaña(Usuario usuario) {
    	return usuario != null && this.usuarios.contains(usuario);
    }
    
    public void agregarBarrio(Barrio barrio) {
        if (barrio != null && !this.barrios.contains(barrio)) {
        	this.barrios.add(barrio);
        	if (!barrio.getOrganizacionesSociales().contains(this)) {
        		barrio.getOrganizacionesSociales().add(this);
            }
        }
    }

    public void quitarBarrio(Barrio barrio) {
        if (barrio != null && this.barrios.remove(barrio)) {
        	barrio.getOrganizacionesSociales().remove(this);
        }
    }

    public boolean tieneBarrio(Barrio barrio) {
        return barrio != null && this.barrios.contains(barrio);
    }

	@Override
	public String toString() {
		return "OrganizacionSocial [nombre=" + nombre + ", direccion=" + direccion + ", actividad=" + actividad
				+ ", infoContacto=" + infoContacto + "]";
	}
    
}
