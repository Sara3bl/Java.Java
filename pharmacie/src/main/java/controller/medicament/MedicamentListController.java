package controller.medicament;

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

public class MedicamentListController implements Initializable {

    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cmbCategorie;
    @FXML
    private ComboBox<String> cmbFournisseur;
    @FXML
    private CheckBox chkExpires;
    @FXML
    private CheckBox chkStockFaible;
    @FXML
    private TableView<MedicamentWrapper> tableMedicaments;
    @FXML
    private TableColumn<MedicamentWrapper, Integer> colId;
    @FXML
    private TableColumn<MedicamentWrapper, String> colNom;
    @FXML
    private TableColumn<MedicamentWrapper, String> colCategorie;
    @FXML
    private TableColumn<MedicamentWrapper, Integer> colQuantite;
    @FXML
    private TableColumn<MedicamentWrapper, Double> colPrix;
    @FXML
    private TableColumn<MedicamentWrapper, LocalDate> colExpiration;
    @FXML
    private TableColumn<MedicamentWrapper, String> colFournisseur;
    @FXML
    private TableColumn<MedicamentWrapper, Void> colActions;

    private final MedicamentDAO medicamentDAO = new MedicamentDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private ObservableList<MedicamentWrapper> allMedicaments;
    private List<Fournisseur> fournisseurs;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadCategories();
        loadFournisseurs();
        loadData();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colExpiration.setCellValueFactory(new PropertyValueFactory<>("dateExpiration"));
        colFournisseur.setCellValueFactory(new PropertyValueFactory<>("fournisseurNom"));

        // Actions column with Edit and Delete buttons
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setOnAction(e -> handleEdit(getTableRow().getItem()));
                btnDelete.setOnAction(e -> handleDelete(getTableRow().getItem()));
                btnEdit.getStyleClass().add("btn-icon");
                btnDelete.getStyleClass().add("btn-icon");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        // Row styling for expired and low stock
        tableMedicaments.setRowFactory(tv -> new TableRow<MedicamentWrapper>() {
            @Override
            protected void updateItem(MedicamentWrapper item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    Medicament m = item.toMedicament();
                    if (m.estExpire()) {
                        getStyleClass().add("expired-row");
                    } else if (m.stockFaible(10)) {
                        getStyleClass().add("low-stock-row");
                    } else {
                        getStyleClass().removeAll("expired-row", "low-stock-row");
                    }
                }
            }
        });
    }

    private void loadCategories() {
        try {
            List<String> categories = medicamentDAO.getAll().stream()
                    .map(Medicament::getCategorie)
                    .distinct()
                    .collect(Collectors.toList());
            cmbCategorie.setItems(FXCollections.observableArrayList(categories));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFournisseurs() {
        try {
            fournisseurs = fournisseurDAO.getAll();
            List<String> nomsFournisseurs = fournisseurs.stream()
                    .map(Fournisseur::getNom)
                    .collect(Collectors.toList());
            cmbFournisseur.setItems(FXCollections.observableArrayList(nomsFournisseurs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadData() {
        try {
            List<Medicament> medicaments = medicamentDAO.getAll();
            allMedicaments = FXCollections.observableArrayList();

            for (Medicament m : medicaments) {
                String nomFournisseur = fournisseurs.stream()
                        .filter(f -> f.getId() == m.getFournisseurId())
                        .map(Fournisseur::getNom)
                        .findFirst()
                        .orElse("Inconnu");
                allMedicaments.add(new MedicamentWrapper(m, nomFournisseur));
            }

            tableMedicaments.setItems(allMedicaments);
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Impossible de charger les médicaments");
        }
    }

    @FXML
    private void handleSearch() {
        handleFilter();
    }

    @FXML
    private void handleFilter() {
        String searchText = txtSearch.getText().toLowerCase();
        String selectedCategorie = cmbCategorie.getValue();
        String selectedFournisseur = cmbFournisseur.getValue();
        boolean expiresOnly = chkExpires.isSelected();
        boolean stockFaibleOnly = chkStockFaible.isSelected();

        ObservableList<MedicamentWrapper> filtered = allMedicaments.stream()
                .filter(m -> searchText.isEmpty() ||
                        m.getNom().toLowerCase().contains(searchText))
                .filter(m -> selectedCategorie == null ||
                        m.getCategorie().equals(selectedCategorie))
                .filter(m -> selectedFournisseur == null ||
                        m.getFournisseurNom().equals(selectedFournisseur))
                .filter(m -> !expiresOnly || m.toMedicament().estExpire())
                .filter(m -> !stockFaibleOnly || m.toMedicament().stockFaible(10))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        tableMedicaments.setItems(filtered);
    }

    @FXML
    private void handleAdd() {
        ViewManager.loadView("/fxml/medicament/form.fxml");
    }

    private void handleEdit(MedicamentWrapper wrapper) {
        if (wrapper != null) {
            MedicamentFormController controller = ViewManager.loadViewWithController("/fxml/medicament/form.fxml");
            if (controller != null) {
                controller.setMedicament(wrapper.toMedicament());
            }
        }
    }

    private void handleDelete(MedicamentWrapper wrapper) {
        if (wrapper != null) {
            boolean confirm = AlertUtil.showConfirmation("Confirmation",
                    "Voulez-vous vraiment supprimer " + wrapper.getNom() + " ?");
            if (confirm) {
                try {
                    medicamentDAO.delete(wrapper.getId());
                    AlertUtil.showInfo("Succès", "Médicament supprimé avec succès");
                    loadData();
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertUtil.showError("Erreur", "Impossible de supprimer le médicament");
                }
            }
        }
    }
}
