package controller.dashboard;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.*;
import util.AlertUtil;
import util.ViewManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label lblTotalMedicaments;
    @FXML
    private Label lblStockFaible;
    @FXML
    private Label lblExpires;
    @FXML
    private Label lblVentesJour;
    @FXML
    private ListView<String> listAlerts;

    private final MedicamentDAO medicamentDAO = new MedicamentDAO();
    private final VenteDAO venteDAO = new VenteDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadDashboardData();
    }

    private void loadDashboardData() {
        try {
            // Load medications
            List<Medicament> medicaments = medicamentDAO.getAll();
            lblTotalMedicaments.setText(String.valueOf(medicaments.size()));

            // Count low stock (threshold: 10)
            long lowStock = medicaments.stream()
                    .filter(m -> m.getQuantite() < 10)
                    .count();
            lblStockFaible.setText(String.valueOf(lowStock));

            // Count expired
            long expired = medicaments.stream()
                    .filter(Medicament::estExpire)
                    .count();
            lblExpires.setText(String.valueOf(expired));

            // Calculate today's sales
            List<Vente> ventes = venteDAO.getAll();
            
            double ventesJour = ventes.stream()
                    .filter(v -> v.getDateVente().equals(LocalDate.now()))
                    .mapToDouble(Vente::getTotal)
                    .sum();
            lblVentesJour.setText(String.format("%.2f DH", ventesJour));

            // Load alerts
            ObservableList<String> alerts = FXCollections.observableArrayList();
            if (expired > 0) {
                alerts.add("⚠️ " + expired + " médicament(s) expiré(s)");
            }
            if (lowStock > 0) {
                alerts.add("⚠️ " + lowStock + " médicament(s) en stock faible");
            }
            medicaments.stream()
                    .filter(m -> m.getDateExpiration() != null &&
                            m.getDateExpiration().isBefore(LocalDate.now().plusMonths(1)) &&
                            !m.estExpire())
                    .forEach(m -> alerts.add("⏰ " + m.getNom() + " expire bientôt"));

            if (alerts.isEmpty()) {
                alerts.add("✅ Aucune alerte");
            }
            listAlerts.setItems(alerts);

        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Impossible de charger les données du tableau de bord");
        }
    }

    @FXML
    private void handleNewSale() {
        ViewManager.loadView("/fxml/vente/form.fxml");
    }

    @FXML
    private void handleNewMedicament() {
        ViewManager.loadView("/fxml/medicament/form.fxml");
    }

    @FXML
    private void handleNewFournisseur() {
        ViewManager.loadView("/fxml/fournisseur/form.fxml");
    }

    @FXML
    private void handleExportCSV() {
        ViewManager.loadView("/fxml/csv/manager.fxml");
    }
}
