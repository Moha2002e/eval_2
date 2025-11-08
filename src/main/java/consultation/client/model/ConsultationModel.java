package consultation.client.model;

import hepl.fead.model.entity.Consultation;
import java.util.List;
import java.util.Observable;

/**
 * Model pour gérer les données des consultations
 * Implémente le pattern Observer pour notifier les changements
 */
public class ConsultationModel extends Observable {
    private List<Consultation> consultations;
    private boolean isLoading = false;
    private String errorMessage = null;

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
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
