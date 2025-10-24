package consultation.client;

import hepl.fead.model.entity.Consultation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Modal dialog to edit a consultation. Prefills current values and returns the new values when confirmed.
 */
public class ConsultationEditDialog extends JDialog {
    private final JTextField dateField = new JTextField(10);
    private final JTextField timeField = new JTextField(5);
    private final JTextField patientField = new JTextField(10);
    private final JTextField reasonField = new JTextField(30);
    private boolean confirmed = false;

    public ConsultationEditDialog(Frame owner, Consultation consultation) {
        super(owner, "Modifier consultation", true);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        form.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Heure (HH:MM):"), gbc);
        gbc.gridx = 1;
        form.add(timeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Id patient (laisser vide = inchangé):"), gbc);
        gbc.gridx = 1;
        form.add(patientField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Raison:"), gbc);
        gbc.gridx = 1;
        form.add(reasonField, gbc);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Annuler");
        buttons.add(ok);
        buttons.add(cancel);
        add(buttons, BorderLayout.SOUTH);

        // Prefill
        try {
            if (consultation.getDateTime() != null) {
                dateField.setText(consultation.getDateTime().toLocalDate().toString());
                timeField.setText(consultation.getDateTime().toLocalTime().toString());
            } else {
                dateField.setText(consultation.getDate());
                timeField.setText(consultation.getHour());
            }
        } catch (Exception ignore) {
            dateField.setText(consultation.getDate());
            timeField.setText(consultation.getHour());
        }
        patientField.setText(consultation.getPatient_id() == null ? "" : consultation.getPatient_id().toString());
        reasonField.setText(consultation.getReason() == null ? "" : consultation.getReason());

        ok.addActionListener((ActionEvent e) -> {
            // Validate basic formats
            String d = dateField.getText().trim();
            String t = timeField.getText().trim();
            if (!d.isEmpty()) {
                try { LocalDate.parse(d); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Date invalide", "Erreur", JOptionPane.ERROR_MESSAGE); return; }
            }
            if (!t.isEmpty()) {
                try { LocalTime.parse(t); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Heure invalide", "Erreur", JOptionPane.ERROR_MESSAGE); return; }
            }
            confirmed = true;
            setVisible(false);
        });

        cancel.addActionListener(e -> { confirmed = false; setVisible(false); });

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public boolean isConfirmed() { return confirmed; }

    public LocalDate getSelectedDate() {
        String d = dateField.getText().trim();
        if (d.isEmpty()) return null;
        return LocalDate.parse(d);
    }

    public LocalTime getSelectedTime() {
        String t = timeField.getText().trim();
        if (t.isEmpty()) return null;
        return LocalTime.parse(t);
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
