package model;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "REPORTES")
public class Reporte implements EliminableLogico{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "nombre_reporte")
	private String nombre;
	@Column(name = "fecha")
	private Date fecha;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "usuario_creador")
	private Usuario creador;
	
	// Campos para almacenar archivos como BLOB
	@Lob
	@Column(name = "contenido_archivo")
	private byte[] contenidoArchivo;
	
	@Column(name = "tipo_mime")
	private String tipoMime;
	
	@Column(name = "tamano_archivo")
	private Long tamanoArchivo;
	
	@Column(name = "nombre_archivo_original")
	private String nombreArchivoOriginal;
	
	@Column(name = "fecha_creacion", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaCreacion;
	
	@Column(name = "fecha_editado", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEditado;
	
	@Column(name = "fecha_eliminacion", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEliminacion;
	
	/* relación Reporte-Usuario */
	@ManyToMany(mappedBy="reportes", fetch = FetchType.LAZY)
	private List<Usuario> usuarios = new ArrayList<Usuario>();
 
	public Reporte() {
		super();
	}

	public Reporte(Long id, String nombre, Date fecha, Usuario creador, List<Usuario> usuariosCompartidos) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.fecha = fecha;
		this.creador = creador;
		this.usuarios = usuariosCompartidos;
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

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Usuario getCreador() {
		return creador;
	}

	public void setCreador(Usuario creador) {
		this.creador = creador;
	}

	public byte[] getContenidoArchivo() {
		return contenidoArchivo;
	}

	public void setContenidoArchivo(byte[] contenidoArchivo) {
		this.contenidoArchivo = contenidoArchivo;
	}

	public String getTipoMime() {
		return tipoMime;
	}

	public void setTipoMime(String tipoMime) {
		this.tipoMime = tipoMime;
	}

	public Long getTamanoArchivo() {
		return tamanoArchivo;
	}

	public void setTamanoArchivo(Long tamanoArchivo) {
		this.tamanoArchivo = tamanoArchivo;
	}

	public String getNombreArchivoOriginal() {
		return nombreArchivoOriginal;
	}

	public void setNombreArchivoOriginal(String nombreArchivoOriginal) {
		this.nombreArchivoOriginal = nombreArchivoOriginal;
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
	
	public List<Usuario> getUsuarios() {
		return usuarios;
	}
	    
    public void setUsuarios(List<Usuario> usuario) {
        this.usuarios = usuario;
    }
	
	
	public void agregarUsuario(Usuario usuario) {
        if (usuario != null && !this.usuarios.contains(usuario)) {
            this.usuarios.add(usuario);
            if (!usuario.getReportes().contains(this)) {
                usuario.getReportes().add(this);
	        }
	    }
	}
	 
	public void quitarUsuario(Usuario usuario) {
        if (usuario != null) {
            this.usuarios.remove(usuario);
            usuario.getReportes().remove(this);
        }
    }
	
	public boolean tieneUsuario(Usuario usuario) {
	    return usuario != null && this.usuarios.contains(usuario);
	}

	@Override
	public String toString() {
		return "Reporte [id=" + id + ", nombre=" + nombre + ", fecha=" + fecha + ", creador=" + creador
				+ ", usuariosCompartidos=" + usuarios + "]";
	}
	 
}