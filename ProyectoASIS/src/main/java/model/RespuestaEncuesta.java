package model;

import java.time.LocalDateTime;

import dao_interfaces.EliminableLogico;
import jakarta.persistence.*;

@Entity
@Table(name = "RESPUESTAS_ENCUESTA")
public class RespuestaEncuesta implements EliminableLogico{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "valor_respuesta", columnDefinition = "TEXT")
	private String valor;
	
	@Column(name = "fecha_creacion", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaCreacion;
	
	@Column(name = "fecha_editado", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEditado;
	
	@Column(name = "fecha_eliminacion", columnDefinition = "TIMESTAMP(0)")
	private LocalDateTime fechaEliminacion;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "preguntaEncuesta_id")
    private PreguntaEncuesta preguntaEncuesta;
    
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "encuesta_id")
    private Encuesta encuesta;
    
	public RespuestaEncuesta() {
		super();
	}

	public RespuestaEncuesta(Long id, PreguntaEncuesta pregunta, Encuesta encuesta, String valor) {
		super();
		this.id = id;
		this.preguntaEncuesta = pregunta;
		this.encuesta = encuesta;
		this.valor = valor;
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

	public PreguntaEncuesta getPregunta() {
		return preguntaEncuesta;
	}

	public void setPregunta(PreguntaEncuesta pregunta) {
		this.preguntaEncuesta = pregunta;
	}

	public Encuesta getEncuesta() {
		return encuesta;
	}

	public void setEncuesta(Encuesta encuesta) {
		this.encuesta = encuesta;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
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
