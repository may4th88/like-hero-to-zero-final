package config;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class JpaProducer {

    private EntityManagerFactory emf;

    @PostConstruct
    public void init() {
        emf = Persistence.createEntityManagerFactory("likeHeroToZeroPU");
    }

    @Produces
    public EntityManager produceEntityManager() {
        return emf.createEntityManager();
    }

    public void close(@Disposes EntityManager em) {
        if (em.isOpen()) {
            em.close();
        }
    }
}
