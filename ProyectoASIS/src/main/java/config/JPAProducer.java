package config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class JPAProducer {

	// Ahora solo declaro la variable, no la inicializo
	private EntityManagerFactory emf;
	
	/* El contenedor CDI llamará a este método automáticamente 
	 * DESPUÉS de construir la instancia JPAProducer 
	 */
	@PostConstruct
	public void init() {
		this.emf = Persistence.createEntityManagerFactory("miUP");
	}
	
	/*
	 * El contenedor CDI llamará a este método automáticamente
	 * ANTES de destruir la instancia (ej. al apagar el servidor).
     */
    @PreDestroy
    public void destroy() {
        if (this.emf != null && this.emf.isOpen()) {
            this.emf.close();
        }
    }
    
    /*
     * Este método productor no cambia. Sigue creando un EntityManager
     * por cada solicitud HTTP. Ahora usa la variable de instancia 'emf'.
     */
    @Produces
    @RequestScoped
    public EntityManager createEntityManager() {
        if (this.emf == null) {
            throw new IllegalStateException("El EntityManagerFactory no ha sido inicializado.");
        }
        return this.emf.createEntityManager();
    }

    /*
     * Este método 'disposer' tampoco cambia. Sigue cerrando el EntityManager
     * cuando la solicitud HTTP termina.
     */
    public void closeEntityManager(@Disposes EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}
