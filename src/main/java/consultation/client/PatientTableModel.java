package consultation.client;

import hepl.fead.model.entity.Patient;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class PatientTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Id", "Prénom", "Nom", "Naissance"};
    private java.util.List<Patient> patients = new ArrayList<>();

    public void setPatients(java.util.List<Patient> list) {
        this.patients = list == null ? new ArrayList<>() : list;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() { return patients.size(); }

    @Override
    public int getColumnCount() { return columnNames.length; }

    @Override
    public String getColumnName(int column) { return columnNames[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Patient p = patients.get(rowIndex);
        switch (columnIndex) {
            case 0: return p.getId();
            case 1: return p.getFirst_name();
            case 2: return p.getLast_name();
            case 3: return p.getBirth_date();
            default: return null;
        }
    }

    public Patient getPatientAt(int row) { return patients.get(row); }
}
