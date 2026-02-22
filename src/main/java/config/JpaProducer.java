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

    private EntityManagerFactory emf;

    /**
     * Wird einmal beim Start der Anwendung ausgeführt.
     * Erstellt die EntityManagerFactory (thread-safe).
     */
    @PostConstruct
    public void init() {
        emf = Persistence.createEntityManagerFactory("likeHeroToZeroPU");
        System.out.println("EntityManagerFactory initialisiert");
    }

    /**
     * Pro HTTP-Request wird ein neuer EntityManager erzeugt.
     * Dadurch wird kein EntityManager zwischen parallelen Requests geteilt.
     */
    @Produces
    @RequestScoped
    public EntityManager produceEntityManager() {
        EntityManager em = emf.createEntityManager();
        System.out.println("EntityManager erzeugt: " + System.identityHashCode(em));
        return em;
    }

    /**
     * Wird am Ende des Request-Scopes automatisch aufgerufen.
     * Schließt den EntityManager sauber.
     */
    public void close(@Disposes EntityManager em) {
        if (em != null && em.isOpen()) {
            System.out.println("EntityManager geschlossen: " + System.identityHashCode(em));
            em.close();
        }
    }

    /**
     * Wird beim Stoppen der Anwendung ausgeführt.
     * Schließt die EntityManagerFactory sauber.
     */
    @PreDestroy
    public void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("EntityManagerFactory geschlossen");
        }
    }
}