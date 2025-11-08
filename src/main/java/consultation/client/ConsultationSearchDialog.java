package consultation.client;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Modal dialog to search consultations by patient ID and/or date range.
 */
public class ConsultationSearchDialog extends JDialog {
    private final JTextField patientIdField = new JTextField(10);
    private final JSpinner fromDateSpinner;
    private final JSpinner toDateSpinner;
    private final JCheckBox useFromDateCheck = new JCheckBox("Utiliser date de début");
    private final JCheckBox useToDateCheck = new JCheckBox("Utiliser date de fin");
    private boolean confirmed = false;

    public ConsultationSearchDialog(Frame owner) {
        super(owner, "Rechercher consultations", true);
        setMinimumSize(new Dimension(420, 380));

        // Initialiser les spinners de date
        SpinnerDateModel fromModel = new SpinnerDateModel();
        fromDateSpinner = new JSpinner(fromModel);
        JSpinner.DateEditor fromEditor = new JSpinner.DateEditor(fromDateSpinner, "dd/MM/yyyy");
        fromDateSpinner.setEditor(fromEditor);

        SpinnerDateModel toModel = new SpinnerDateModel();
        toDateSpinner = new JSpinner(toModel);
        JSpinner.DateEditor toEditor = new JSpinner.DateEditor(toDateSpinner, "dd/MM/yyyy");
        toDateSpinner.setEditor(toEditor);

        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        // Style moderne pour le champ patient
        patientIdField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "ID du patient (optionnel)");
        patientIdField.putClientProperty(FlatClientProperties.STYLE, "showClearButton:true;");
        patientIdField.setPreferredSize(new Dimension(200, 36));

        // Style pour les spinners
        fromDateSpinner.setPreferredSize(new Dimension(200, 36));
        toDateSpinner.setPreferredSize(new Dimension(200, 36));

        // Désactiver les spinners par défaut
        fromDateSpinner.setEnabled(false);
        toDateSpinner.setEnabled(false);

        // Listeners pour activer/désactiver les spinners
        useFromDateCheck.addActionListener(e -> fromDateSpinner.setEnabled(useFromDateCheck.isSelected()));
        useToDateCheck.addActionListener(e -> toDateSpinner.setEnabled(useToDateCheck.isSelected()));

        // Layout compact
        var panel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.gridx = 0; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        // Titre
        var titleLabel = new JLabel("Rechercher des consultations", SwingConstants.CENTER);
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2;");
        gbc.gridy = y++;
        panel.add(titleLabel, gbc);

        // Sous-titre
        var subtitleLabel = new JLabel("Laissez les champs vides pour voir toutes les consultations", SwingConstants.CENTER);
        subtitleLabel.putClientProperty(FlatClientProperties.STYLE, "font:-1; foreground:mix(@foreground,@background,50%);");
        gbc.gridy = y++;
        panel.add(subtitleLabel, gbc);

        // Patient ID
        gbc.gridy = y++;
        var patientLabel = new JLabel("ID du patient");
        patientLabel.putClientProperty(FlatClientProperties.STYLE, "font:-1;");
        panel.add(patientLabel, gbc);
        gbc.gridy = y++; panel.add(patientIdField, gbc);

        // Date de début avec checkbox
        gbc.gridy = y++;
        panel.add(useFromDateCheck, gbc);
        gbc.gridy = y++; panel.add(fromDateSpinner, gbc);

        // Date de fin avec checkbox
        gbc.gridy = y++;
        panel.add(useToDateCheck, gbc);
        gbc.gridy = y++; panel.add(toDateSpinner, gbc);

        // Boutons
        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        JButton cancel = new JButton("Annuler");
        JButton search = new JButton("Rechercher");

        cancel.putClientProperty(FlatClientProperties.STYLE, "borderWidth:1; arc:999;");
        cancel.setPreferredSize(new Dimension(120, 36));
        search.putClientProperty(FlatClientProperties.STYLE, "font:+1; borderWidth:1; arc:999;");
        search.setPreferredSize(new Dimension(120, 36));

        buttons.add(cancel);
        buttons.add(search);

        gbc.gridy = y++;
        gbc.insets = new Insets(16, 12, 8, 12);
        panel.add(buttons, gbc);

        setContentPane(panel);

        search.addActionListener(e -> {
            // Validate patient ID if provided
            String patientStr = patientIdField.getText().trim();
            if (!patientStr.isEmpty()) {
                try {
                    Integer.parseInt(patientStr);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "ID patient invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Valider que fromDate <= toDate si les deux sont sélectionnés
            if (useFromDateCheck.isSelected() && useToDateCheck.isSelected()) {
                Date fromDate = (Date) fromDateSpinner.getValue();
                Date toDate = (Date) toDateSpinner.getValue();
                if (fromDate.after(toDate)) {
                    JOptionPane.showMessageDialog(this, "La date de début doit être avant la date de fin", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            confirmed = true;
            setVisible(false);
        });

        cancel.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        getRootPane().setDefaultButton(search);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Integer getPatientId() {
        String p = patientIdField.getText().trim();
        if (p.isEmpty()) return null;
        try {
            return Integer.parseInt(p);
        } catch (Exception e) {
            return null;
        }
    }

    public LocalDate getFromDate() {
        if (!useFromDateCheck.isSelected()) return null;
        Date date = (Date) fromDateSpinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public LocalDate getToDate() {
        if (!useToDateCheck.isSelected()) return null;
        Date date = (Date) toDateSpinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}

