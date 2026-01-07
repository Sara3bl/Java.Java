package dao;

import dao.DatabaseManager;
import model.Fournisseur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO {

    private final Connection conn = DatabaseManager.getConnection();

    // CREATE
    public void ajouter(Fournisseur f) throws SQLException {
        String sql = "INSERT INTO fournisseur (nom, email, telephone, adresse) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, f.getNom());
            stmt.setString(2, f.getEmail());
            stmt.setString(3, f.getTelephone());
            stmt.setString(4, f.getAdresse());
            stmt.executeUpdate();
        }
    }

    // READ (ALL)
    public List<Fournisseur> getAll() throws SQLException {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Fournisseur f = new Fournisseur();
                f.setId(rs.getInt("id"));
                f.setNom(rs.getString("nom"));
                f.setEmail(rs.getString("email"));
                f.setTelephone(rs.getString("telephone"));
                f.setAdresse(rs.getString("adresse"));
                fournisseurs.add(f);
            }
        }
        return fournisseurs;
    }

    // READ (BY ID)
    public Fournisseur getById(int id) throws SQLException {
        String sql = "SELECT * FROM fournisseur WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Fournisseur f = new Fournisseur();
                f.setId(rs.getInt("id"));
                f.setNom(rs.getString("nom"));
                f.setEmail(rs.getString("email"));
                f.setTelephone(rs.getString("telephone"));
                f.setAdresse(rs.getString("adresse"));
                return f;
            }
        }
        return null;
    }

    // UPDATE
    public void update(Fournisseur f) throws SQLException {
        String sql = "UPDATE fournisseur SET nom=?, email=?, telephone=?, adresse=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, f.getNom());
            stmt.setString(2, f.getEmail());
            stmt.setString(3, f.getTelephone());
            stmt.setString(4, f.getAdresse());
            stmt.setInt(5, f.getId());
            stmt.executeUpdate();
        }
    }

    // DELETE
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM fournisseur WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
