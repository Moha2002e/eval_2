package consultation.client;


import consultation.server.protocol.*;
import hepl.fead.model.entity.Consultation;
import hepl.fead.model.entity.Doctor;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MainFrame extends JFrame {
    private final NetworkManager networkManager;
    private final Doctor doctor;
    private final ConsultationTableModel tableModel = new ConsultationTableModel();
    private final JTable table = new JTable(tableModel);
    // Patients tab
    private final PatientTableModel patientTableModel = new PatientTableModel();
    private final JTable patientTable = new JTable(patientTableModel);
    public MainFrame(NetworkManager networkManager, Doctor doctor) {
        this.networkManager = networkManager;
        this.doctor = doctor;
        setTitle("Gestion des consultations – Dr. " + doctor.getLast_name());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        initComponents();
        loadConsultations();
        loadPatients();
    }

    public MainFrame(NetworkManager networkManager, hepl.fead.model.entity.Doctor doctor, NetworkManager networkManager1, Doctor doctor1) {
        this.networkManager = networkManager1;
        this.doctor = doctor1;
    }

    private void initComponents() {
        JScrollPane scrollPane = new JScrollPane(table);
        JScrollPane patientScroll = new JScrollPane(patientTable);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Consultations", scrollPane);
        tabbedPane.addTab("Patients", patientScroll);
        JPanel buttonPanel = new JPanel();
        JButton btnRefresh = new JButton("Actualiser");
        JButton btnCreate = new JButton("Créer consultation");
        JButton btnAddPatient = new JButton("Ajouter patient");
        JButton btnModify = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");
        JButton btnLogout = new JButton("Logout");
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnCreate);
        buttonPanel.add(btnAddPatient);
        buttonPanel.add(btnModify);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnLogout);
        btnRefresh.addActionListener(e -> loadConsultations());
        btnCreate.addActionListener(e -> createConsultation());
        btnAddPatient.addActionListener(e -> addPatient());
        btnModify.addActionListener(e -> modifyConsultation());
        btnDelete.addActionListener(e -> deleteConsultation());
        btnLogout.addActionListener(e -> logout());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }
    private void loadConsultations() {
        new Thread(() -> {
            try {
                ReponseTraitee resp = networkManager.sendRequest(new RequeteSearchConsultations(doctor.getId(), null, null, null));
                if (resp.isSuccess()) {
                    List<Consultation> list = (List<Consultation>) resp.getData();
                    SwingUtilities.invokeLater(() -> tableModel.setConsultations(list));
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, resp.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE));
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement des consultations: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
    private void createConsultation() {
        ConsultationCreateDialog dialog = new ConsultationCreateDialog(this);
        dialog.setVisible(true);
        if (!dialog.isConfirmed()) return;
        LocalDate date = dialog.getSelectedDate();
        LocalTime time = dialog.getSelectedTime();
        int count = dialog.getCount();
        new Thread(() -> {
            try {
                ReponseTraitee resp = networkManager.sendRequest(new RequeteAddConsultation(doctor.getId(), date, time, count));
                if (resp.isSuccess()) {
                    loadConsultations();
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, resp.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE));
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Erreur lors de la création: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
    private void loadPatients() {
        new Thread(() -> {
            try {
                ReponseTraitee resp = networkManager.sendRequest(new RequeteListPatients());
                if (resp.isSuccess()) {
                        java.util.List<hepl.fead.model.entity.Patient> list = (java.util.List<hepl.fead.model.entity.Patient>) resp.getData();
                        SwingUtilities.invokeLater(() -> patientTableModel.setPatients(list));
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, resp.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE));
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement des patients: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
    private void addPatient() {
        String nom = JOptionPane.showInputDialog(this, "Nom du patient:", "Ajouter patient", JOptionPane.QUESTION_MESSAGE);
        if (nom == null || nom.isEmpty()) return;
        String prenom = JOptionPane.showInputDialog(this, "Prénom du patient:", "Ajouter patient", JOptionPane.QUESTION_MESSAGE);
        if (prenom == null || prenom.isEmpty()) return;
        new Thread(() -> {
            try {
                ReponseTraitee resp = networkManager.sendRequest(new RequeteAddPatient(nom, prenom));
                if (resp.isSuccess()) {
                    int id = (Integer) resp.getData();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Patient ajouté avec succès. Id généré: " + id,
                                "Information", JOptionPane.INFORMATION_MESSAGE);
                        // Refresh the patients tab so the newly created patient appears
                        loadPatients();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, resp.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE));
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'ajout du patient: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
    private void modifyConsultation() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une consultation à modifier.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Consultation consultation = tableModel.getConsultationAt(row);
        // Open a modal dialog that contains all editable fields so the user updates them at once
        ConsultationEditDialog dialog = new ConsultationEditDialog(this, consultation);
        dialog.setVisible(true);
        if (!dialog.isConfirmed()) return;
        LocalDate newDate = dialog.getSelectedDate();
        LocalTime newTime = dialog.getSelectedTime();
        Integer newPatientId = dialog.getSelectedPatientId();
        String newReason = dialog.getSelectedReason();
        new Thread(() -> {
            try {
                ReponseTraitee resp = networkManager.sendRequest(new RequeteUpdateConsultation(consultation.getId(), newDate, newTime, newPatientId, newReason));
                if (resp.isSuccess()) {
                    loadConsultations();
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, resp.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE));
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
    private void deleteConsultation() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une consultation à supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Consultation consultation = tableModel.getConsultationAt(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer la consultation sélectionnée?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        new Thread(() -> {
            try {
                ReponseTraitee resp = networkManager.sendRequest(new RequeteDeleteConsultation(consultation.getId()));
                if (resp.isSuccess()) {
                    loadConsultations();
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, resp.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE));
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
    private void logout() {
        networkManager.close();
        SwingUtilities.invokeLater(() -> {
            dispose();
            LoginFrame loginFrame = new LoginFrame("localhost", 9090);
            loginFrame.setVisible(true);
        });
    }
}
