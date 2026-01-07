package model;

import java.time.LocalDate;
import java.util.Objects;

public class Medicament {

    private int id;
    private String nom;
    private String categorie;
    private int quantite;
    private LocalDate dateExpiration;
    private int fournisseurId;
    private double prix;

    // ===== Constructors =====

    public Medicament() {
    }

    public Medicament(int id, String nom, String categorie, int quantite,
                      LocalDate dateExpiration, int fournisseurId, double prix) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.quantite = quantite;
        this.dateExpiration = dateExpiration;
        this.fournisseurId = fournisseurId;
        this.prix = prix;
    }

    // ===== Getters & Setters =====

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public int getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(int fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    // ===== Business Helpers =====

    public boolean estExpire() {
        return dateExpiration != null &&
                dateExpiration.isBefore(LocalDate.now());
    }

    public boolean stockFaible(int seuil) {
        return quantite <= seuil;
    }

    // ===== Overrides =====

    @Override
    public String toString() {
        return "Medicament{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", categorie='" + categorie + '\'' +
                ", quantite=" + quantite +
                ", dateExpiration=" + dateExpiration +
                ", fournisseurId=" + fournisseurId +
                ", prix=" + prix +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicament)) return false;
        Medicament that = (Medicament) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
