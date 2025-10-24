package consultation.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Modal dialog to create a consultation. Collects date, time and number of slots.
 */
public class ConsultationCreateDialog extends JDialog {
    private final JTextField dateField = new JTextField(10);
    private final JTextField timeField = new JTextField(5);
    private final JTextField countField = new JTextField(5);
    private boolean confirmed = false;

    public ConsultationCreateDialog(Frame owner) {
        super(owner, "Créer consultation", true);
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
        form.add(new JLabel("Nombre de plages:"), gbc);
        gbc.gridx = 1;
        form.add(countField, gbc);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Annuler");
        buttons.add(ok);
        buttons.add(cancel);
        add(buttons, BorderLayout.SOUTH);

        ok.addActionListener((ActionEvent e) -> {
            // Validate basic formats
            String d = dateField.getText().trim();
            String t = timeField.getText().trim();
            String c = countField.getText().trim();
            if (d.isEmpty() || t.isEmpty() || c.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs sont requis", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try { LocalDate.parse(d); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Date invalide", "Erreur", JOptionPane.ERROR_MESSAGE); return; }
            try { LocalTime.parse(t); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Heure invalide", "Erreur", JOptionPane.ERROR_MESSAGE); return; }
            try { int v = Integer.parseInt(c); if (v <= 0) { JOptionPane.showMessageDialog(this, "Nombre de plages doit être > 0", "Erreur", JOptionPane.ERROR_MESSAGE); return; } } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Nombre de plages invalide", "Erreur", JOptionPane.ERROR_MESSAGE); return; }
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
        return LocalDate.parse(dateField.getText().trim());
    }

    public LocalTime getSelectedTime() {
        return LocalTime.parse(timeField.getText().trim());
    }

    public int getCount() {
        return Integer.parseInt(countField.getText().trim());
    }
}
