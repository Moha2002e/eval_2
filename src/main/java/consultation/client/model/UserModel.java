package consultation.client.model;

import hepl.fead.model.entity.Doctor;
import java.util.Observable;

/**
 * Model pour gérer les données de l'utilisateur connecté
 * Implémente le pattern Observer pour notifier les changements
 */
public class UserModel extends Observable {
    private Doctor currentDoctor;
    private boolean isLoggedIn = false;
    private String loginError = null;

    public Doctor getCurrentDoctor() {
        return currentDoctor;
    }

    public void setCurrentDoctor(Doctor doctor) {
        this.currentDoctor = doctor;
        this.isLoggedIn = (doctor != null);
        this.loginError = null;
        setChanged();
        notifyObservers();
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void logout() {
        this.currentDoctor = null;
        this.isLoggedIn = false;
        this.loginError = null;
        setChanged();
        notifyObservers();
    }

    public String getLoginError() {
        return loginError;
    }

    public void setLoginError(String error) {
        this.loginError = error;
        setChanged();
        notifyObservers();
    }

    public void clearLoginError() {
        this.loginError = null;
        setChanged();
        notifyObservers();
    }
}
