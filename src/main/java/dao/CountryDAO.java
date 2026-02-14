package dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

import controller.EmissionsController;
import model.Country;

@ApplicationScoped
public class CountryDAO {

    @Inject
    private EntityManager em;

    public List<Country> findCountries() {
        return em.createQuery(
            "SELECT c FROM Country c ",
            Country.class
        ).getResultList();}
    
        
    public Country findCountry(Long countryId) {
    	System.out.println("debug countryid");
    	System.out.println(countryId);
    	
        // Debug-Query: erst einmal alles holen
        var debugQuery = em.createQuery(
            "SELECT c FROM Country c",
            Country.class
        );

        System.out.println("DEBUG: Alle Länder:");
        debugQuery.getResultList()
                  .forEach(c -> System.out.println(c));

        // Platzhalter für die finale Query
        var finalQuery = em.createQuery(
        	    "SELECT c FROM Country c WHERE c.id = :countryId",
        	    Country.class
        	);

        finalQuery.setParameter("countryId", countryId);


        // bewusst noch kein Filter / Parameter
        return finalQuery.getResultList().isEmpty()
                ? null
                : finalQuery.getResultList().get(0);
    }

}
