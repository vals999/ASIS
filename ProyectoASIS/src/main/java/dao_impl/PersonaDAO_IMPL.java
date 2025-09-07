package dao_impl;

import dao_interfaces.I_PersonaDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.DatosPersonales;

@RequestScoped
public class PersonaDAO_IMPL extends GenericDAO_IMPL<DatosPersonales, Long> implements I_PersonaDAO{
	
	public PersonaDAO_IMPL() {
		super(DatosPersonales.class);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public DatosPersonales obtenerPorUsuarioId(Long usuarioId) {
		try {
			TypedQuery<DatosPersonales> query = em.createQuery(
				"SELECT dp FROM DatosPersonales dp WHERE dp.usuario.id = :usuarioId AND dp.fechaEliminacion IS NULL", 
				DatosPersonales.class);
			query.setParameter("usuarioId", usuarioId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
