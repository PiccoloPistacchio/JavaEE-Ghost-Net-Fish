/**Bean fungiert als COntroller in MVC.
 * Verarbeitet die Benutzerinteraktion der JSF
 * Koordiniert Satusänderung der Netze
 * Steuerung Persistenz
 * */
package de.simone.ghostnet.bean;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.simone.ghostnet.model.Geisternetz;
import de.simone.ghostnet.model.Person;
import de.simone.ghostnet.model.Status;

@ManagedBean(name = "geisternetzBean")
@SessionScoped //Damit User-Eingaben erhalten bleiben solange Browser offen
public class GeisternetzBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // Formular: Netz melden
    private String standort;   // z.B. "53.55, 9.99"
    private String groesse;    // z.B. "ca. 20m²" / "groß"

    // MUST 1: anonym melden -> optional
    private String melderName;
    private String melderTelefon;

    // MUST 2: Bergung eintragen -> Pflichtfelder
    private String bergerName;
    private String bergerTelefon;

    // COULD 7: Verschollen -> Pflichtfelder
    private String verschollenName;
    private String verschollenTelefon;

    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("ghostnetPU");

    private EntityManager em() {
        return EMF.createEntityManager();
    }

    // MUST 3: Offene anzeigen --> Statusoptionen {GEMELDET, BERGUNG_BEVORSTEHEND}
    public List<Geisternetz> getNetze() {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT g FROM Geisternetz g " +
                    "WHERE g.status IN (:s1, :s2) " +
                    "ORDER BY g.id DESC", Geisternetz.class)
                .setParameter("s1", Status.GEMELDET)
                .setParameter("s2", Status.BERGUNG_BEVORSTEHEND)
                .getResultList();
        } finally {
            em.close();
        }
    }

    // MUST 1: anonym melden -> melder_id = NULL erlaubt
    public String melden() { 
        EntityManager em = em();
        try {
            em.getTransaction().begin();

            //Geisternetz mit standort + groesse
            Geisternetz g = new Geisternetz(standort, groesse);
            g.setStatus(Status.GEMELDET);

            //optionaler Melder (nur wenn beides da ist)
            if (!isBlank(melderName) && !isBlank(melderTelefon)) {
                Person p = new Person(melderName.trim(), melderTelefon.trim());
                em.persist(p);
                g.setMelder(p);
            } else {
                g.setMelder(null);
            }

            em.persist(g);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }

        // Formular zurücksetzen
        standort = "";
        groesse = "";
        melderName = "";
        melderTelefon = "";

        return "netze?faces-redirect=true";
    }

    // MUST 2: berger_id setzen (nur wenn leer) + Status = BERGUNG_BEVORSTEHEND
    public String bergungEintragen(Long netzId) {
        if (netzId == null) return "netze?faces-redirect=true";
        if (isBlank(bergerName) || isBlank(bergerTelefon)) return "netze?faces-redirect=true";

        EntityManager em = em();
        try {
            em.getTransaction().begin();

            Geisternetz g = em.find(Geisternetz.class, netzId);
            if (g != null && g.getBerger() == null) {
                Person p = new Person(bergerName.trim(), bergerTelefon.trim());
                em.persist(p);

                g.setBerger(p);
                g.setStatus(Status.BERGUNG_BEVORSTEHEND);
            }

            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }

        bergerName = "";
        bergerTelefon = "";

        return "netze?faces-redirect=true";
    }

    // MUST 4: Status = GEBORGEN nur wenn berger_id gesetzt
    public String alsGeborgenMarkieren(Long netzId) {
        if (netzId == null) return "netze?faces-redirect=true";

        EntityManager em = em();
        try {
            em.getTransaction().begin();

            Geisternetz g = em.find(Geisternetz.class, netzId);
            if (g != null && g.getBerger() != null) {
                g.setStatus(Status.GEBORGEN);
            }

            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }

        return "netze?faces-redirect=true";
    }

    // COULD 7: Status = VERSCHOLLEN + verschollenMelder gesetzt + nicht anonym
    public String alsVerschollenMarkieren(Long netzId) {
        if (netzId == null) return "netze?faces-redirect=true";
        if (isBlank(verschollenName) || isBlank(verschollenTelefon)) return "netze?faces-redirect=true";

        EntityManager em = em();
        try {
            em.getTransaction().begin();

            Geisternetz g = em.find(Geisternetz.class, netzId);
            if (g != null) {
                Person p = new Person(verschollenName.trim(), verschollenTelefon.trim());
                em.persist(p);

                g.setVerschollenMelder(p);
                g.setStatus(Status.VERSCHOLLEN);
            }

            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }

        verschollenName = "";
        verschollenTelefon = "";

        return "netze?faces-redirect=true";
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Getter und Setter
    public String getStandort() { return standort; }
    public void setStandort(String standort) { this.standort = standort; }

    public String getGroesse() { return groesse; }
    public void setGroesse(String groesse) { this.groesse = groesse; }

    public String getMelderName() { return melderName; }
    public void setMelderName(String melderName) { this.melderName = melderName; }

    public String getMelderTelefon() { return melderTelefon; }
    public void setMelderTelefon(String melderTelefon) { this.melderTelefon = melderTelefon; }

    public String getBergerName() { return bergerName; }
    public void setBergerName(String bergerName) { this.bergerName = bergerName; }

    public String getBergerTelefon() { return bergerTelefon; }
    public void setBergerTelefon(String bergerTelefon) { this.bergerTelefon = bergerTelefon; }

    public String getVerschollenName() { return verschollenName; }
    public void setVerschollenName(String verschollenName) { this.verschollenName = verschollenName; }

    public String getVerschollenTelefon() { return verschollenTelefon; }
    public void setVerschollenTelefon(String verschollenTelefon) { this.verschollenTelefon = verschollenTelefon; }
}