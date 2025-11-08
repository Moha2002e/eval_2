package consultation.client.controller;

import consultation.client.model.ConsultationModel;
import consultation.client.NetworkManager;
import consultation.server.protocol.*;
import hepl.fead.model.entity.Consultation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Observer;

/**
 * Controller pour gérer les consultations
 * Sépare la logique métier de la vue
 */
public class ConsultationController {
    private final ConsultationModel consultationModel;
    private final NetworkManager networkManager;

    public ConsultationController(ConsultationModel consultationModel, NetworkManager networkManager) {
        this.consultationModel = consultationModel;
        this.networkManager = networkManager;
    }

    /**
     * Charge toutes les consultations
     */
    public void loadConsultations() {
        consultationModel.setLoading(true);
        consultationModel.clearError();
        
        new Thread(() -> {
            try {
                ReponseTraitee response = networkManager.sendRequest(new RequeteSearchConsultations(null, null, null, null));
                if (response.isSuccess()) {
                    @SuppressWarnings("unchecked")
                    List<Consultation> consultations = (List<Consultation>) response.getData();
                    consultationModel.setConsultations(consultations);
                } else {
                    consultationModel.setError(response.getMessage());
                }
            } catch (Exception e) {
                consultationModel.setError("Erreur lors du chargement des consultations: " + e.getMessage());
            } finally {
                consultationModel.setLoading(false);
            }
        }).start();
    }

    /**
     * Crée de nouvelles consultations
     */
    public void createConsultations(int doctorId, LocalDate date, LocalTime time, int count) {
        consultationModel.setLoading(true);
        consultationModel.clearError();
        
        new Thread(() -> {
            try {
                ReponseTraitee response = networkManager.sendRequest(
                    new RequeteAddConsultation(doctorId, date, time, count));
                
                if (response.isSuccess()) {
                    // Recharger les consultations après création
                    loadConsultations();
                } else {
                    consultationModel.setError(response.getMessage());
                    consultationModel.setLoading(false);
                }
            } catch (Exception e) {
                consultationModel.setError("Erreur lors de la création des consultations: " + e.getMessage());
                consultationModel.setLoading(false);
            }
        }).start();
    }

    /**
     * Met à jour une consultation
     */
    public void updateConsultation(int consultationId, LocalDate newDate, LocalTime newTime, Integer patientId, String reason) {
        consultationModel.setLoading(true);
        consultationModel.clearError();
        
        new Thread(() -> {
            try {
                ReponseTraitee response = networkManager.sendRequest(
                    new RequeteUpdateConsultation(consultationId, newDate, newTime, patientId, reason));
                
                if (response.isSuccess()) {
                    // Recharger les consultations après mise à jour
                    loadConsultations();
                } else {
                    consultationModel.setError(response.getMessage());
                    consultationModel.setLoading(false);
                }
            } catch (Exception e) {
                consultationModel.setError("Erreur lors de la mise à jour de la consultation: " + e.getMessage());
                consultationModel.setLoading(false);
            }
        }).start();
    }

    /**
     * Supprime une consultation
     */
    public void deleteConsultation(int consultationId) {
        consultationModel.setLoading(true);
        consultationModel.clearError();
        
        new Thread(() -> {
            try {
                ReponseTraitee response = networkManager.sendRequest(
                    new RequeteDeleteConsultation(consultationId));
                
                if (response.isSuccess()) {
                    // Recharger les consultations après suppression
                    loadConsultations();
                } else {
                    consultationModel.setError(response.getMessage());
                    consultationModel.setLoading(false);
                }
            } catch (Exception e) {
                consultationModel.setError("Erreur lors de la suppression de la consultation: " + e.getMessage());
                consultationModel.setLoading(false);
            }
        }).start();
    }

    /**
     * Ajoute un observateur au modèle consultation
     */
    public void addConsultationObserver(Observer observer) {
        consultationModel.addObserver(observer);
    }

    /**
     * Retire un observateur du modèle consultation
     */
    public void removeConsultationObserver(Observer observer) {
        consultationModel.deleteObserver(observer);
    }
}
