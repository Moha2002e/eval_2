package consultation.client;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Modal dialog to create a consultation. Collects date, time and number of slots.
 */
public class ConsultationCreateDialog extends JDialog {
    private final JSpinner dateSpinner;
    private final JSpinner timeSpinner;
    private final JTextField countField = new JTextField(5);
    private boolean confirmed = false;

    public ConsultationCreateDialog(Frame owner) {
        super(owner, "Créer consultation", true);
        setMinimumSize(new Dimension(380, 320));

        // Initialiser le spinner de date
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);

        // Initialiser le spinner d'heure
        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);

        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        // Style moderne pour les spinners
        dateSpinner.setPreferredSize(new Dimension(200, 36));
        timeSpinner.setPreferredSize(new Dimension(200, 36));

        countField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ex: 10");
        countField.putClientProperty(FlatClientProperties.STYLE, "showClearButton:true;");
        countField.setPreferredSize(new Dimension(200, 36));

        // Layout compact
        var panel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.gridx = 0; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        gbc.gridy = y++; panel.add(new JLabel("Créer une consultation", SwingConstants.CENTER), gbc);

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
        var countLabel = new JLabel("Nombre de plages");
        countLabel.putClientProperty(FlatClientProperties.STYLE, "font:-1;");
        panel.add(countLabel, gbc);
        gbc.gridy = y++; panel.add(countField, gbc);

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

        ok.addActionListener((ActionEvent e) -> {
            // Validate count field
            String c = countField.getText().trim();
            if (c.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le nombre de plages est requis", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int v = Integer.parseInt(c);
                if (v <= 0) {
                    JOptionPane.showMessageDialog(this, "Nombre de plages doit être > 0", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Nombre de plages invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
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

    public int getCount() {
        return Integer.parseInt(countField.getText().trim());
    }
}
