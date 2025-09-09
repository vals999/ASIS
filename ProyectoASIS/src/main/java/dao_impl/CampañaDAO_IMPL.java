package dao_impl;

import java.time.LocalDateTime;

import dao_interfaces.I_CampañaDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityTransaction;
import model.Campaña;
import model.Jornada;

@RequestScoped
public class CampañaDAO_IMPL extends GenericDAO_IMPL<Campaña, Long> implements I_CampañaDAO  {

	public CampañaDAO_IMPL() {
		super(Campaña.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void eliminar(Long id) {
	    EntityTransaction tx = em.getTransaction();    
	    try {
	        tx.begin();        
	        // Buscar la campaña con sus jornadas
	        Campaña campaña = em.createQuery(
	            "SELECT c FROM Campaña c LEFT JOIN FETCH c.jornadas WHERE c.id = :id", 
	            Campaña.class)
	            .setParameter("id", id)
	            .getSingleResult();
	            
	        if (campaña != null) {
	            // Primero eliminar lógicamente las jornadas asociadas
	            if (campaña.getJornada() != null) {
	                for (Jornada jornada : campaña.getJornada()) {
	                    if (jornada.getFechaEliminacion() == null) {
	                        jornada.setFechaEliminacion(LocalDateTime.now());
	                        em.merge(jornada);
	                    }
	                }
	            }	          
	            // Luego eliminar lógicamente la campaña
	            campaña.setFechaEliminacion(LocalDateTime.now());
	            em.merge(campaña);
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
	        // Buscar la campaña con sus jornadas
	        Campaña campaña = em.createQuery(
	            "SELECT c FROM Campaña c LEFT JOIN FETCH c.jornadas WHERE c.id = :id", 
	            Campaña.class)
	            .setParameter("id", id)
	            .getSingleResult();	           
	        if (campaña != null) {
	            // Primero recuperar las jornadas asociadas
	            if (campaña.getJornada() != null) {
	                for (Jornada jornada : campaña.getJornada()) {
	                    if (jornada.getFechaEliminacion() != null) {
	                        jornada.setFechaEliminacion(null);
	                        em.merge(jornada);
	                    }
	                }
	            }	            
	            // Luego recuperar la campaña
	            campaña.setFechaEliminacion(null);
	            em.merge(campaña);
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
