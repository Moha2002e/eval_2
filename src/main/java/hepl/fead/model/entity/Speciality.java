package hepl.fead.model.entity;

import java.io.Serializable;

public class Speciality implements Entity {
    private static final long serialVersionUID = 1L;
    private Integer id ;
    private String name ;

    public Speciality(){

    }
    public Speciality(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Speciality{" + "id=" + id + ", name=" + name + '}';
    }

}
