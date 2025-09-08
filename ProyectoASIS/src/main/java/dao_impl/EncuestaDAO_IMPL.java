package dao_impl;

import dao_interfaces.I_EncuestaDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.Encuesta;

@RequestScoped
public class EncuestaDAO_IMPL extends GenericDAO_IMPL<Encuesta, Long> implements I_EncuestaDAO{

	public EncuestaDAO_IMPL() {
		super(Encuesta.class);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Encuesta findByIdExterno(String idExterno) {
		if (idExterno == null || idExterno.trim().isEmpty()) {
			return null;
		}
		
		try {
			TypedQuery<Encuesta> query = em.createQuery(
				"SELECT e FROM Encuesta e WHERE e.idExterno = :idExterno AND e.fechaEliminacion IS NULL", 
				Encuesta.class
			);
			query.setParameter("idExterno", idExterno.trim());
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
}
