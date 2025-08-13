package dao_impl;

import java.time.LocalDateTime;

import dao_interfaces.I_BarrioDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityTransaction;
import model.Barrio;
import model.Zona;

@RequestScoped
public class BarrioDAO_IMPL extends GenericDAO_IMPL<Barrio, Long> implements I_BarrioDAO{
	
	public BarrioDAO_IMPL() {
		super(Barrio.class);
	}
	
	@Override
	public void eliminar(Long id) {
	    EntityTransaction tx = em.getTransaction();    
	    try {
	        tx.begin();        
	        // Buscar el barrio con sus zonas
	        Barrio barrio = em.createQuery(
	            "SELECT b FROM Barrio b LEFT JOIN FETCH b.zonas WHERE b.id = :id", 
	            Barrio.class)
	            .setParameter("id", id)
	            .getSingleResult();
	            
	        if (barrio != null) {
	            // Primero eliminar lógicamente las zonas asociadas
	            if (barrio.getZonas() != null) {
	                for (Zona zona : barrio.getZonas()) {
	                    if (zona.getFechaEliminacion() == null) {
	                        zona.setFechaEliminacion(LocalDateTime.now());
	                        em.merge(zona);
	                    }
	                }
	            }	          
	            // Luego eliminar lógicamente el barrio
	            barrio.setFechaEliminacion(LocalDateTime.now());
	            em.merge(barrio);
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
	        // Buscar el barrio con sus zonas
	        Barrio barrio = em.createQuery(
	            "SELECT b FROM Barrio b LEFT JOIN FETCH b.zonas WHERE b.id = :id", 
	            Barrio.class)
	            .setParameter("id", id)
	            .getSingleResult();	           
	        if (barrio != null) {
	            // Primero eliminar lógicamente las zonas asociadas
	            if (barrio.getZonas() != null) {
	                for (Zona zona : barrio.getZonas()) {
	                    if (zona.getFechaEliminacion() != null) {
	                        zona.setFechaEliminacion(null);
	                        em.merge(zona);
	                    }
	                }
	            }	            
	            // Luego eliminar lógicamente el barrio
	            barrio.setFechaEliminacion(null);
	            em.merge(barrio);
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
