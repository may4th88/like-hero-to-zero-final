package dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import model.Emission;

@ApplicationScoped
public class EmissionDAO {

    @Inject
    private EntityManager em;

    public List<Emission> findLatestEmissionsPerCountry() {
        return em.createQuery(
            "SELECT e FROM Emission e " +
            "WHERE e.year = (" +
            "  SELECT MAX(e2.year) FROM Emission e2 WHERE e2.country = e.country" +
            ") ORDER BY e.country.name",
            Emission.class
        ).getResultList();
    }
}
