CREATE DATABASE IF NOT EXISTS pharmacie_db;
USE pharmacie_db;

CREATE TABLE IF NOT EXISTS fournisseur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    adresse TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS medicament (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    categorie VARCHAR(50) NOT NULL,
    quantite INT NOT NULL,
    date_expiration DATE NOT NULL,
    fournisseur_id INT NOT NULL,
    prix DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (fournisseur_id) REFERENCES fournisseur(id)
);

CREATE TABLE IF NOT EXISTS vente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medicament_id INT NOT NULL,
    quantite_vendue INT NOT NULL,
    prix_unitaire DECIMAL(10, 2) NOT NULL,
    date_vente DATE NOT NULL,
    FOREIGN KEY (medicament_id) REFERENCES medicament(id)
);

-- Sample data
INSERT IGNORE INTO fournisseur (nom, email, telephone, adresse) VALUES
('Pharma Suppliers', 'contact@pharma.com', '0612345678', '123 Rue Medicale, Casablanca'),
('MediCorp', 'info@medicorp.com', '0623456789', '456 Avenue Santé, Rabat');

INSERT IGNORE INTO medicament (nom, categorie, quantite, date_expiration, fournisseur_id, prix) VALUES
('Paracétamol 500mg', 'Antalgique', 150, '2026-12-31', 1, 25.00),
('Amoxicilline 1g', 'Antibiotique', 80, '2025-06-30', 2, 45.50),
('Ibuprofène 400mg', 'Anti-inflammatoire', 5, '2025-03-15', 1, 30.00),
('Aspirine 100mg', 'Antalgique', 200, '2024-12-01', 2, 15.00);
