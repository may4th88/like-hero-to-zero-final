package config;

import jakarta.enterprise.context.ApplicationScoped;
import model.User;

import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class UserStore implements Serializable {

    private static final long serialVersionUID = 1L;

    // Demo-Users
    private final List<User> users = List.of(
            new User("mueller", "secret", User.Role.SCIENTIST),
            new User("editor", "secret", User.Role.EDITOR)
    );

    public List<User> getUsers() {
        return users;
    }
}
