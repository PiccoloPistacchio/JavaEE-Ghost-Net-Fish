package de.simone.ghostnet.model;

import javax.persistence.*;

@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Primärschlüssel, automatisch generiert
    private Long id; 

    private String name;
    private String telefon;

    public Person() {}

    public Person(String name, String telefon) {
        this.name = name;
        this.telefon = telefon;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
}
