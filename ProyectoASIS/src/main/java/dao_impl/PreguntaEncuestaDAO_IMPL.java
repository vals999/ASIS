package dao_impl;

import dao_interfaces.I_PreguntaEncuestaDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.PreguntaEncuesta;

@RequestScoped
public class PreguntaEncuestaDAO_IMPL extends GenericDAO_IMPL<PreguntaEncuesta, Long> implements I_PreguntaEncuestaDAO{
	
	public PreguntaEncuestaDAO_IMPL() {
		super(PreguntaEncuesta.class);
	}

	@Override
    public PreguntaEncuesta findByPreguntaCsv(String preguntaCsv) {
        try {
            TypedQuery<PreguntaEncuesta> query = em.createQuery(
                "SELECT p FROM PreguntaEncuesta p WHERE p.preguntaCsv = :csv", PreguntaEncuesta.class);
            query.setParameter("csv", preguntaCsv);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
	
}
