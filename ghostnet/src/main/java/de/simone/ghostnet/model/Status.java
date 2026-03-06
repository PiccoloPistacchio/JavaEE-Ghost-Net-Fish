package de.simone.ghostnet.model;

/**Zyklus, den ein Netz durchläuft bzw durchlaufen kann 
 
 * Die Speicherung erfolgt in der Datenbank als String
 * (@Enumerated(EnumType.STRING) in der Entität Geisternetz).
 */
public enum Status {
    GEMELDET,
    BERGUNG_BEVORSTEHEND,
    GEBORGEN,
    VERSCHOLLEN
}