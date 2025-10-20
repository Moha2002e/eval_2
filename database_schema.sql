-- ========================================================================
-- SCHÉMA DE BASE DE DONNÉES - SYSTÈME DE GESTION MÉDICALE
-- ========================================================================
-- Projet : Système de gestion de consultations médicales
-- Base de données : MySQL
-- Encodage : UTF-8 (utf8mb4_unicode_ci)
-- ========================================================================

-- Création de la base de données
CREATE DATABASE IF NOT EXISTS PourStudent 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE PourStudent;

-- ========================================================================
-- TABLE : SPECIALTIES (Spécialités médicales)
-- ========================================================================
-- Stocke les différentes spécialités médicales disponibles
-- ========================================================================

CREATE TABLE IF NOT EXISTS specialties (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Nom de la spécialité médicale',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Date de création',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Date de dernière modification'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Table des spécialités médicales';

-- ========================================================================
-- TABLE : PATIENT (Patients)
-- ========================================================================
-- Stocke les informations des patients
-- ========================================================================

CREATE TABLE IF NOT EXISTS patient (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL COMMENT 'Prénom du patient',
    last_name VARCHAR(100) NOT NULL COMMENT 'Nom de famille du patient',
    birth_date DATE COMMENT 'Date de naissance du patient',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Date de création',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Date de dernière modification',
    
    -- Index pour optimiser les recherches
    INDEX idx_last_name (last_name),
    INDEX idx_birth_date (birth_date),
    INDEX idx_full_name (first_name, last_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Table des patients';

-- ========================================================================
-- TABLE : DOCTOR (Médecins)
-- ========================================================================
-- Stocke les informations des médecins
-- ========================================================================

CREATE TABLE IF NOT EXISTS doctor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL COMMENT 'Prénom du médecin',
    last_name VARCHAR(100) NOT NULL COMMENT 'Nom de famille du médecin',
    password VARCHAR(255) NOT NULL COMMENT 'Mot de passe du médecin (hashé)',
    specialite_id INT COMMENT 'ID de la spécialité du médecin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Date de création',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Date de dernière modification',
    
    -- Clé étrangère vers la table specialties
    FOREIGN KEY (specialite_id) REFERENCES specialties(id) ON DELETE SET NULL,
    
    -- Index pour optimiser les recherches
    INDEX idx_last_name (last_name),
    INDEX idx_specialite (specialite_id),
    INDEX idx_full_name (first_name, last_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Table des médecins';

-- ========================================================================
-- TABLE : CONSULTATIONS (Consultations médicales)
-- ========================================================================
-- Stocke les rendez-vous et consultations entre médecins et patients
-- ========================================================================

CREATE TABLE IF NOT EXISTS consultations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL COMMENT 'ID du patient',
    doctor_id INT NOT NULL COMMENT 'ID du médecin',
    date DATE NOT NULL COMMENT 'Date de la consultation',
    hour TIME COMMENT 'Heure de la consultation',
    reason TEXT COMMENT 'Motif de la consultation',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Date de création',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Date de dernière modification',
    
    -- Clés étrangères
    FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE,
    
    -- Index pour optimiser les recherches
    INDEX idx_date (date),
    INDEX idx_patient (patient_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_doctor_date (doctor_id, date),
    INDEX idx_patient_date (patient_id, date),
    
    -- Contrainte unique pour éviter les doublons de rendez-vous
    UNIQUE KEY unique_appointment (doctor_id, date, hour)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Table des consultations médicales';

-- ========================================================================
-- DONNÉES INITIALES - SPÉCIALITÉS MÉDICALES
-- ========================================================================

INSERT INTO specialties (name) VALUES 
('Cardiologie'),
('Pédiatrie'),
('Dermatologie'),
('Neurologie'),
('Psychiatrie'),
('Médecine Générale'),
('Chirurgie'),
('Ophtalmologie')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ========================================================================
-- DONNÉES INITIALES - PATIENTS DE TEST
-- ========================================================================

INSERT INTO patient (first_name, last_name, birth_date) VALUES 
('Marie', 'Dupont', '1985-03-15'),
('Jean', 'Martin', '1978-07-22'),
('Sophie', 'Bernard', '1992-11-08'),
('Pierre', 'Dubois', '1965-05-30'),
('Julie', 'Lefebvre', '1988-09-17')
ON DUPLICATE KEY UPDATE first_name = VALUES(first_name);

-- ========================================================================
-- DONNÉES INITIALES - MÉDECINS DE TEST
-- ========================================================================

INSERT INTO doctor (first_name, last_name, password, specialite_id) VALUES 
('Dr. Antoine', 'Rousseau', 'password123', 1),
('Dr. Claire', 'Moreau', 'password123', 2),
('Dr. Thomas', 'Lambert', 'password123', 6),
('Dr. Isabelle', 'Petit', 'password123', 3)
ON DUPLICATE KEY UPDATE first_name = VALUES(first_name);

-- ========================================================================
-- DONNÉES INITIALES - CONSULTATIONS DE TEST
-- ========================================================================

INSERT INTO consultations (patient_id, doctor_id, date, hour, reason) VALUES 
(1, 1, '2024-01-15', '09:00:00', 'Contrôle cardiaque'),
(2, 2, '2024-01-15', '10:30:00', 'Consultation pédiatrique'),
(3, 3, '2024-01-16', '14:00:00', 'Consultation générale'),
(4, 1, '2024-01-16', '15:30:00', 'Examen cardiologique'),
(5, 4, '2024-01-17', '11:00:00', 'Problème de peau')
ON DUPLICATE KEY UPDATE reason = VALUES(reason);

-- ========================================================================
-- VUES UTILES POUR LES REQUÊTES COURANTES
-- ========================================================================

-- Vue pour afficher les consultations avec les noms des patients et médecins
CREATE OR REPLACE VIEW consultations_details AS
SELECT 
    c.id,
    c.date,
    c.hour,
    c.reason,
    CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
    CONCAT(d.first_name, ' ', d.last_name) AS doctor_name,
    s.name AS specialty_name
FROM consultations c
JOIN patient p ON c.patient_id = p.id
JOIN doctor d ON c.doctor_id = d.id
LEFT JOIN specialties s ON d.specialite_id = s.id
ORDER BY c.date DESC, c.hour DESC;

-- Vue pour afficher les médecins avec leurs spécialités
CREATE OR REPLACE VIEW doctors_with_specialties AS
SELECT 
    d.id,
    CONCAT(d.first_name, ' ', d.last_name) AS doctor_name,
    s.name AS specialty_name,
    d.specialite_id
FROM doctor d
LEFT JOIN specialties s ON d.specialite_id = s.id
ORDER BY d.last_name, d.first_name;

-- ========================================================================
-- PROCÉDURES STOCKÉES UTILES
-- ========================================================================

DELIMITER //

-- Procédure pour obtenir les consultations d'un médecin pour une date donnée
CREATE PROCEDURE GetDoctorConsultations(IN doctor_id INT, IN consultation_date DATE)
BEGIN
    SELECT 
        c.id,
        c.hour,
        c.reason,
        CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
        p.birth_date
    FROM consultations c
    JOIN patient p ON c.patient_id = p.id
    WHERE c.doctor_id = doctor_id AND c.date = consultation_date
    ORDER BY c.hour;
END //

-- Procédure pour obtenir les consultations d'un patient
CREATE PROCEDURE GetPatientConsultations(IN patient_id INT)
BEGIN
    SELECT 
        c.id,
        c.date,
        c.hour,
        c.reason,
        CONCAT(d.first_name, ' ', d.last_name) AS doctor_name,
        s.name AS specialty_name
    FROM consultations c
    JOIN doctor d ON c.doctor_id = d.id
    LEFT JOIN specialties s ON d.specialite_id = s.id
    WHERE c.patient_id = patient_id
    ORDER BY c.date DESC, c.hour DESC;
END //

DELIMITER ;

-- ========================================================================
-- TRIGGERS POUR AUDIT ET LOGGING
-- ========================================================================

DELIMITER //

-- Trigger pour logger les modifications sur la table consultations
CREATE TRIGGER consultations_audit_update
    AFTER UPDATE ON consultations
    FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, action, record_id, old_values, new_values, timestamp)
    VALUES ('consultations', 'UPDATE', NEW.id, 
            JSON_OBJECT('date', OLD.date, 'hour', OLD.hour, 'reason', OLD.reason),
            JSON_OBJECT('date', NEW.date, 'hour', NEW.hour, 'reason', NEW.reason),
            NOW());
END //

DELIMITER ;

-- ========================================================================
-- TABLE D'AUDIT (optionnelle)
-- ========================================================================

CREATE TABLE IF NOT EXISTS audit_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    action VARCHAR(10) NOT NULL,
    record_id INT,
    old_values JSON,
    new_values JSON,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_table_action (table_name, action),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Table de log pour l''audit des modifications';

-- ========================================================================
-- COMMENTAIRES FINAUX
-- ========================================================================

/*
STRUCTURE DE LA BASE DE DONNÉES :

1. SPECIALTIES : Gère les spécialités médicales
   - Clé primaire : id (auto-increment)
   - Contrainte unique sur le nom de spécialité

2. PATIENT : Stocke les informations des patients
   - Clé primaire : id (auto-increment)
   - Index sur nom, date de naissance et nom complet

3. DOCTOR : Stocke les informations des médecins
   - Clé primaire : id (auto-increment)
   - Clé étrangère vers specialties
   - Index sur nom, spécialité et nom complet

4. CONSULTATIONS : Gère les rendez-vous médicaux
   - Clé primaire : id (auto-increment)
   - Clés étrangères vers patient et doctor
   - Contrainte unique sur médecin/date/heure
   - Index multiples pour optimiser les recherches

RELATIONS :
- Doctor → Specialties (Many-to-One)
- Consultations → Patient (Many-to-One)
- Consultations → Doctor (Many-to-One)

CARACTÉRISTIQUES TECHNIQUES :
- Encodage UTF-8 pour support multilingue
- Timestamps automatiques pour audit
- Index optimisés pour les recherches fréquentes
- Contraintes d'intégrité référentielle
- Procédures stockées pour requêtes complexes
- Vues pour simplifier les accès aux données
*/
