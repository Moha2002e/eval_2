package consultation.client.model;

import hepl.fead.model.entity.Patient;
import java.util.List;
import java.util.Observable;

/**
 * Model pour gérer les données des patients
 * Implémente le pattern Observer pour notifier les changements
 */
public class PatientModel extends Observable {
    private List<Patient> patients;
    private boolean isLoading = false;
    private String errorMessage = null;

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
        this.errorMessage = null;
        setChanged();
        notifyObservers();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
        setChanged();
        notifyObservers();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setError(String errorMessage) {
        this.errorMessage = errorMessage;
        setChanged();
        notifyObservers();
    }

    public void clearError() {
        this.errorMessage = null;
        setChanged();
        notifyObservers();
    }
}
