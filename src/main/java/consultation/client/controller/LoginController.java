package consultation.client.controller;

import consultation.client.model.UserModel;
import consultation.client.NetworkManager;
import consultation.server.protocol.RequeteLogin;
import consultation.server.protocol.ReponseTraitee;
import hepl.fead.model.entity.Doctor;

import javax.swing.SwingUtilities;
import java.util.Observer;

/**
 * Controller pour gérer l'authentification
 * Sépare la logique métier de la vue
 */
public class LoginController {
    private final UserModel userModel;
    private final NetworkManager networkManager;

    public LoginController(UserModel userModel, NetworkManager networkManager) {
        this.userModel = userModel;
        this.networkManager = networkManager;
    }

    /**
     * Tente de connecter l'utilisateur
     */
    public void login(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            userModel.setLoginError("Veuillez entrer vos identifiants");
            return;
        }

        userModel.clearLoginError();
        
        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                
                // Établir la connexion avec timeout
                networkManager.connect();
                
                long connectionTime = System.currentTimeMillis() - startTime;
                System.out.println("Temps de connexion réseau: " + connectionTime + "ms");
                
                // Envoyer la requête de login
                long loginStart = System.currentTimeMillis();
                ReponseTraitee response = networkManager.sendRequest(new RequeteLogin(username, password));
                long loginTime = System.currentTimeMillis() - loginStart;
                System.out.println("Temps de traitement serveur: " + loginTime + "ms");
                
                long totalTime = System.currentTimeMillis() - startTime;
                System.out.println("Temps total: " + totalTime + "ms");
                
                // Analyser les performances
                if (connectionTime > 100) {
                    System.out.println("⚠️  Connexion réseau lente (" + connectionTime + "ms)");
                }
                if (loginTime > 500) {
                    System.out.println("⚠️  Serveur/base de données lente (" + loginTime + "ms)");
                }
                if (totalTime > 1000) {
                    System.out.println("⚠️  Connexion très lente (" + totalTime + "ms)");
                }
                
                // Forcer la mise à jour immédiate de l'interface
                SwingUtilities.invokeLater(() -> {
                    if (response.isSuccess()) {
                        Doctor doctor = (Doctor) response.getData();
                        userModel.setCurrentDoctor(doctor);
                    } else {
                        userModel.setLoginError(response.getMessage());
                    }
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    userModel.setLoginError("Erreur lors de la connexion au serveur: " + e.getMessage());
                });
            }
        }).start();
    }

    /**
     * Déconnecte l'utilisateur
     */
    public void logout() {
        try {
            networkManager.close();
        } catch (Exception e) {
            // Ignore les erreurs de fermeture
        }
        userModel.logout();
    }

    /**
     * Ajoute un observateur au modèle utilisateur
     */
    public void addUserObserver(Observer observer) {
        userModel.addObserver(observer);
    }

    /**
     * Retire un observateur du modèle utilisateur
     */
    public void removeUserObserver(Observer observer) {
        userModel.deleteObserver(observer);
    }

    /**
     * Retourne le NetworkManager pour les autres contrôleurs
     */
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    /**
     * Retourne le docteur actuellement connecté
     */
    public Doctor getCurrentDoctor() {
        return userModel.getCurrentDoctor();
    }
}