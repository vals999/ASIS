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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;


@Entity
@Table (name = "USUARIOS")
@Schema(
	    description = "Representa un usuario en el sistema",
	    example = """
	        {
	            "nombreUsuario": "juan.perez",
	            "contrasena": "contraseña123",
	            "email": "juan.perez@example.com",
	            "habilitado": true,
	            "perfil": "ADMINISTRADOR"
	        }
	        """
	)
public class Usuario implements EliminableLogico{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(mappedBy="usuario", fetch = FetchType.EAGER)
	private DatosPersonales datosPersonales;
	
	@Column(name = "nombre_usuario")
	private String nombreUsuario;
	
	@Column(name = "contrasena")
    private String contrasena;
    
	@Column(name = "email")
    private String email;
    
	@Column(name = "habilitado")
    private boolean habilitado;
    
	@Column(name = "perfil")
    private Perfil perfil;
	    
    @OneToOne(mappedBy="usuario", optional = true, fetch = FetchType.EAGER)
    private Encuestador escuestador;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_campaña", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn (name = "campaña_id"))
    private List<Campaña> campañas = new ArrayList<Campaña>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_barrios", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn (name = "barrio_id"))
    private List<Barrio> barrios = new ArrayList<Barrio>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_orgsocial", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn (name = "org_social_id"))
    private List<OrganizacionSocial> organizacionesSociales = new ArrayList<OrganizacionSocial>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_reporte", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn (name = "reporte_id"))
    private List<Reporte> reportes = new ArrayList<Reporte>(); 
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaCreacion;
	
	@Column(name = "fecha_editado", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEditado;
	
	@Column(name = "fecha_eliminacion", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEliminacion;
    
	public Usuario() {
		super();
	}

	public Usuario(int id, String nombreUsuario, String contrasena, String email, boolean habilitado, Perfil perfil) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.contrasena = contrasena;
		this.email = email;
		this.habilitado = habilitado;
		this.perfil = perfil;
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

	public DatosPersonales getDatosPersonales() {
		return datosPersonales;
	}

	public void setDatosPersonales(DatosPersonales datosPersonales) {
		this.datosPersonales = datosPersonales;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getContrasena() {
		return contrasena;
	}
	
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	/**
	 * @return the fechaCreacion
	 */
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	/**
	 * @param fechaCreacion the fechaCreacion to set
	 */
	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	/**
	 * @return the fechaEditado
	 */
	public LocalDateTime getFechaEditado() {
		return fechaEditado;
	}

	/**
	 * @param fechaEditado the fechaEditado to set
	 */
	public void setFechaEditado(LocalDateTime fechaEditado) {
		this.fechaEditado = fechaEditado;
	}

	/**
	 * @return the fechaEliminacion
	 */
	public LocalDateTime getFechaEliminacion() {
		return fechaEliminacion;
	}

	/**
	 * @param fechaEliminacion the fechaEliminacion to set
	 */
	public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
		this.fechaEliminacion = fechaEliminacion;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}


	public Encuestador getEscuestador() {
		return escuestador;
	}

	public void setEscuestador(Encuestador escuestador) {
		this.escuestador = escuestador;
	}

	public List<Campaña> getCampañas() {
		return campañas;
	}
	
	
	public void setCampañas(List<Campaña> campañas) {
		this.campañas = campañas;
	}

	public List<Barrio> getBarrios() {
		return barrios;
	}

	public void setBarrios(List<Barrio> barrios) {
		this.barrios = barrios;
	}

	public List<OrganizacionSocial> getOrganizacionesSociales() {
		return organizacionesSociales;
	}

	public void setOrganizacionesSociales(List<OrganizacionSocial> organizacionesSociales) {
		this.organizacionesSociales = organizacionesSociales;
	}

	public List<Reporte> getReportes() {
		return reportes;
	}

	public void setReportes(List<Reporte> reportes) {
		this.reportes = reportes;
	}

	// Métodos para campañas
	public void agregarCampaña(Campaña campaña) {
        if (campaña != null && !this.campañas.contains(campaña)) {
            this.campañas.add(campaña);
            if (!campaña.getUsuarios().contains(this)) {
            	campaña.getUsuarios().add(this);
            }
        }
    }

    public void quitarCampaña(Campaña campaña) {
        if (campaña != null && this.campañas.remove(campaña)) {
            campaña.getUsuarios().remove(this);
        }
    }

    public boolean tieneCampaña(Campaña campaña) {
    	return campaña != null && this.campañas.contains(campaña);
    }

    // Métodos para barrios
    public void agregarBarrio(Barrio barrio) {
        if (barrio != null && !this.barrios.contains(barrio)) {
            this.barrios.add(barrio);
            if (!barrio.getUsuarios().contains(this)) {
            	barrio.getUsuarios().add(this);
            }
        }
    }

    public void quitarBarrio(Barrio barrio) {
        if (barrio != null && this.barrios.remove(barrio)) {
            barrio.getUsuarios().remove(this);
        }
    }

    public boolean tieneBarrio(Barrio barrio) {
    	return barrio != null && this.barrios.contains(barrio);
    }

    // Métodos para organizaciones sociales
    public void agregarOrganizacionSocial(OrganizacionSocial org) {
        if (org != null && !this.organizacionesSociales.contains(org)) {
        	this.organizacionesSociales.add(org);
        	if (!org.getUsuarios().contains(this)) {
                org.getUsuarios().add(this);
            }
        }
    }

    public void quitarOrganizacionSocial(OrganizacionSocial org) {
        if (org != null && this.organizacionesSociales.remove(org)) {
            org.getUsuarios().remove(this);
        }
    }

    public boolean tieneOrganizacionSocial(OrganizacionSocial org) {
    	return org != null && this.organizacionesSociales.contains(org);
    }

    // Métodos para reportes
    public void agregarReporte(Reporte reporte) {
        if (reporte != null && !this.reportes.contains(reporte)) {
        	this.reportes.add(reporte);
        	if (!reporte.getUsuarios().contains(this)) {
        		reporte.getUsuarios().add(this);
            }            
        }
    }

    public void quitarReporte(Reporte reporte) {
        if (reporte != null && this.reportes.remove(reporte)) {
            reporte.getUsuarios().remove(this);
        }
    }

    public boolean tieneReporte(Reporte reporte) {
    	return reporte != null && this.reportes.contains(reporte);
    }
	
	@Override
	public String toString() {
		return "Usuario [nombreUsuario=" + nombreUsuario + ", contraseña=" + contrasena + ", email=" + email
				+ ", habilitado=" + habilitado + ", perfil=" + perfil + ", getNombre()=" + datosPersonales.getNombre()
				+ ", getApellido()=" + datosPersonales.getApellido() + ", getDni()=" + datosPersonales.getDni() + "]";
	}	
    
}