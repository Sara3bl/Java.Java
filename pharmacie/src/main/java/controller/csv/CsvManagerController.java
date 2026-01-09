package controller.csv;

import dao.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import model.*;
import util.AlertUtil;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CsvManagerController implements Initializable {

    @FXML
    private ComboBox<String> cmbExportType;
    @FXML
    private ComboBox<String> cmbImportType;
    @FXML
    private TextField txtImportFile;
    @FXML
    private Button btnExport;
    @FXML
    private Button btnImport;
    @FXML
    private Label lblExportStatus;
    @FXML
    private Label lblImportStatus;

    private final MedicamentDAO medicamentDAO = new MedicamentDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private final VenteDAO venteDAO = new VenteDAO();
    private File selectedFile;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate ComboBoxes
        cmbExportType.setItems(FXCollections.observableArrayList(
                "Médicaments", "Fournisseurs", "Ventes"
        ));
        cmbImportType.setItems(FXCollections.observableArrayList(
                "Médicaments", "Fournisseurs", "Ventes"
        ));
    }

    @FXML
    public void handleExport() {
        String type = cmbExportType.getValue();
        if (type == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un type de données à exporter");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder le fichier CSV");
        fileChooser.setInitialFileName(type.toLowerCase() + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv")
        );

        File file = fileChooser.showSaveDialog(btnExport.getScene().getWindow());
        if (file != null) {
            try {
                switch (type) {
                    case "Médicaments":
                        exportMedicaments(file);
                        break;
                    case "Fournisseurs":
                        exportFournisseurs(file);
                        break;
                    case "Ventes":
                        exportVentes(file);
                        break;
                }
                lblExportStatus.setText("✅ Export réussi: " + file.getName());
                AlertUtil.showSuccess("Succès", "Données exportées avec succès!");
            } catch (Exception e) {
                e.printStackTrace();
                lblExportStatus.setText("❌ Erreur lors de l'export");
                AlertUtil.showError("Erreur", "Impossible d'exporter les données: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv")
        );

        selectedFile = fileChooser.showOpenDialog(txtImportFile.getScene().getWindow());
        if (selectedFile != null) {
            txtImportFile.setText(selectedFile.getAbsolutePath());
            lblImportStatus.setText("");
        }
    }

    @FXML
    public void handleImport() {
        String type = cmbImportType.getValue();
        if (type == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un type de données à importer");
            return;
        }

        if (selectedFile == null || !selectedFile.exists()) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un fichier CSV valide");
            return;
        }

        try {
            int count = 0;
            switch (type) {
                case "Médicaments":
                    count = importMedicaments(selectedFile);
                    break;
                case "Fournisseurs":
                    count = importFournisseurs(selectedFile);
                    break;
                case "Ventes":
                    count = importVentes(selectedFile);
                    break;
            }
            lblImportStatus.setText("✅ Import réussi: " + count + " enregistrement(s) ajouté(s)");
            AlertUtil.showSuccess("Succès", count + " enregistrement(s) importé(s) avec succès!");
        } catch (Exception e) {
            e.printStackTrace();
            lblImportStatus.setText("❌ Erreur lors de l'import");
            AlertUtil.showError("Erreur", "Impossible d'importer les données: " + e.getMessage());
        }
    }

    private void exportMedicaments(File file) throws SQLException, IOException {
        List<Medicament> medicaments = medicamentDAO.getAll();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            
            // Header
            writer.write("id,nom,categorie,quantite,prix,date_expiration,fournisseur_id");
            writer.newLine();

            // Data
            for (Medicament m : medicaments) {
                writer.write(String.format("%d,%s,%s,%d,%.2f,%s,%d",
                        m.getId(),
                        escapeCsv(m.getNom()),
                        escapeCsv(m.getCategorie()),
                        m.getQuantite(),
                        m.getPrix(),
                        m.getDateExpiration(),
                        m.getFournisseurId()));
                writer.newLine();
            }
        }
    }

    private void exportFournisseurs(File file) throws SQLException, IOException {
        List<Fournisseur> fournisseurs = fournisseurDAO.getAll();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            
            // Header
            writer.write("id,nom,telephone,email,adresse");
            writer.newLine();

            // Data
            for (Fournisseur f : fournisseurs) {
                writer.write(String.format("%d,%s,%s,%s,%s",
                        f.getId(),
                        escapeCsv(f.getNom()),
                        escapeCsv(f.getTelephone()),
                        escapeCsv(f.getEmail()),
                        escapeCsv(f.getAdresse())));
                writer.newLine();
            }
        }
    }

    private void exportVentes(File file) throws SQLException, IOException {
        List<Vente> ventes = venteDAO.getAll();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            
            // Header
            writer.write("id,medicament_id,quantite_vendue,prix_unitaire,date_vente,total");
            writer.newLine();

            // Data
            for (Vente v : ventes) {
                writer.write(String.format("%d,%d,%d,%.2f,%s,%.2f",
                        v.getId(),
                        v.getMedicamentId(),
                        v.getQuantiteVendue(),
                        v.getPrixUnitaire(),
                        v.getDateVente(),
                        v.getTotal()));
                writer.newLine();
            }
        }
    }

    private int importMedicaments(File file) throws IOException, SQLException {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    Medicament m = new Medicament();
                    m.setNom(parts[0].trim());
                    m.setCategorie(parts[1].trim());
                    m.setQuantite(Integer.parseInt(parts[2].trim()));
                    m.setPrix(Double.parseDouble(parts[3].trim()));
                    m.setDateExpiration(LocalDate.parse(parts[4].trim()));
                    m.setFournisseurId(Integer.parseInt(parts[5].trim()));
                    
                    medicamentDAO.ajouter(m);
                    count++;
                }
            }
        }
        return count;
    }

    private int importFournisseurs(File file) throws IOException, SQLException {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Fournisseur f = new Fournisseur();
                    f.setNom(parts[0].trim());
                    f.setTelephone(parts[1].trim());
                    f.setEmail(parts[2].trim());
                    f.setAdresse(parts[3].trim());
                    
                    fournisseurDAO.ajouter(f);
                    count++;
                }
            }
        }
        return count;
    }

    private int importVentes(File file) throws IOException, SQLException {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Vente v = new Vente();
                    v.setMedicamentId(Integer.parseInt(parts[0].trim()));
                    v.setQuantiteVendue(Integer.parseInt(parts[1].trim()));
                    v.setPrixUnitaire(Double.parseDouble(parts[2].trim()));
                    v.setDateVente(LocalDate.parse(parts[3].trim()));
                    
                    venteDAO.ajouter(v);
                    count++;
                }
            }
        }
        return count;
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
