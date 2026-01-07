package dao;

import model.Medicament;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentDAO {

    private Connection conn = DatabaseManager.getConnection();

    public void ajouter(Medicament m) throws SQLException {
        String sql = "INSERT INTO medicament (nom, categorie, quantite, date_expiration, fournisseur_id, prix) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getNom());
            stmt.setString(2, m.getCategorie());
            stmt.setInt(3, m.getQuantite());
            stmt.setDate(4, Date.valueOf(m.getDateExpiration()));
            stmt.setInt(5, m.getFournisseurId());
            stmt.setDouble(6, m.getPrix());
            stmt.executeUpdate();
        }
    }

    public List<Medicament> getAll() throws SQLException {
        List<Medicament> list = new ArrayList<>();
        String sql = "SELECT * FROM medicament";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Medicament m = new Medicament();
                m.setId(rs.getInt("id"));
                m.setNom(rs.getString("nom"));
                m.setCategorie(rs.getString("categorie"));
                m.setQuantite(rs.getInt("quantite"));
                m.setDateExpiration(rs.getDate("date_expiration").toLocalDate());
                m.setFournisseurId(rs.getInt("fournisseur_id"));
                m.setPrix(rs.getDouble("prix"));
                list.add(m);
            }
        }
        return list;
    }

    public void update(Medicament m) throws SQLException {
        String sql = "UPDATE medicament SET nom=?, categorie=?, quantite=?, date_expiration=?, fournisseur_id=?, prix=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getNom());
            stmt.setString(2, m.getCategorie());
            stmt.setInt(3, m.getQuantite());
            stmt.setDate(4, Date.valueOf(m.getDateExpiration()));
            stmt.setInt(5, m.getFournisseurId());
            stmt.setDouble(6, m.getPrix());
            stmt.setInt(7, m.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM medicament WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
