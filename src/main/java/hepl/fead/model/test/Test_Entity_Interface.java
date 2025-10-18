package hepl.fead.model.test;

import hepl.fead.model.entity.Entity;
import hepl.fead.model.entity.Patient;
import hepl.fead.model.entity.Doctor;
import hepl.fead.model.entity.Speciality;
import hepl.fead.model.entity.Consultation;

/**
 * Démonstration de l'utilisation de l'interface Entity
 */
public class Test_Entity_Interface {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║     TEST DE L'INTERFACE ENTITY                         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Créer un patient sans ID (nouveau)
        Patient patient = new Patient();
        patient.setFirst_name("Jean");
        patient.setLast_name("Dupont");
        
        System.out.println("📋 TEST 1: Vérifier si une entité est nouvelle");
        System.out.println("------------------------------------------");
        System.out.println("Patient créé: " + patient.getFirst_name() + " " + patient.getLast_name());
        System.out.println("ID: " + patient.getId());
        System.out.println("Est nouveau (pas encore en base) ? " + patient.isNew());
        System.out.println("Est persisté (en base) ? " + patient.isPersisted());

        // Simuler l'insertion en base
        patient.setId(42);
        
        System.out.println("\n📋 TEST 2: Après insertion en base");
        System.out.println("------------------------------------------");
        System.out.println("ID assigné: " + patient.getId());
        System.out.println("Est nouveau ? " + patient.isNew());
        System.out.println("Est persisté ? " + patient.isPersisted());

        // Méthode générique qui accepte n'importe quelle entité
        System.out.println("\n📋 TEST 3: Méthode générique avec Entity");
        System.out.println("------------------------------------------");
        
        Doctor doctor = new Doctor(10, 1, "Martin", "Dr. Pierre");
        Speciality speciality = new Speciality(5, "Cardiologie");
        Consultation consultation = new Consultation();
        consultation.setId(99);
        
        afficherInfoEntity(patient);
        afficherInfoEntity(doctor);
        afficherInfoEntity(speciality);
        afficherInfoEntity(consultation);

        // Travailler avec une liste d'entités hétérogènes
        System.out.println("\n📋 TEST 4: Liste polymorphique d'entités");
        System.out.println("------------------------------------------");
        
        Entity[] entities = {patient, doctor, speciality, consultation};
        int nouvelles = 0;
        int persistees = 0;
        
        for (Entity entity : entities) {
            if (entity.isNew()) {
                nouvelles++;
            } else {
                persistees++;
            }
        }
        
        System.out.println("Total d'entités: " + entities.length);
        System.out.println("Nouvelles (pas en base): " + nouvelles);
        System.out.println("Persistées (en base): " + persistees);

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║     AVANTAGES DE L'INTERFACE ENTITY                    ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("\n✅ Avantages:");
        System.out.println("   1. Méthodes communes pour toutes les entités");
        System.out.println("   2. Polymorphisme - traiter toutes les entités de la même façon");
        System.out.println("   3. Vérification facile si nouvelle ou persistée");
        System.out.println("   4. Facilite la création de DAO générique");
        System.out.println("   5. Code plus maintenable et extensible");
        System.out.println("\n💡 Utilisation future possible:");
        System.out.println("   - Créer un GenericDAO<T extends Entity>");
        System.out.println("   - Validation automatique basée sur isPersisted()");
        System.out.println("   - Audit trail (qui a créé/modifié quoi)");
        System.out.println("   - Cache d'entités basé sur l'ID");
    }

    /**
     * Méthode générique qui fonctionne avec n'importe quelle entité
     */
    private static void afficherInfoEntity(Entity entity) {
        String type = entity.getClass().getSimpleName();
        String statut = entity.isPersisted() ? "✅ Persisté" : "🆕 Nouveau";
        System.out.println("  " + type + " [ID: " + entity.getId() + "] - " + statut);
    }
}

