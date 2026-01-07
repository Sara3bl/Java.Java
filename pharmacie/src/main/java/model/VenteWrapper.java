package model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class VenteWrapper {

    private final IntegerProperty id;
    private final IntegerProperty medicamentId;
    private final StringProperty medicamentNom;
    private final IntegerProperty quantiteVendue;
    private final DoubleProperty prixUnitaire;
    private final ObjectProperty<LocalDate> dateVente;
    private final DoubleProperty total;

    public VenteWrapper(Vente v, String medicamentNom) {
        this.id = new SimpleIntegerProperty(v.getId());
        this.medicamentId = new SimpleIntegerProperty(v.getMedicamentId());
        this.medicamentNom = new SimpleStringProperty(medicamentNom);
        this.quantiteVendue = new SimpleIntegerProperty(v.getQuantiteVendue());
        this.prixUnitaire = new SimpleDoubleProperty(v.getPrixUnitaire());
        this.dateVente = new SimpleObjectProperty<>(v.getDateVente());
        this.total = new SimpleDoubleProperty(v.getTotal());
    }

    // Property getters
    public IntegerProperty idProperty() {
        return id;
    }

    public IntegerProperty medicamentIdProperty() {
        return medicamentId;
    }

    public StringProperty medicamentNomProperty() {
        return medicamentNom;
    }

    public IntegerProperty quantiteVendueProperty() {
        return quantiteVendue;
    }

    public DoubleProperty prixUnitaireProperty() {
        return prixUnitaire;
    }

    public ObjectProperty<LocalDate> dateVenteProperty() {
        return dateVente;
    }

    public DoubleProperty totalProperty() {
        return total;
    }

    // Standard getters
    public int getId() {
        return id.get();
    }

    public int getMedicamentId() {
        return medicamentId.get();
    }

    public String getMedicamentNom() {
        return medicamentNom.get();
    }

    public int getQuantiteVendue() {
        return quantiteVendue.get();
    }

    public double getPrixUnitaire() {
        return prixUnitaire.get();
    }

    public LocalDate getDateVente() {
        return dateVente.get();
    }

    public double getTotal() {
        return total.get();
    }

    public Vente toVente() {
        Vente v = new Vente();
        v.setId(getId());
        v.setMedicamentId(getMedicamentId());
        v.setQuantiteVendue(getQuantiteVendue());
        v.setPrixUnitaire(getPrixUnitaire());
        v.setDateVente(getDateVente());
        return v;
    }
}
