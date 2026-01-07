package model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class MedicamentWrapper {

    private final IntegerProperty id;
    private final StringProperty nom;
    private final StringProperty categorie;
    private final IntegerProperty quantite;
    private final ObjectProperty<LocalDate> dateExpiration;
    private final IntegerProperty fournisseurId;
    private final DoubleProperty prix;
    private final StringProperty fournisseurNom;

    public MedicamentWrapper(Medicament m, String fournisseurNom) {
        this.id = new SimpleIntegerProperty(m.getId());
        this.nom = new SimpleStringProperty(m.getNom());
        this.categorie = new SimpleStringProperty(m.getCategorie());
        this.quantite = new SimpleIntegerProperty(m.getQuantite());
        this.dateExpiration = new SimpleObjectProperty<>(m.getDateExpiration());
        this.fournisseurId = new SimpleIntegerProperty(m.getFournisseurId());
        this.prix = new SimpleDoubleProperty(m.getPrix());
        this.fournisseurNom = new SimpleStringProperty(fournisseurNom);
    }

    // Getters for properties
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public StringProperty categorieProperty() {
        return categorie;
    }

    public IntegerProperty quantiteProperty() {
        return quantite;
    }

    public ObjectProperty<LocalDate> dateExpirationProperty() {
        return dateExpiration;
    }

    public IntegerProperty fournisseurIdProperty() {
        return fournisseurId;
    }

    public DoubleProperty prixProperty() {
        return prix;
    }

    public StringProperty fournisseurNomProperty() {
        return fournisseurNom;
    }

    // Standard getters
    public int getId() {
        return id.get();
    }

    public String getNom() {
        return nom.get();
    }

    public String getCategorie() {
        return categorie.get();
    }

    public int getQuantite() {
        return quantite.get();
    }

    public LocalDate getDateExpiration() {
        return dateExpiration.get();
    }

    public int getFournisseurId() {
        return fournisseurId.get();
    }

    public double getPrix() {
        return prix.get();
    }

    public String getFournisseurNom() {
        return fournisseurNom.get();
    }

    public Medicament toMedicament() {
        Medicament m = new Medicament();
        m.setId(getId());
        m.setNom(getNom());
        m.setCategorie(getCategorie());
        m.setQuantite(getQuantite());
        m.setDateExpiration(getDateExpiration());
        m.setFournisseurId(getFournisseurId());
        m.setPrix(getPrix());
        return m;
    }
}
