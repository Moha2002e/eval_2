package consultation.client;

import hepl.fead.model.entity.Consultation;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ConsultationTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Id", "Date", "Heure", "Prénom", "Nom", "Naissance", "Raison"};
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private List<Consultation> consultations = new ArrayList<>();
    @Override
    public int getRowCount() {
        return consultations.size();
    }
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Consultation c = consultations.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return c.getId();
            case 1:
                return c.getDateTime().format(dateFormatter);
            case 2:
                return c.getDateTime().format(timeFormatter);
            case 3: // Prénom
                return c.getPatient_first_name() != null && !c.getPatient_first_name().isEmpty() ? c.getPatient_first_name() : "-";
            case 4: // Nom
                return c.getPatient_last_name() != null && !c.getPatient_last_name().isEmpty() ? c.getPatient_last_name() : "-";
            case 5: // Naissance
                String birth = c.getPatient_birth_date();
                if (birth == null || birth.isEmpty()) return "-";
                try {
                    java.time.LocalDate bd = java.time.LocalDate.parse(birth);
                    return bd.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception ex) {
                    return birth;
                }
            
            
            
            case 6:
                return c.getReason() != null ? c.getReason() : "";
            default:
                return null;
        }
    }
    public void setConsultations(List<Consultation> consultations) {
        // Trier les consultations par date et heure (du plus récent au plus ancien)
        if (consultations != null) {
            consultations.sort(Comparator.comparing(Consultation::getDateTime).reversed());
        }
        this.consultations = consultations != null ? consultations : new ArrayList<>();
        fireTableDataChanged();
    }
    public Consultation getConsultationAt(int row) {
        return consultations.get(row);
    }
}
