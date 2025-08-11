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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "PREGUNTAS_ENCUESTA")
public class PreguntaEncuesta implements EliminableLogico{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "texto_pregunta")
    private String texto;
    
	@Column(name = "fecha_creacion", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaCreacion;
	
	@Column(name = "fecha_editado", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEditado;
	
	@Column(name = "fecha_eliminacion", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEliminacion;
	
    @OneToMany(mappedBy = "preguntaEncuesta", fetch = FetchType.LAZY)
    private List<RespuestaEncuesta> respuestasEncuesta;
    
    private TipoCategoria categoria;
    private TipoRespuesta tipoRespuesta;
    
	public PreguntaEncuesta() {
		super();
	}

	public PreguntaEncuesta(Long id, String texto, TipoCategoria categoria, TipoRespuesta tipoRespuesta) {
		super();
		this.id = id;
		this.texto = texto;
		this.categoria = categoria;
		this.tipoRespuesta = tipoRespuesta;
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

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public TipoCategoria getCategoria() {
		return categoria;
	}

	public void setCategoria(TipoCategoria categoria) {
		this.categoria = categoria;
	}

	public TipoRespuesta getTipoRespuesta() {
		return tipoRespuesta;
	}

	public void setTipoRespuesta(TipoRespuesta tipoRespuesta) {
		this.tipoRespuesta = tipoRespuesta;
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
	
	 public void agregarRespuestaEncuesta(RespuestaEncuesta respuestaEncuesta) {
		 if (respuestaEncuesta != null && !this.respuestasEncuesta.contains(respuestaEncuesta)) {
			 this.respuestasEncuesta.add(respuestaEncuesta);
			 respuestaEncuesta.setPregunta(this);   
		 }
	 }

	 public void quitarRespuestaEncuesta(RespuestaEncuesta respuestaEncuesta) {
		 if (respuestaEncuesta != null && this.respuestasEncuesta.remove(respuestaEncuesta)) {
			 respuestaEncuesta.setPregunta(null);
		 }
	 }

	 public boolean tieneRespuestaEncuesta(RespuestaEncuesta respuestaEncuesta) {
		 return respuestaEncuesta != null && this.respuestasEncuesta.contains(respuestaEncuesta);
	 }
	
}
