package dao_impl;

import	java.util.List;
import dao_interfaces.I_UsuarioDAO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.Usuario;
@ApplicationScoped 
public class UsuarioDAO_IMPL extends GenericDAO_IMPL<Usuario, Long> implements I_UsuarioDAO{

	// Implemento los métodos específicos de Usuario
    // Los métodos genéricos ya están implementados en la clase padre
	
	public UsuarioDAO_IMPL() {
		super(Usuario.class);
		// TODO Auto-generated constructor stub
	}
	
	@Inject
	protected EntityManager em;
	
	@Override
	public Usuario obtenerPorNombreUsuario(String nombreUsuario) {
		try {
			TypedQuery<Usuario> query = em.createQuery(
				"SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario", Usuario.class);
			query.setParameter("nombreUsuario", nombreUsuario);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public Usuario obtenerPorEmail(String email) {
		try {
			TypedQuery<Usuario> query = em.createQuery(
				"SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class);
			query.setParameter("email", email);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}


	@Override
	public List<Usuario> obtenerUsuariosPendientes() {
		TypedQuery<Usuario> query = em.createQuery(
			"SELECT u FROM Usuario u WHERE u.habilitado = false ORDER BY u.fechaCreacion DESC", Usuario.class);
		return query.getResultList();
	}
	
}
