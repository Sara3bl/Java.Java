package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import util.ViewManager;
import dao.DatabaseManager;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private BorderPane mainContainer;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblDate;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set the main container for ViewManager
        ViewManager.setMainContainer(mainContainer);

        // Test database connection
        try {
            DatabaseManager.getConnection();
            lblStatus.setText("✅ Connecté à la base de données");
        } catch (Exception e) {
            lblStatus.setText("❌ Erreur de connexion");
            lblStatus.setStyle("-fx-text-fill: red;");
        }

        // Set current date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblDate.setText(LocalDate.now().format(formatter));

        // Load dashboard by default
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        ViewManager.loadView("/fxml/dashboard/dashboard.fxml");
    }

    @FXML
    private void showMedicaments() {
        ViewManager.loadView("/fxml/medicament/list.fxml");
    }

    @FXML
    private void showFournisseurs() {
        ViewManager.loadView("/fxml/fournisseur/list.fxml");
    }

    @FXML
    private void showVentes() {
        ViewManager.loadView("/fxml/vente/list.fxml");
    }
}
