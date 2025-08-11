package dao_impl;

import java.time.LocalDateTime;
import java.util.List;
import dao_interfaces.EliminableLogico;
import dao_interfaces.I_GenericDAO;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

public abstract class GenericDAO_IMPL<T extends EliminableLogico, ID> implements I_GenericDAO<T, ID> {

	// Implementaciones comunes que funcionan para TODAS las entidades

	private final Class<T> clasePersistente;

	@Inject
	protected EntityManager em;

	public GenericDAO_IMPL(Class<T> clasePersistente) {
		this.clasePersistente = clasePersistente;
	}
	
	public void crear(T entidad) {
		EntityTransaction tx = em.getTransaction();		
		try {
			tx.begin(); // INICIA la transacción
			em.persist(entidad); // Persiste la entidad
			tx.commit(); // CONFIRMA la transacción
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }		
	}

	@Override
	public T obtenerPorId(Long id) {
		return em.find(clasePersistente, id);
	}

	@Override
	public List<T> obtenerTodos() {
		String qlString = "SELECT e FROM " + clasePersistente.getSimpleName() + " e";
		TypedQuery<T> query = em.createQuery(qlString, clasePersistente); // garantiza que la lista devuelta sea de objetos T, con seguridad de tipos
		return query.getResultList();
	}

	public List<T> obtenerNoBorrados() {
		String qlString = "SELECT e FROM " + clasePersistente.getSimpleName() + " e WHERE e.fechaEliminacion IS NULL";
		TypedQuery<T> query = em.createQuery(qlString, clasePersistente); // garantiza que la lista devuelta sea de objetos T, con seguridad de tipos
		return query.getResultList();
	}
	
	@Override
	public void actualizar(T entidad) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(entidad);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
		
	}

	@Override
	public void eliminar(Long id) {

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entidad = em.find(clasePersistente, id);
            if (entidad != null) {
                entidad.setFechaEliminacion(LocalDateTime.now());
                em.merge(entidad);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

	@Override
	public void recuperar(Long id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entidad = em.find(clasePersistente, id);
            if (entidad != null) {
                entidad.setFechaEliminacion(null);
                em.merge(entidad);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
	}

}
