package hepl.fead.model.viewmodel;

import java.io.Serializable;

public class SpecialitySearchVM implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;

    public SpecialitySearchVM() {
    }

    public SpecialitySearchVM(String name) {
        this.name = name;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
