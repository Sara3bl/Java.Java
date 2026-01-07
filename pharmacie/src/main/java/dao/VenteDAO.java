package dao;

import model.Vente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenteDAO {

    private final Connection conn = DatabaseManager.getConnection();

    // CREATE
    public void ajouter(Vente v) throws SQLException {
        String sql = "INSERT INTO vente (medicament_id, quantite_vendue, prix_unitaire, date_vente) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, v.getMedicamentId());
            stmt.setInt(2, v.getQuantiteVendue());
            stmt.setDouble(3, v.getPrixUnitaire());
            stmt.setDate(4, Date.valueOf(v.getDateVente()));
            stmt.executeUpdate();
        }
    }

    // READ (ALL)
    public List<Vente> getAll() throws SQLException {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM vente";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vente v = new Vente();
                v.setId(rs.getInt("id"));
                v.setMedicamentId(rs.getInt("medicament_id"));
                v.setQuantiteVendue(rs.getInt("quantite_vendue"));
                v.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                v.setDateVente(rs.getDate("date_vente").toLocalDate());
                ventes.add(v);
            }
        }
        return ventes;
    }

    // READ (BY ID)
    public Vente getById(int id) throws SQLException {
        String sql = "SELECT * FROM vente WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Vente v = new Vente();
                v.setId(rs.getInt("id"));
                v.setMedicamentId(rs.getInt("medicament_id"));
                v.setQuantiteVendue(rs.getInt("quantite_vendue"));
                v.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                v.setDateVente(rs.getDate("date_vente").toLocalDate());
                return v;
            }
        }
        return null;
    }

    // UPDATE
    public void update(Vente v) throws SQLException {
        String sql = "UPDATE vente SET medicament_id=?, quantite_vendue=?, prix_unitaire=?, date_vente=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, v.getMedicamentId());
            stmt.setInt(2, v.getQuantiteVendue());
            stmt.setDouble(3, v.getPrixUnitaire());
            stmt.setDate(4, Date.valueOf(v.getDateVente()));
            stmt.setInt(5, v.getId());
            stmt.executeUpdate();
        }
    }

    // DELETE
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM vente WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
