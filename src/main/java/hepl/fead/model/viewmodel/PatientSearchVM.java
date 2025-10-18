package hepl.fead.model.viewmodel;


import java.io.Serializable;

public class PatientSearchVM implements Serializable {
    private static final long serialVersionUID = 1L;
    private String lastName;
    private String firstName;
    private String birthDateFrom;
    private String birthDateTo;

    public PatientSearchVM() {
    }

    public PatientSearchVM(String lastName, String firstName, String birthDateFrom, String birthDateTo) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDateFrom = birthDateFrom;
        this.birthDateTo = birthDateTo;
    }

    // Getters and Setters
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getBirthDateFrom() {
        return birthDateFrom;
    }

    public void setBirthDateFrom(String birthDateFrom) {
        this.birthDateFrom = birthDateFrom;
    }

    public String getBirthDateTo() {
        return birthDateTo;
    }

    public void setBirthDateTo(String birthDateTo) {
        this.birthDateTo = birthDateTo;
    }
}
