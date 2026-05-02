package dao;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import model.Country;

@ApplicationScoped
public class CountryDAO {

    @Inject
    private EntityManager em;

    /**
     * Liefert alle Länder alphabetisch sortiert.
     */
    public List<Country> findCountries() {
        return em.createQuery(
            "SELECT c FROM Country c ORDER BY c.name",
            Country.class
        ).getResultList();
    }

    /**
     * Sucht ein Land anhand seiner Datenbank-ID.
     */
    public Country findCountry(Long countryId) {
        if (countryId == null) {
            return null;
        }

        return em.find(Country.class, countryId);
    }
}