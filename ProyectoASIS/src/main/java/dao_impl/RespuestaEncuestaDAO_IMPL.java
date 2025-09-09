package dao_impl;

import dao_interfaces.I_RespuestaEncuestaDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.Query;
import model.RespuestaEncuesta;
import java.util.List;

@RequestScoped
public class RespuestaEncuestaDAO_IMPL extends GenericDAO_IMPL<RespuestaEncuesta, Long> implements I_RespuestaEncuestaDAO{
	
	public RespuestaEncuestaDAO_IMPL() {
		super(RespuestaEncuesta.class);
	}

	@Override
	public List<RespuestaEncuesta> obtenerNoBorrados() {
		String jpql = "SELECT r FROM RespuestaEncuesta r WHERE r.fechaEliminacion IS NULL";
		Query query = em.createQuery(jpql, RespuestaEncuesta.class);
		return query.getResultList();
	}

	@Override
	public List<String> obtenerRespuestasUnicasPorPreguntaCodigo(String preguntaCodigo) {
		String jpql = "SELECT DISTINCT r.valor FROM RespuestaEncuesta r " +
					  "INNER JOIN r.preguntaEncuesta p " +
					  "WHERE p.preguntaCsv = :preguntaCodigo " +
					  "AND r.fechaEliminacion IS NULL " +
					  "AND r.valor IS NOT NULL " +
					  "AND r.valor != '' " +
					  "ORDER BY r.valor";
		
		Query query = em.createQuery(jpql, String.class);
		query.setParameter("preguntaCodigo", preguntaCodigo);
		return query.getResultList();
	}

	@Override
	public List<RespuestaEncuesta> obtenerRespuestasPorPreguntaCodigo(String preguntaCodigo) {
		String jpql = "SELECT r FROM RespuestaEncuesta r " +
					  "INNER JOIN r.preguntaEncuesta p " +
					  "WHERE p.preguntaCsv = :preguntaCodigo " +
					  "AND r.fechaEliminacion IS NULL " +
					  "ORDER BY r.id";
		
		Query query = em.createQuery(jpql, RespuestaEncuesta.class);
		query.setParameter("preguntaCodigo", preguntaCodigo);
		return query.getResultList();
	}

	@Override
	public RespuestaEncuesta obtenerRespuestaPorEncuestaYPregunta(Long encuestaId, Long preguntaId) {
		String jpql = "SELECT r FROM RespuestaEncuesta r " +
					  "WHERE r.encuesta.id = :encuestaId " +
					  "AND r.preguntaEncuesta.id = :preguntaId " +
					  "AND r.fechaEliminacion IS NULL";
		
		Query query = em.createQuery(jpql, RespuestaEncuesta.class);
		query.setParameter("encuestaId", encuestaId);
		query.setParameter("preguntaId", preguntaId);
		
		List<RespuestaEncuesta> resultados = query.getResultList();
		return resultados.isEmpty() ? null : resultados.get(0);
	}

	@Override
	public RespuestaEncuesta obtenerRespuestaPorEncuestaYPreguntaCsv(Long encuestaId, String preguntaCsv) {
		String jpql = "SELECT r FROM RespuestaEncuesta r " +
					  "WHERE r.encuesta.id = :encuestaId " +
					  "AND r.preguntaEncuesta.preguntaCsv = :preguntaCsv " +
					  "AND r.fechaEliminacion IS NULL";
		
		Query query = em.createQuery(jpql, RespuestaEncuesta.class);
		query.setParameter("encuestaId", encuestaId);
		query.setParameter("preguntaCsv", preguntaCsv);
		
		List<RespuestaEncuesta> resultados = query.getResultList();
		return resultados.isEmpty() ? null : resultados.get(0);
	}
}
