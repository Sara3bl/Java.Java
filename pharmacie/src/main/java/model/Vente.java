package model;

import java.time.LocalDate;
import java.util.Objects;

public class Vente {

    private int id;
    private int medicamentId;
    private int quantiteVendue;
    private double prixUnitaire;
    private LocalDate dateVente;

    // ===== Constructors =====

    public Vente() {
    }

    public Vente(int id, int medicamentId, int quantiteVendue,
                 double prixUnitaire, LocalDate dateVente) {
        this.id = id;
        this.medicamentId = medicamentId;
        this.quantiteVendue = quantiteVendue;
        this.prixUnitaire = prixUnitaire;
        this.dateVente = dateVente;
    }

    // ===== Getters & Setters =====

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMedicamentId() {
        return medicamentId;
    }

    public void setMedicamentId(int medicamentId) {
        this.medicamentId = medicamentId;
    }

    public int getQuantiteVendue() {
        return quantiteVendue;
    }

    public void setQuantiteVendue(int quantiteVendue) {
        this.quantiteVendue = quantiteVendue;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public LocalDate getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDate dateVente) {
        this.dateVente = dateVente;
    }

    // ===== Derived Values =====

    public double getTotal() {
        return quantiteVendue * prixUnitaire;
    }

    // ===== Overrides =====

    @Override
    public String toString() {
        return "Vente{" +
                "id=" + id +
                ", medicamentId=" + medicamentId +
                ", quantiteVendue=" + quantiteVendue +
                ", prixUnitaire=" + prixUnitaire +
                ", dateVente=" + dateVente +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vente)) return false;
        Vente vente = (Vente) o;
        return id == vente.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
