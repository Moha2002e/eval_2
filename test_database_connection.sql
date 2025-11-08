-- Script de test de connexion à la base de données
-- À exécuter pour vérifier que la base de données est accessible

-- 1. Vérifier que la base de données existe
SHOW DATABASES LIKE 'consultation_db';

-- 2. Utiliser la base de données
USE consultation_db;

-- 3. Vérifier que la table doctor existe
SHOW TABLES LIKE 'doctor';

-- 4. Vérifier la structure de la table doctor
DESCRIBE doctor;

-- 5. Vérifier les données existantes
SELECT COUNT(*) as nombre_docteurs FROM doctor;

-- 6. Tester une requête de login (remplacer par des vraies données)
-- SELECT * FROM doctor WHERE (first_name = 'test' OR last_name = 'test' OR CONCAT(first_name, ' ', last_name) = 'test') AND password = 'test';

-- 7. Vérifier les index existants
SHOW INDEX FROM doctor;
