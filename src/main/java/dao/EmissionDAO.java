package dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

import model.Emission;

@ApplicationScoped
public class EmissionDAO {

    @Inject
    private EntityManager em;

    /**
     * Liefert pro Land den jeweils aktuellsten Emissionsdatensatz.
     */
    public List<Emission> findLatestEmissionsPerCountry() {

        System.out.println("=== DAO: findLatestEmissionsPerCountry() ===");

        List<Emission> result = em.createQuery(
            "SELECT e FROM Emission e " +
            "WHERE e.year = (" +
            "  SELECT MAX(e2.year) FROM Emission e2 WHERE e2.country = e.country" +
            ") ORDER BY e.country.name",
            Emission.class
        ).getResultList();

        System.out.println("Gefundene Datensätze: " + result.size());

        return result;
    }

    /**
     * Update bestehender Datensatz
     */
    public Emission update(Emission emission) {

        System.out.println("=== DAO: update() ===");
        System.out.println("ID: " + emission.getId());
        System.out.println("Status: " + emission.getStatus());

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Emission merged = em.merge(emission);
            tx.commit();

            System.out.println("Update erfolgreich committed.");

            return merged;

        } catch (RuntimeException e) {

            System.out.println("!!! FEHLER BEIM UPDATE !!!");
            e.printStackTrace();

            if (tx.isActive()) {
                tx.rollback();
                System.out.println("Rollback durchgeführt.");
            }

            throw e;
        }
    }

    /**
     * Prüfen ob Land + Jahr bereits existiert
     */
    public boolean existsByCountryAndYear(Long countryId, int year) {

        System.out.println("=== DAO: existsByCountryAndYear() ===");
        System.out.println("CountryId: " + countryId);
        System.out.println("Year: " + year);

        Long count = em.createQuery(
            "SELECT COUNT(e) FROM Emission e WHERE e.country.id = :cid AND e.year = :year",
            Long.class)
            .setParameter("cid", countryId)
            .setParameter("year", year)
            .getSingleResult();

        System.out.println("Gefundene Anzahl: " + count);

        return count > 0;
    }

    /**
     * Neuer Datensatz
     */
    public Emission create(Emission emission) {

        System.out.println("=== DAO: create() ===");
        System.out.println("Country: " + 
            (emission.getCountry() != null ? emission.getCountry().getName() : "NULL"));
        System.out.println("Year: " + emission.getYear());
        System.out.println("co2KtPending: " + emission.getCo2KtPending());
        System.out.println("Status: " + emission.getStatus());
        System.out.println("Unit: " + emission.getUnit());

        EntityTransaction tx = em.getTransaction();

        try {

            System.out.println("Transaktion gestartet...");
            tx.begin();

            em.persist(emission);

            System.out.println("Persist ausgeführt...");

            tx.commit();

            System.out.println("Commit erfolgreich.");
            System.out.println("Neue ID: " + emission.getId());

            return emission;

        } catch (RuntimeException e) {

            System.out.println("!!! FEHLER BEIM CREATE !!!");
            e.printStackTrace();

            if (tx.isActive()) {
                tx.rollback();
                System.out.println("Rollback durchgeführt.");
            }

            throw e;
        }
    }

    public Emission findById(Long id) {
        System.out.println("=== DAO: findById() === ID=" + id);
        return em.find(Emission.class, id);
    }
}
