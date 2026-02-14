package controller;

import config.UserStore;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.User;

import java.io.Serializable;

@Named
@SessionScoped
public class AuthController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserStore userStore;

    // Eingaben aus login.xhtml
    private User loginUser = new User();

    // eingeloggter User in der Session
    private User currentUser;

    public User getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isScientist() {
        return isLoggedIn() && currentUser.getRole() == User.Role.SCIENTIST;
    }

    public boolean isEditor() {
        return isLoggedIn() && currentUser.getRole() == User.Role.EDITOR;
    }

    /**
     * Login + Navigation über Rückgabewert
     */
    public String login() {
        for (User u : userStore.getUsers()) {
            if (u.equals(loginUser)) {
                currentUser = u;
                loginUser = new User(); // Felder leeren
                return "backend.xhtml?faces-redirect=true";
            }
        }
        // Login fehlgeschlagen -> auf login.xhtml bleiben
        return null;
    }

    public String logout() {
        currentUser = null;
        loginUser = new User();
        return "index.xhtml?faces-redirect=true";
    }

    /**
     * ViewAction-Guard: wenn nicht eingeloggt -> Login
     */
    public String ensureLoggedIn() {
        if (!isLoggedIn()) {
            return "login.xhtml?faces-redirect=true";
        }
        return null;
    }
}
