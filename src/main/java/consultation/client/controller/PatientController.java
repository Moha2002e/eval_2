package consultation.client.controller;

import consultation.client.model.PatientModel;
import consultation.client.NetworkManager;
import consultation.server.protocol.RequeteListPatients;
import consultation.server.protocol.ReponseTraitee;
import hepl.fead.model.entity.Patient;

import java.util.List;
import java.util.Observer;

/**
 * Controller pour gérer les patients
 * Sépare la logique métier de la vue
 */
public class PatientController {
    private final PatientModel patientModel;
    private final NetworkManager networkManager;

    public PatientController(PatientModel patientModel, NetworkManager networkManager) {
        this.patientModel = patientModel;
        this.networkManager = networkManager;
    }

    /**
     * Charge tous les patients
     */
    public void loadPatients() {
        patientModel.setLoading(true);
        patientModel.clearError();
        
        new Thread(() -> {
            try {
                ReponseTraitee response = networkManager.sendRequest(new RequeteListPatients());
                if (response.isSuccess()) {
                    @SuppressWarnings("unchecked")
                    List<Patient> patients = (List<Patient>) response.getData();
                    patientModel.setPatients(patients);
                } else {
                    patientModel.setError(response.getMessage());
                }
            } catch (Exception e) {
                patientModel.setError("Erreur lors du chargement des patients: " + e.getMessage());
            } finally {
                patientModel.setLoading(false);
            }
        }).start();
    }

    /**
     * Ajoute un observateur au modèle patient
     */
    public void addPatientObserver(Observer observer) {
        patientModel.addObserver(observer);
    }

    /**
     * Retire un observateur du modèle patient
     */
    public void removePatientObserver(Observer observer) {
        patientModel.deleteObserver(observer);
    }
}
