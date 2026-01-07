package controller.dashboard;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;
import util.AlertUtil;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
    @FXML
    private TableView<VenteWrapper> tableRecentSales;
    @FXML
    private TableColumn<VenteWrapper, String> colSaleMedicament;
    @FXML
    private TableColumn<VenteWrapper, Integer> colSaleQuantite;
    @FXML
    private TableColumn<VenteWrapper, Double> colSalePrix;
    @FXML
    private TableColumn<VenteWrapper, Double> colSaleTotal;
    @FXML
    private TableColumn<VenteWrapper, LocalDate> colSaleDate;

    private final MedicamentDAO medicamentDAO = new MedicamentDAO();
    private final VenteDAO venteDAO = new VenteDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadDashboardData();
    }

    private void setupTableColumns() {
        colSaleMedicament.setCellValueFactory(new PropertyValueFactory<>("medicamentNom"));
        colSaleQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteVendue"));
        colSalePrix.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        colSaleTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colSaleDate.setCellValueFactory(new PropertyValueFactory<>("dateVente"));
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

            // Load recent sales (last 10)
            ObservableList<VenteWrapper> recentSales = FXCollections.observableArrayList();
            List<Vente> last10 = ventes.stream()
                    .sorted((v1, v2) -> v2.getDateVente().compareTo(v1.getDateVente()))
                    .limit(10)
                    .collect(Collectors.toList());

            for (Vente v : last10) {
                Medicament m = medicamentDAO.getAll().stream()
                        .filter(med -> med.getId() == v.getMedicamentId())
                        .findFirst()
                        .orElse(null);
                String nomMed = m != null ? m.getNom() : "Inconnu";
                recentSales.add(new VenteWrapper(v, nomMed));
            }
            tableRecentSales.setItems(recentSales);

        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Impossible de charger les données du tableau de bord");
        }
    }
}
