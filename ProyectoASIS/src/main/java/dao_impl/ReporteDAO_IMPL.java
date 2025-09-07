package dao_impl;

import java.util.List;

import dao_interfaces.I_ReporteDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.TypedQuery;
import model.Reporte;
import model.TipoVisibilidad;

@RequestScoped
public class ReporteDAO_IMPL extends GenericDAO_IMPL<Reporte, Long> implements I_ReporteDAO  {

	public ReporteDAO_IMPL() {
		super(Reporte.class);
	}

	@Override
	public List<Reporte> obtenerReportesPublicos() {
		TypedQuery<Reporte> query = em.createQuery(
			"SELECT r FROM Reporte r WHERE r.visibilidad = :visibilidad AND r.fechaEliminacion IS NULL", 
			Reporte.class
		);
		query.setParameter("visibilidad", TipoVisibilidad.PUBLICO);
		return query.getResultList();
	}

	@Override
	public List<Reporte> obtenerReportesPrivados() {
		TypedQuery<Reporte> query = em.createQuery(
			"SELECT r FROM Reporte r WHERE r.visibilidad = :visibilidad AND r.fechaEliminacion IS NULL", 
			Reporte.class
		);
		query.setParameter("visibilidad", TipoVisibilidad.PRIVADO);
		return query.getResultList();
	}

	@Override
	public List<Reporte> obtenerPorVisibilidad(TipoVisibilidad visibilidad) {
		TypedQuery<Reporte> query = em.createQuery(
			"SELECT r FROM Reporte r WHERE r.visibilidad = :visibilidad AND r.fechaEliminacion IS NULL", 
			Reporte.class
		);
		query.setParameter("visibilidad", visibilidad);
		return query.getResultList();
	}
}
