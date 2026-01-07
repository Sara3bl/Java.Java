package controller.vente;

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

public class VenteFormController implements Initializable {

    @FXML
    private ComboBox<Medicament> cmbMedicament;
    @FXML
    private Label lblStockDisponible;
    @FXML
    private Label lblPrixUnitaire;
    @FXML
    private TextField txtQuantite;
    @FXML
    private DatePicker dateVente;
    @FXML
    private Label lblTotal;
    @FXML
    private Button btnSave;

    @FXML
    private Label lblMedicamentError;
    @FXML
    private Label lblQuantiteError;

    private final MedicamentDAO medicamentDAO = new MedicamentDAO();
    private final VenteDAO venteDAO = new VenteDAO();
    private Medicament selectedMedicament;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadMedicaments();
        dateVente.setValue(LocalDate.now());
    }

    private void loadMedicaments() {
        try {
            List<Medicament> medicaments = medicamentDAO.getAll();
            cmbMedicament.setItems(FXCollections.observableArrayList(medicaments));

            // Custom string converter
            cmbMedicament.setConverter(new javafx.util.StringConverter<Medicament>() {
                @Override
                public String toString(Medicament m) {
                    return m != null ? m.getNom() + " (" + m.getQuantite() + " en stock)" : "";
                }

                @Override
                public Medicament fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Impossible de charger les médicaments");
        }
    }

    @FXML
    private void handleMedicamentSelection() {
        selectedMedicament = cmbMedicament.getValue();
        if (selectedMedicament != null) {
            lblStockDisponible.setText(String.valueOf(selectedMedicament.getQuantite()));
            lblPrixUnitaire.setText(String.format("%.2f DH", selectedMedicament.getPrix()));
            calculateTotal();
        }
    }

    @FXML
    private void calculateTotal() {
        if (selectedMedicament != null && ValidationUtil.isPositiveInteger(txtQuantite.getText())) {
            int quantite = Integer.parseInt(txtQuantite.getText());
            double total = quantite * selectedMedicament.getPrix();
            lblTotal.setText(String.format("%.2f DH", total));
        } else {
            lblTotal.setText("0.00 DH");
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        try {
            Vente v = new Vente();
            v.setMedicamentId(selectedMedicament.getId());
            v.setQuantiteVendue(Integer.parseInt(txtQuantite.getText().trim()));
            v.setPrixUnitaire(selectedMedicament.getPrix());
            v.setDateVente(dateVente.getValue());

            // Check stock availability
            if (v.getQuantiteVendue() > selectedMedicament.getQuantite()) {
                AlertUtil.showError("Erreur", "Stock insuffisant!");
                return;
            }

            // Save vente
            venteDAO.ajouter(v);

            // Update medicament stock
            selectedMedicament.setQuantite(selectedMedicament.getQuantite() - v.getQuantiteVendue());
            medicamentDAO.update(selectedMedicament);

            AlertUtil.showInfo("Succès", "Vente enregistrée avec succès");
            handleCancel();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Impossible d'enregistrer la vente");
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        hideAllErrors();

        // Validate medicament
        if (selectedMedicament == null) {
            showError(lblMedicamentError, "Sélectionnez un médicament");
            valid = false;
        }

        // Validate quantite
        if (!ValidationUtil.isPositiveInteger(txtQuantite.getText())) {
            showError(lblQuantiteError, "Quantité invalide");
            valid = false;
        } else {
            int quantite = Integer.parseInt(txtQuantite.getText());
            if (selectedMedicament != null && quantite > selectedMedicament.getQuantite()) {
                showError(lblQuantiteError, "Stock insuffisant");
                valid = false;
            }
        }

        return valid;
    }

    private void hideAllErrors() {
        lblMedicamentError.setVisible(false);
        lblQuantiteError.setVisible(false);
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    @FXML
    private void handleCancel() {
        ViewManager.loadView("/fxml/vente/list.fxml");
    }
}
