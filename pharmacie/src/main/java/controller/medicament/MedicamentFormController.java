package controller.medicament;

import dao.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.*;
import util.AlertUtil;
import util.ValidationUtil;
import util.ViewManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MedicamentFormController implements Initializable {

    @FXML
    private Label lblTitle;
    @FXML
    private TextField txtNom;
    @FXML
    private ComboBox<String> cmbCategorie;
    @FXML
    private TextField txtQuantite;
    @FXML
    private TextField txtPrix;
    @FXML
    private DatePicker dateExpiration;
    @FXML
    private ComboBox<Fournisseur> cmbFournisseur;
    @FXML
    private Button btnSave;

    @FXML
    private Label lblNomError;
    @FXML
    private Label lblCategorieError;
    @FXML
    private Label lblQuantiteError;
    @FXML
    private Label lblPrixError;
    @FXML
    private Label lblExpirationError;
    @FXML
    private Label lblFournisseurError;

    private final MedicamentDAO medicamentDAO = new MedicamentDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private Medicament currentMedicament;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadCategories();
        loadFournisseurs();
        dateExpiration.setValue(LocalDate.now().plusYears(1));
    }

    private void loadCategories() {
        try {
            List<String> categories = medicamentDAO.getAll().stream()
                    .map(Medicament::getCategorie)
                    .distinct()
                    .collect(Collectors.toList());

            // Add common categories
            if (!categories.contains("Antibiotique"))
                categories.add("Antibiotique");
            if (!categories.contains("Antalgique"))
                categories.add("Antalgique");
            if (!categories.contains("Anti-inflammatoire"))
                categories.add("Anti-inflammatoire");

            cmbCategorie.setItems(FXCollections.observableArrayList(categories));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFournisseurs() {
        try {
            List<Fournisseur> fournisseurs = fournisseurDAO.getAll();
            cmbFournisseur.setItems(FXCollections.observableArrayList(fournisseurs));

            // Custom string converter for display
            cmbFournisseur.setConverter(new javafx.util.StringConverter<Fournisseur>() {
                @Override
                public String toString(Fournisseur f) {
                    return f != null ? f.getNom() : "";
                }

                @Override
                public Fournisseur fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Impossible de charger les fournisseurs");
        }
    }

    public void setMedicament(Medicament medicament) {
        this.currentMedicament = medicament;
        this.isEditMode = true;
        lblTitle.setText("Modifier Médicament");

        // Populate fields
        txtNom.setText(medicament.getNom());
        cmbCategorie.setValue(medicament.getCategorie());
        txtQuantite.setText(String.valueOf(medicament.getQuantite()));
        txtPrix.setText(String.valueOf(medicament.getPrix()));
        dateExpiration.setValue(medicament.getDateExpiration());

        // Select fournisseur
        cmbFournisseur.getItems().stream()
                .filter(f -> f.getId() == medicament.getFournisseurId())
                .findFirst()
                .ifPresent(cmbFournisseur::setValue);
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        try {
            Medicament m = isEditMode ? currentMedicament : new Medicament();
            m.setNom(txtNom.getText().trim());
            m.setCategorie(cmbCategorie.getValue());
            m.setQuantite(Integer.parseInt(txtQuantite.getText().trim()));
            m.setPrix(Double.parseDouble(txtPrix.getText().trim()));
            m.setDateExpiration(dateExpiration.getValue());
            m.setFournisseurId(cmbFournisseur.getValue().getId());

            if (isEditMode) {
                medicamentDAO.update(m);
                AlertUtil.showInfo("Succès", "Médicament modifié avec succès");
            } else {
                medicamentDAO.ajouter(m);
                AlertUtil.showInfo("Succès", "Médicament ajouté avec succès");
            }

            handleCancel();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Impossible d'enregistrer le médicament");
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        hideAllErrors();

        // Validate nom
        if (!ValidationUtil.isNotEmpty(txtNom.getText())) {
            showError(lblNomError, "Le nom est obligatoire");
            valid = false;
        }

        // Validate categorie
        if (cmbCategorie.getValue() == null || cmbCategorie.getValue().isEmpty()) {
            showError(lblCategorieError, "La catégorie est obligatoire");
            valid = false;
        }

        // Validate quantite
        if (!ValidationUtil.isPositiveInteger(txtQuantite.getText())) {
            showError(lblQuantiteError, "Quantité invalide");
            valid = false;
        }

        // Validate prix
        if (!ValidationUtil.isPositiveNumber(txtPrix.getText())) {
            showError(lblPrixError, "Prix invalide");
            valid = false;
        }

        // Validate expiration date
        if (dateExpiration.getValue() == null) {
            showError(lblExpirationError, "Date obligatoire");
            valid = false;
        } else if (!ValidationUtil.isFutureDate(dateExpiration.getValue())) {
            showError(lblExpirationError, "La date doit être future");
            valid = false;
        }

        // Validate fournisseur
        if (cmbFournisseur.getValue() == null) {
            showError(lblFournisseurError, "Sélectionnez un fournisseur");
            valid = false;
        }

        return valid;
    }

    private void hideAllErrors() {
        lblNomError.setVisible(false);
        lblCategorieError.setVisible(false);
        lblQuantiteError.setVisible(false);
        lblPrixError.setVisible(false);
        lblExpirationError.setVisible(false);
        lblFournisseurError.setVisible(false);
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    @FXML
    private void handleCancel() {
        ViewManager.loadView("/fxml/medicament/list.fxml");
    }
}
