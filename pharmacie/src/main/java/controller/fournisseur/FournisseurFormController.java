package controller.fournisseur;

import dao.FournisseurDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Fournisseur;
import util.AlertUtil;
import util.ValidationUtil;
import util.ViewManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FournisseurFormController implements Initializable {

    @FXML
    private Label lblTitle;
    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtTelephone;
    @FXML
    private TextArea txtAdresse;
    @FXML
    private Button btnSave;

    @FXML
    private Label lblNomError;
    @FXML
    private Label lblEmailError;
    @FXML
    private Label lblTelephoneError;
    @FXML
    private Label lblAdresseError;

    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private Fournisseur currentFournisseur;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization if needed
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.currentFournisseur = fournisseur;
        this.isEditMode = true;
        lblTitle.setText("Modifier Fournisseur");

        // Populate fields
        txtNom.setText(fournisseur.getNom());
        txtEmail.setText(fournisseur.getEmail());
        txtTelephone.setText(fournisseur.getTelephone());
        txtAdresse.setText(fournisseur.getAdresse());
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        try {
            Fournisseur f = isEditMode ? currentFournisseur : new Fournisseur();
            f.setNom(txtNom.getText().trim());
            f.setEmail(txtEmail.getText().trim());
            f.setTelephone(txtTelephone.getText().trim());
            f.setAdresse(txtAdresse.getText().trim());

            if (isEditMode) {
                fournisseurDAO.update(f);
                AlertUtil.showInfo("Succès", "Fournisseur modifié avec succès");
            } else {
                fournisseurDAO.ajouter(f);
                AlertUtil.showInfo("Succès", "Fournisseur ajouté avec succès");
            }

            handleCancel();
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Impossible d'enregistrer le fournisseur");
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

        // Validate email
        if (!ValidationUtil.isValidEmail(txtEmail.getText())) {
            showError(lblEmailError, "Email invalide");
            valid = false;
        }

        // Validate telephone
        if (!ValidationUtil.isValidPhone(txtTelephone.getText())) {
            showError(lblTelephoneError, "Téléphone invalide (10 chiffres)");
            valid = false;
        }

        // Validate adresse
        if (!ValidationUtil.isNotEmpty(txtAdresse.getText())) {
            showError(lblAdresseError, "L'adresse est obligatoire");
            valid = false;
        }

        return valid;
    }

    private void hideAllErrors() {
        lblNomError.setVisible(false);
        lblEmailError.setVisible(false);
        lblTelephoneError.setVisible(false);
        lblAdresseError.setVisible(false);
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    @FXML
    private void handleCancel() {
        ViewManager.loadView("/fxml/fournisseur/list.fxml");
    }
}
