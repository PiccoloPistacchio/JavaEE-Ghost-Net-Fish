package de.simone.ghostnet.model;

import javax.persistence.*;

@Entity
@Table(name = "geisternetz") //Tabelle in MySQL DB
public class Geisternetz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Inkrement durch DB
    private Long id;

    private String standort;
    private String groesse;

    @Enumerated(EnumType.STRING) // Speicherung lesbarer STring in DB
    private Status status = Status.GEMELDET;

    // MUST 1: anonym melden -> melder darf NULL sein
    @ManyToOne(optional = true)
    @JoinColumn(name = "melder_id")
    private Person melder;

    // MUST 2: Berger wird gesetzt bei Bergung
    @ManyToOne(optional = true)
    @JoinColumn(name = "berger_id")
    private Person berger;

    // PFLICHT bei "verschollen": Person muss gesetzt sein (nicht anonym)
    @ManyToOne(optional = true)
    @JoinColumn(name = "verschollen_melder_id")
    private Person verschollenMelder;

    public Geisternetz() {
    }

    public Geisternetz(String standort, String groesse) {
        this.standort = standort;
        this.groesse = groesse;
        this.status = Status.GEMELDET;
    }

    public Long getId() { return id; }


    public String getStandort() { return standort; }
    public void setStandort(String standort) { this.standort = standort; }

    public String getGroesse() { return groesse; }
    public void setGroesse(String groesse) { this.groesse = groesse; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Person getMelder() { return melder; }
    public void setMelder(Person melder) { this.melder = melder; }

    public Person getBerger() { return berger; }
    public void setBerger(Person berger) { this.berger = berger; }

    public Person getVerschollenMelder() { return verschollenMelder; }
    public void setVerschollenMelder(Person verschollenMelder) { this.verschollenMelder = verschollenMelder; }
}
