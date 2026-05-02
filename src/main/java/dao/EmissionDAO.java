package dao;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Emission;

@ApplicationScoped
public class EmissionDAO {

    @Inject
    private EntityManager em;

    /**
     * Liefert pro Land den jeweils aktuellsten Emissionsdatensatz.
     */
    public List<Emission> findLatestEmissionsPerCountry() {
        return em.createQuery(
            "SELECT e FROM Emission e " +
            "JOIN FETCH e.country c " +
            "WHERE e.year = (" +
            "   SELECT MAX(e2.year) FROM Emission e2 " +
            "   WHERE e2.country.id = e.country.id" +
            ") " +
            "ORDER BY c.name",
            Emission.class
        ).getResultList();
    }

    /**
     * Aktualisiert einen bestehenden Emissionsdatensatz.
     */
    public Emission update(Emission emission) {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            Emission mergedEmission = em.merge(emission);
            transaction.commit();

            return mergedEmission;
        } catch (RuntimeException e) {
            rollback(transaction);
            throw e;
        }
    }

    /**
     * Prüft, ob für ein Land und ein Jahr bereits ein Emissionsdatensatz existiert.
     */
    public boolean existsByCountryAndYear(Long countryId, int year) {
        Long count = em.createQuery(
            "SELECT COUNT(e) FROM Emission e " +
            "WHERE e.country.id = :countryId AND e.year = :year",
            Long.class
        )
            .setParameter("countryId", countryId)
            .setParameter("year", year)
            .getSingleResult();

        return count > 0;
    }

    /**
     * Legt einen neuen Emissionsdatensatz an.
     */
    public Emission create(Emission emission) {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.persist(emission);
            transaction.commit();

            return emission;
        } catch (RuntimeException e) {
            rollback(transaction);
            throw e;
        }
    }

    /**
     * Sucht einen Emissionsdatensatz anhand seiner Datenbank-ID.
     */
    public Emission findById(Long id) {
        if (id == null) {
            return null;
        }

        return em.find(Emission.class, id);
    }

    /**
     * Liefert alle Emissionsdatensätze eines Landes aufsteigend nach Jahr sortiert.
     */
    public List<Emission> findEmissionHistoryByCountry(Long countryId) {
        return em.createQuery(
            "SELECT e FROM Emission e " +
            "WHERE e.country.id = :countryId " +
            "ORDER BY e.year",
            Emission.class
        )
            .setParameter("countryId", countryId)
            .getResultList();
    }

    /**
     * Liefert den neuesten Emissionsdatensatz eines Landes.
     */
    public Emission findLatestEmissionByCountry(Long countryId) {
        List<Emission> emissions = em.createQuery(
            "SELECT e FROM Emission e " +
            "WHERE e.country.id = :countryId " +
            "ORDER BY e.year DESC",
            Emission.class
        )
            .setParameter("countryId", countryId)
            .setMaxResults(1)
            .getResultList();

        return emissions.isEmpty() ? null : emissions.get(0);
    }

    /**
     * Setzt eine aktive Transaktion im Fehlerfall zurück.
     */
    private void rollback(EntityTransaction transaction) {
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }
}