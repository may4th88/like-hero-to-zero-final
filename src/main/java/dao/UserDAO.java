package dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.User;

@ApplicationScoped
public class UserDAO {

    @Inject
    private EntityManager em;

    /**
     * Sucht einen Benutzer anhand von Benutzername und Passwort.
     */
    public User findByUsernameAndPassword(String username, String password) {
        TypedQuery<User> query = em.createQuery(
            "SELECT u FROM User u " +
            "WHERE u.username = :username AND u.password = :password",
            User.class
        );

        query.setParameter("username", username);
        query.setParameter("password", password);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}