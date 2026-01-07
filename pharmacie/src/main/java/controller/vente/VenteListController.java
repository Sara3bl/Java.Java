package controller.vente;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.*;
import util.AlertUtil;
import util.ViewManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VenteListController implements Initializable {

    @FXML
    private TextField txtSearch;
    @FXML
    private DatePicker dateDebut;
    @FXML
    private DatePicker dateFin;
    @FXML
    private Label lblTotalVentes;
    @FXML
    private TableView<VenteWrapper> tableVentes;
    @FXML
    private TableColumn<VenteWrapper, Integer> colId;
    @FXML
    private TableColumn<VenteWrapper, String> colMedicament;
    @FXML
    private TableColumn<VenteWrapper, Integer> colQuantite;
    @FXML
    private TableColumn<VenteWrapper, Double> colPrixUnitaire;
    @FXML
    private TableColumn<VenteWrapper, Double> colTotal;
    @FXML
    private TableColumn<VenteWrapper, LocalDate> colDate;
    @FXML
    private TableColumn<VenteWrapper, Void> colActions;

    private final VenteDAO venteDAO = new VenteDAO();
    private final MedicamentDAO medicamentDAO = new MedicamentDAO();
    private ObservableList<VenteWrapper> allVentes;
    private List<Medicament> medicaments;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadMedicaments();
        loadData();

        // Set default date range (last 30 days)
        dateFin.setValue(LocalDate.now());
        dateDebut.setValue(LocalDate.now().minusDays(30));
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMedicament.setCellValueFactory(new PropertyValueFactory<>("medicamentNom"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteVendue"));
        colPrixUnitaire.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateVente"));

        // Actions column with Delete button
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("🗑️");
            private final HBox pane = new HBox(btnDelete);

            {
                btnDelete.setOnAction(e -> handleDelete(getTableRow().getItem()));
                btnDelete.getStyleClass().add("btn-icon");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void loadMedicaments() {
        try {
            medicaments = medicamentDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadData() {
        try {
            List<Vente> ventes = venteDAO.getAll();
            allVentes = FXCollections.observableArrayList();

            for (Vente v : ventes) {
                String nomMed = medicaments.stream()
                        .filter(m -> m.getId() == v.getMedicamentId())
                        .map(Medicament::getNom)
                        .findFirst()
                        .orElse("Inconnu");
                allVentes.add(new VenteWrapper(v, nomMed));
            }

            tableVentes.setItems(allVentes);
            updateTotalVentes(allVentes);
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Impossible de charger les ventes");
        }
    }

    @FXML
    private void handleSearch() {
        handleFilter();
    }

    @FXML
    private void handleFilter() {
        String searchText = txtSearch.getText().toLowerCase();
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();

        ObservableList<VenteWrapper> filtered = allVentes.stream()
                .filter(v -> searchText.isEmpty() ||
                        v.getMedicamentNom().toLowerCase().contains(searchText))
                .filter(v -> debut == null || !v.getDateVente().isBefore(debut))
                .filter(v -> fin == null || !v.getDateVente().isAfter(fin))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        tableVentes.setItems(filtered);
        updateTotalVentes(filtered);
    }

    private void updateTotalVentes(ObservableList<VenteWrapper> ventes) {
        double total = ventes.stream()
                .mapToDouble(VenteWrapper::getTotal)
                .sum();
        lblTotalVentes.setText(String.format("Total: %.2f DH", total));
    }

    @FXML
    private void handleAdd() {
        ViewManager.loadView("/fxml/vente/form.fxml");
    }

    private void handleDelete(VenteWrapper wrapper) {
        if (wrapper != null) {
            boolean confirm = AlertUtil.showConfirmation("Confirmation",
                    "Voulez-vous vraiment supprimer cette vente ?");
            if (confirm) {
                try {
                    venteDAO.delete(wrapper.getId());
                    AlertUtil.showInfo("Succès", "Vente supprimée avec succès");
                    loadData();
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertUtil.showError("Erreur", "Impossible de supprimer la vente");
                }
            }
        }
    }
}
