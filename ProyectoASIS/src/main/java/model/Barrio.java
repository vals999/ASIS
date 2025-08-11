package model;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "BARRIOS")
@Schema(
	    description = "Representa un barrio en el sistema",
	    example = """
	        {
	            "nombre": "Centro",
	            "geolocalización": "Barrio céntrico de la ciudad"
	        }
	        """
	)
public class Barrio implements EliminableLogico{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nombre")
    private String nombre;
	@Column(name = "geolocalizacion")
    private String geolocalizacion;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaCreacion;
	
	@Column(name = "fecha_editado", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEditado;
	
	@Column(name = "fecha_eliminacion", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEliminacion;
    
    @OneToMany(mappedBy = "barrio",  fetch = FetchType.LAZY)
    private List<Zona> zonas = new ArrayList<Zona>();
    
    @ManyToMany(mappedBy = "barrios",fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<Usuario>();
    
    @OneToMany(mappedBy = "barrio",fetch = FetchType.LAZY)
    private List<Campaña> campañas = new ArrayList<Campaña>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "barrio_orgsocial", joinColumns = @JoinColumn(name = "barrio_id"), inverseJoinColumns = @JoinColumn (name = "org_social_id"))
    private List<OrganizacionSocial> organizacionesSociales = new ArrayList<OrganizacionSocial>();
    
	public Barrio() {
		super();
	}

	public Barrio(Long id, String nombre, String geolocalizacion, List<Zona> zonas) {
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
	
	
	public List<OrganizacionSocial> getOrganizacionesSociales() {
		return organizacionesSociales;
	}
	    
    public void setOrganizacionesSociales(List<OrganizacionSocial> organizacionesSociales) {
        this.organizacionesSociales = organizacionesSociales;
    }
	
	
	public void agregarOrganizacionSocial(OrganizacionSocial organizacion) {
        if (organizacion != null && !this.organizacionesSociales.contains(organizacion)) {
            this.organizacionesSociales.add(organizacion);
            if (!organizacion.getBarrios().contains(this)) {
                organizacion.getBarrios().add(this);
	        }
	    }
	}
	 
	public void quitarOrganizacionSocial(OrganizacionSocial organizacion) {
        if (organizacion != null) {
            this.organizacionesSociales.remove(organizacion);
            organizacion.getBarrios().remove(this);
        }
    }
	
	public boolean tieneOrganizacionSocial(OrganizacionSocial organizacion) {
	    return organizacion != null && this.organizacionesSociales.contains(organizacion);
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
            if (!usuario.getBarrios().contains(this)) {
                usuario.getBarrios().add(this);
	        }
	    }
	}
	 
	public void quitarUsuario(Usuario usuario) {
        if (usuario != null) {
            this.usuarios.remove(usuario);
            usuario.getBarrios().remove(this);
        }
    }
	
	public boolean tieneUsuario(Usuario usuario) {
	    return usuario != null && this.usuarios.contains(usuario);
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
            campaña.setBarrio(this);
	    }
	}
	 
	public void quitarCampaña(Campaña campaña) {
        if (campaña != null) {
            this.campañas.remove(campaña);
            campaña.setBarrio(null);
        }
    }
	
	public boolean tieneCampaña(Campaña campaña) {
	    return campaña != null && this.campañas.contains(campaña);
	}
	
	
	public List<Zona> getZonas() {
		return zonas;
	}
	    
    public void setZona(List<Zona> zona) {
        this.zonas = zona;
    }
	
	
	public void agregarZona(Zona zona) {
        if (zona != null && !this.zonas.contains(zona)) {
            this.zonas.add(zona);
            zona.setBarrio(this);
	    }
	}
	 
	public void quitarZona(Zona zona) {
        if (zona != null) {
            this.zonas.remove(zona);
            zona.setBarrio(null);
        }
    }
	
	public boolean tieneZona(Zona zona) {
	    return zona != null && this.zonas.contains(zona);
	}
}
