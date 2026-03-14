package controller;

import dao.UserDAO;
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
    private UserDAO userDAO;

    private User loginUser = new User();
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

    public String login() {

        User user = userDAO.findByUsernameAndPassword(
                loginUser.getUsername(),
                loginUser.getPassword()
        );

        if (user != null) {
            currentUser = user;
            loginUser = new User();
            return "backend.xhtml";
        }

        return null;
    }

    public String logout() {
        currentUser = null;
        loginUser = new User();
        return "index.xhtml?faces-redirect=true";
    }

    public String ensureLoggedIn() {
        if (!isLoggedIn()) {
            return "login.xhtml?faces-redirect=true";
        }
        return null;
    }

    public String getWelcomeText() {

        if (!isLoggedIn()) {
            return "";
        }

        switch (currentUser.getGender()) {
            case MALE:
                return "Willkommen, Herr " + currentUser.getName();
            case FEMALE:
                return "Willkommen, Frau " + currentUser.getName();
            case DIVERS:
                return "Willkommen, " + currentUser.getFirstname() + " Divers";
            default:
                return "Willkommen, " + currentUser.getFirstname();
        }
    }
}
