-- Script d'optimisation de la base de données pour améliorer les performances de connexion
-- À exécuter sur la base de données MySQL

-- 1. Créer des index sur les colonnes utilisées dans la requête de login
CREATE INDEX idx_doctor_first_name ON doctor(first_name);
CREATE INDEX idx_doctor_last_name ON doctor(last_name);
CREATE INDEX idx_doctor_password ON doctor(password);

-- 2. Index composite pour la requête de login (plus efficace)
CREATE INDEX idx_doctor_login ON doctor(first_name, password);
CREATE INDEX idx_doctor_login_lastname ON doctor(last_name, password);

-- 3. Optimiser la table doctor
ALTER TABLE doctor ENGINE=InnoDB;

-- 4. Analyser les performances de la requête
EXPLAIN SELECT * FROM doctor WHERE (first_name = 'test' OR last_name = 'test' OR CONCAT(first_name, ' ', last_name) = 'test') AND password = 'test';

-- 5. Vérifier les index créés
SHOW INDEX FROM doctor;
