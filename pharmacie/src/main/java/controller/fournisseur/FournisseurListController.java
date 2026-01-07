package controller.fournisseur;

import dao.FournisseurDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.Fournisseur;
import util.AlertUtil;
import util.ViewManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FournisseurListController implements Initializable {

    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<Fournisseur> tableFournisseurs;
    @FXML
    private TableColumn<Fournisseur, Integer> colId;
    @FXML
    private TableColumn<Fournisseur, String> colNom;
    @FXML
    private TableColumn<Fournisseur, String> colEmail;
    @FXML
    private TableColumn<Fournisseur, String> colTelephone;
    @FXML
    private TableColumn<Fournisseur, String> colAdresse;
    @FXML
    private TableColumn<Fournisseur, Void> colActions;

    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private ObservableList<Fournisseur> allFournisseurs;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));

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
    }

    @FXML
    public void loadData() {
        try {
            List<Fournisseur> fournisseurs = fournisseurDAO.getAll();
            allFournisseurs = FXCollections.observableArrayList(fournisseurs);
            tableFournisseurs.setItems(allFournisseurs);
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Impossible de charger les fournisseurs");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = txtSearch.getText().toLowerCase();
        ObservableList<Fournisseur> filtered = allFournisseurs.stream()
                .filter(f -> searchText.isEmpty() ||
                        f.getNom().toLowerCase().contains(searchText) ||
                        f.getEmail().toLowerCase().contains(searchText) ||
                        f.getTelephone().contains(searchText))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tableFournisseurs.setItems(filtered);
    }

    @FXML
    private void handleAdd() {
        ViewManager.loadView("/fxml/fournisseur/form.fxml");
    }

    private void handleEdit(Fournisseur fournisseur) {
        if (fournisseur != null) {
            FournisseurFormController controller = ViewManager.loadViewWithController("/fxml/fournisseur/form.fxml");
            if (controller != null) {
                controller.setFournisseur(fournisseur);
            }
        }
    }

    private void handleDelete(Fournisseur fournisseur) {
        if (fournisseur != null) {
            boolean confirm = AlertUtil.showConfirmation("Confirmation",
                    "Voulez-vous vraiment supprimer " + fournisseur.getNom() + " ?");
            if (confirm) {
                try {
                    fournisseurDAO.delete(fournisseur.getId());
                    AlertUtil.showInfo("Succès", "Fournisseur supprimé avec succès");
                    loadData();
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertUtil.showError("Erreur", "Impossible de supprimer le fournisseur");
                }
            }
        }
    }
}
