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
public class JpaProducer {

    private EntityManagerFactory entityManagerFactory;

    /**
     * Initialisiert die EntityManagerFactory einmalig beim Start der Anwendung.
     */
    @PostConstruct
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("likeHeroToZeroPU");
    }

    /**
     * Erzeugt pro HTTP-Request einen eigenen EntityManager.
     */
    @Produces
    @RequestScoped
    public EntityManager produceEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Schließt den EntityManager am Ende des Request-Scopes.
     */
    public void closeEntityManager(@Disposes EntityManager entityManager) {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

    /**
     * Schließt die EntityManagerFactory beim Beenden der Anwendung.
     */
    @PreDestroy
    public void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}