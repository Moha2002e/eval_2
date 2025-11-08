package consultation.client;

import com.formdev.flatlaf.FlatClientProperties;
import hepl.fead.model.entity.Consultation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Modal dialog to edit a consultation. Prefills current values and returns the new values when confirmed.
 */
public class ConsultationEditDialog extends JDialog {
    private final JSpinner dateSpinner;
    private final JSpinner timeSpinner;
    private final JTextField patientField = new JTextField(10);
    private final JTextField reasonField = new JTextField(30);
    private boolean confirmed = false;

    public ConsultationEditDialog(Frame owner, Consultation consultation) {
        super(owner, "Modifier consultation", true);
        setMinimumSize(new Dimension(400, 360));

        // Initialiser les spinners avec les valeurs de la consultation
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);

        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);

        initComponents(consultation);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents(Consultation consultation) {
        // Style moderne
        dateSpinner.setPreferredSize(new Dimension(200, 36));
        timeSpinner.setPreferredSize(new Dimension(200, 36));

        patientField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Id patient");
        patientField.putClientProperty(FlatClientProperties.STYLE, "showClearButton:true;");
        patientField.setPreferredSize(new Dimension(200, 36));

        reasonField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Raison");
        reasonField.putClientProperty(FlatClientProperties.STYLE, "showClearButton:true;");
        reasonField.setPreferredSize(new Dimension(200, 36));

        // Layout compact
        var panel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.gridx = 0; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        gbc.gridy = y++; panel.add(new JLabel("Modifier la consultation", SwingConstants.CENTER), gbc);

        gbc.gridy = y++;
        var dateLabel = new JLabel("Date");
        dateLabel.putClientProperty(FlatClientProperties.STYLE, "font:-1;");
        panel.add(dateLabel, gbc);
        gbc.gridy = y++; panel.add(dateSpinner, gbc);

        gbc.gridy = y++;
        var timeLabel = new JLabel("Heure");
        timeLabel.putClientProperty(FlatClientProperties.STYLE, "font:-1;");
        panel.add(timeLabel, gbc);
        gbc.gridy = y++; panel.add(timeSpinner, gbc);

        gbc.gridy = y++;
        var patientLabel = new JLabel("Id patient (laisser vide = inchangé)");
        patientLabel.putClientProperty(FlatClientProperties.STYLE, "font:-1;");
        panel.add(patientLabel, gbc);
        gbc.gridy = y++; panel.add(patientField, gbc);

        gbc.gridy = y++;
        var reasonLabel = new JLabel("Raison");
        reasonLabel.putClientProperty(FlatClientProperties.STYLE, "font:-1;");
        panel.add(reasonLabel, gbc);
        gbc.gridy = y++; panel.add(reasonField, gbc);

        // Boutons
        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        JButton cancel = new JButton("Annuler");
        JButton ok = new JButton("OK");

        cancel.putClientProperty(FlatClientProperties.STYLE, "borderWidth:1; arc:999;");
        cancel.setPreferredSize(new Dimension(120, 36));
        ok.putClientProperty(FlatClientProperties.STYLE, "font:+1; borderWidth:1; arc:999;");
        ok.setPreferredSize(new Dimension(120, 36));

        buttons.add(cancel);
        buttons.add(ok);

        gbc.gridy = y++;
        gbc.insets = new Insets(16, 12, 8, 12);
        panel.add(buttons, gbc);

        setContentPane(panel);

        // Prefill les spinners avec les valeurs de la consultation
        try {
            Date dateValue;
            if (consultation.getDateTime() != null) {
                // OffsetDateTime vers Date
                dateValue = Date.from(consultation.getDateTime().toInstant());
            } else {
                // Parse manuellement depuis les chaînes de date/heure
                LocalDate date = LocalDate.parse(consultation.getDate());
                LocalTime time = LocalTime.parse(consultation.getHour());
                LocalDateTime dateTime = LocalDateTime.of(date, time);
                dateValue = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
            }

            dateSpinner.setValue(dateValue);
            timeSpinner.setValue(dateValue);
        } catch (Exception ignore) {
            // Garder les valeurs par défaut (aujourd'hui)
        }

        patientField.setText(consultation.getPatient_id() == null ? "" : consultation.getPatient_id().toString());
        reasonField.setText(consultation.getReason() == null ? "" : consultation.getReason());

        ok.addActionListener((ActionEvent e) -> {
            confirmed = true;
            setVisible(false);
        });

        cancel.addActionListener(e -> { confirmed = false; setVisible(false); });

        getRootPane().setDefaultButton(ok);
    }

    public boolean isConfirmed() { return confirmed; }

    public LocalDate getSelectedDate() {
        Date date = (Date) dateSpinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public LocalTime getSelectedTime() {
        Date date = (Date) timeSpinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public Integer getSelectedPatientId() {
        String p = patientField.getText().trim();
        if (p.isEmpty()) return null;
        try { return Integer.parseInt(p); } catch (Exception e) { return null; }
    }

    public String getSelectedReason() {
        String r = reasonField.getText().trim();
        return r.isEmpty() ? null : r;
    }
}

