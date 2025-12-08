# Cheminement du Login - Analyse du Flux

Ce document décrit le cheminement complet d'une requête de connexion depuis l'appui sur le bouton "Connexion" jusqu'à la réponse et l'affichage de l'interface principale.

---

## 📍 CÔTÉ CLIENT

### 1️⃣ **LoginFrame.java** (Interface utilisateur)
**Fichier** : `src/main/java/consultation/client/LoginFrame.java`

- **Ligne 79** : `loginButton.addActionListener(e -> performLogin());`
  - L'utilisateur appuie sur le bouton "Connexion"
  - Déclenchement de l'événement

---

### 2️⃣ **LoginFrame.java → `performLogin()`** 
**Lignes 87-130**

```java
private void performLogin() {
    final String login = loginField.getText().trim();
    final String password = new String(passwordField.getPassword());
    
    // Validation
    if (login.isEmpty() || password.isEmpty()) {
        showError("Veuillez entrer vos identifiants.");
        return;
    }
    
    setBusy(true);
    
    // SwingWorker pour traitement asynchrone
    new SwingWorker<ReponseTraitee, Void>() {
        @Override
        protected ReponseTraitee doInBackground() {
            try {
                networkManager.connect();  // Ligne 104
                return networkManager.sendRequest(new RequeteLogin(login, password));  // Ligne 105
            } catch (Exception ex) {
                error = ex;
                return null;
            }
        }
    }.execute();
}
```

**Actions** :
- **Ligne 88-89** : Récupération du login et password depuis les champs
- **Ligne 91-95** : Validation des entrées
- **Ligne 103-109** : Création d'un `SwingWorker` pour traitement en arrière-plan
- **Ligne 104** : `networkManager.connect()` - connexion TCP au serveur
- **Ligne 105** : `networkManager.sendRequest(new RequeteLogin(login, password))` - envoi de la requête

---

### 3️⃣ **NetworkManager.java → `connect()`**
**Fichier** : `src/main/java/consultation/client/NetworkManager.java`  
**Lignes 16-20**

```java
public void connect() throws IOException {
    this.socket = new Socket(host, port);  // Ligne 17
    this.output = new ObjectOutputStream(socket.getOutputStream());  // Ligne 18
    this.input = new ObjectInputStream(socket.getInputStream());  // Ligne 19
}
```

**Actions** :
- **Ligne 17** : `new Socket(host, port)` - Établissement de la connexion TCP au serveur (127.0.0.1:5000)
- **Ligne 18** : Création du flux de sortie pour sérialiser les objets vers le serveur
- **Ligne 19** : Création du flux d'entrée pour désérialiser les objets reçus du serveur

---

### 4️⃣ **NetworkManager.java → `sendRequest()`**
**Lignes 22-29**

```java
public synchronized ReponseTraitee sendRequest(Requete req) throws IOException, ClassNotFoundException {
    output.writeObject(req);  // Ligne 23
    output.flush();  // Ligne 24
    Object resp = input.readObject();  // Ligne 25 - BLOQUANT
    if (resp instanceof ReponseTraitee) {
        return (ReponseTraitee) resp;
    }
    throw new IOException("Réponse inconnue reçue du serveur");
}
```

**Actions** :
- **Ligne 23** : `output.writeObject(req)` - Sérialisation et envoi de l'objet `RequeteLogin` au serveur
- **Ligne 24** : `output.flush()` - Force l'envoi des données
- **Ligne 25** : `input.readObject()` - **ATTENTE BLOQUANTE** de la réponse du serveur

---

## 🌐 TRANSMISSION RÉSEAU

```
[CLIENT] ──────► Sérialisation de RequeteLogin(login, password)
           │
           │     TCP/IP Socket (127.0.0.1:5000)
           │
           └────► [SERVEUR]
```

---

## 📍 CÔTÉ SERVEUR

### 5️⃣ **ConsultationServer.java** (Serveur principal)
**Fichier** : `src/main/java/consultation/server/ConsultationServer.java`  
**Lignes 27-34**

```java
while (running) {
    try {
        Socket client = ss.accept();  // Ligne 30 - BLOQUANT
        connectionQueue.addConnection(client);  // Ligne 31
    } catch (IOException e) {
        if (running) {
            System.err.println("Erreur lors de l'acceptation d'une connexion: " + e.getMessage());
        }
    }
}
```

**Actions** :
- **Ligne 30** : `ss.accept()` - Accepte la connexion entrante du client (appel bloquant)
- **Ligne 31** : `connectionQueue.addConnection(client)` - Ajoute le socket à la file d'attente

---

### 6️⃣ **ConnectionQueue.java → `addConnection()`**
**Fichier** : `src/main/java/consultation/server/ConnectionQueue.java`  
**Lignes 7-10**

```java
public synchronized void addConnection(Socket socket) {
    queue.addLast(socket);  // Ligne 8
    notify();  // Ligne 9
}
```

**Actions** :
- **Ligne 8** : Ajoute le socket dans une `LinkedList<Socket>` (file FIFO)
- **Ligne 9** : `notify()` - Réveille un thread worker en attente dans `getConnection()`

---

### 7️⃣ **ConnectionWorker.java** (Thread worker du pool)
**Fichier** : `src/main/java/consultation/server/ConnectionWorker.java`  
**Lignes 20-34**

```java
@Override
public void run() {
    while (running) {
        Socket clientSocket = null;
        try {
            clientSocket = queue.getConnection();  // Ligne 24 - BLOQUANT
            handleClient(clientSocket);  // Ligne 25
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
}
```

**Actions** :
- **Ligne 24** : `queue.getConnection()` - Récupère un socket depuis la file (appel bloquant via `wait()`)
- **Ligne 25** : `handleClient(clientSocket)` - Traite les requêtes du client

---

### 8️⃣ **ConnectionWorker.java → `handleClient()`**
**Lignes 38-63**

```java
private void handleClient(Socket socket) {
    try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());  // Ligne 39
         ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {  // Ligne 40
        
        while (running) {
            try {
                Object obj = ois.readObject();  // Ligne 43 - REÇOIT l'objet
                
                if (!(obj instanceof Requete)) {
                    System.err.println("Objet reçu inconnu: " + obj);
                    break;
                }
                
                Requete req = (Requete) obj;  // Ligne 51
                ReponseTraitee resp = protocol.traiter(req);  // Ligne 52
                
                oos.writeObject(resp);  // Ligne 53 - ENVOIE la réponse
                oos.flush();  // Ligne 54
                
                if (req.isLogout()) {
                    break;
                }
            } catch (EOFException e) {
                break;
            }
        }
    }
}
```

**Actions** :
- **Ligne 39-40** : Création des flux ObjectOutputStream et ObjectInputStream
- **Ligne 43** : `ois.readObject()` - **RÉCEPTION** et désérialisation de l'objet `RequeteLogin`
- **Ligne 51** : Cast vers `Requete req`
- **Ligne 52** : `protocol.traiter(req)` - Appel du protocole pour traiter la requête
- **Ligne 53** : `oos.writeObject(resp)` - **ENVOI** de la réponse sérialisée au client
- **Ligne 54** : `oos.flush()` - Force l'envoi

---

### 9️⃣ **CAPProtocol.java → `traiter()`**
**Fichier** : `src/main/java/consultation/server/protocol/CAPProtocol.java`  
**Lignes 9-15**

```java
public ReponseTraitee traiter(Requete requete) {
    try {
        return requete.traite(daoFactory);  // Ligne 11
    } catch (Exception e) {
        return new ReponseTraitee(false, e.getMessage(), null);
    }
}
```

**Actions** :
- **Ligne 11** : Délègue le traitement à la méthode `traite()` de la requête
- Gestion des exceptions et création d'une réponse d'erreur si nécessaire

---

### 🔟 **RequeteLogin.java → `traite()`**
**Fichier** : `src/main/java/consultation/server/protocol/RequeteLogin.java`  
**Lignes 14-22**

```java
@Override
public ReponseTraitee traite(DAOFactory daoFactory) throws Exception {
    DoctorDAO doctorDAO = daoFactory.getDoctorDAO();  // Ligne 15
    Doctor doctor = doctorDAO.login(login, password);  // Ligne 16
    
    if (doctor != null) {  // Ligne 17
        return new ReponseTraitee(true, "Connexion réussie", doctor);  // Ligne 18
    }
    return new ReponseTraitee(false, "Identifiants invalides", null);  // Ligne 20
}
```

**Actions** :
- **Ligne 15** : Récupération du DAO pour les médecins
- **Ligne 16** : Appel de la méthode `login()` du DAO
- **Ligne 17-20** : Création de la réponse selon le résultat

---

### 1️⃣1️⃣ **DoctorDAO.java → `login()`**
**Fichier** : `src/main/java/hepl/fead/model/dao/DoctorDAO.java`  
**Lignes 136-164**

```java
public Doctor login(String login, String password) {
    if (login == null || login.isEmpty() || password == null) {
        return null;
    }

    try {
        String query = "SELECT * FROM doctor WHERE " +
                      "(first_name = ? OR last_name = ? OR CONCAT(first_name, ' ', last_name) = ?) " +
                      "AND password = ?";  // Lignes 141-143
        
        PreparedStatement ps = ConnectBD.getConnection().prepareStatement(query);  // Ligne 144
        ps.setString(1, login);  // Ligne 145
        ps.setString(2, login);
        ps.setString(3, login);
        ps.setString(4, password);

        ResultSet rs = ps.executeQuery();  // Ligne 148 - EXÉCUTION SQL
        
        if (rs.next()) {  // Ligne 149
            Doctor doctor = new Doctor();
            doctor.setId(rs.getInt("id"));
            doctor.setFirst_name(rs.getString("first_name"));
            doctor.setLast_name(rs.getString("last_name"));
            doctor.setSpecialite_id(rs.getInt("specialite_id"));
            doctor.setPassword(rs.getString("password"));
            rs.close();
            ps.close();
            return doctor;  // Ligne 157
        }

        rs.close();
        ps.close();
    } catch (SQLException e) {
        Logger.getLogger(DoctorDAO.class.getName()).warning(e.getMessage());
    }

    return null;  // Ligne 163 - Échec de connexion
}
```

**Actions** :
- **Ligne 141-143** : Préparation de la requête SQL avec recherche flexible (prénom, nom, ou nom complet)
- **Ligne 144-147** : Préparation du PreparedStatement avec les paramètres
- **Ligne 148** : `ps.executeQuery()` - **EXÉCUTION** de la requête SQL dans la base de données
- **Ligne 149-157** : Si un résultat est trouvé, création et retour de l'objet `Doctor`
- **Ligne 163** : Retourne `null` si aucun médecin ne correspond

---

## 🗄️ INTERACTION BASE DE DONNÉES

```sql
SELECT * FROM doctor 
WHERE (first_name = ? OR last_name = ? OR CONCAT(first_name, ' ', last_name) = ?) 
  AND password = ?
```

**Paramètres** :
- `?1, ?2, ?3` : login (testé sur prénom, nom, ou nom complet)
- `?4` : password

**Résultat** :
- **Si trouvé** : Objet `Doctor` avec `id`, `first_name`, `last_name`, `specialite_id`, `password`
- **Si non trouvé** : `null`

---

## 🔙 REMONTÉE DE LA RÉPONSE

### 1️⃣2️⃣ **RequeteLogin → ReponseTraitee**

```java
// Si doctor != null
return new ReponseTraitee(true, "Connexion réussie", doctor);

// Sinon
return new ReponseTraitee(false, "Identifiants invalides", null);
```

---

### 1️⃣3️⃣ **ConnectionWorker → Client**

```java
oos.writeObject(resp);  // Sérialisation de ReponseTraitee
oos.flush();            // Envoi au client
```

---

## 🌐 TRANSMISSION RÉSEAU (RETOUR)

```
[SERVEUR] ──────► Sérialisation de ReponseTraitee(success, message, doctor)
             │
             │     TCP/IP Socket
             │
             └────► [CLIENT]
```

---

## 📍 RETOUR CÔTÉ CLIENT

### 1️⃣4️⃣ **NetworkManager.java** (Réception)
**Lignes 25-28**

```java
Object resp = input.readObject();  // Ligne 25 - RÉCEPTION
if (resp instanceof ReponseTraitee) {
    return (ReponseTraitee) resp;  // Ligne 27
}
```

**Actions** :
- **Ligne 25** : `input.readObject()` - Déblocage et désérialisation de la `ReponseTraitee`
- **Ligne 27** : Retour de la réponse au SwingWorker

---

### 1️⃣5️⃣ **LoginFrame.java → SwingWorker `done()`**
**Lignes 113-127**

```java
@Override
protected void done() {
    setBusy(false);  // Ligne 114
    
    if (error != null) {  // Ligne 115
        showError("Erreur de connexion au serveur : " + error.getMessage());
        return;
    }
    
    try {
        ReponseTraitee resp = get();  // Ligne 120 - Récupération du résultat
        
        if (resp != null && resp.isSuccess()) {  // Ligne 121
            Doctor doctor = (Doctor) resp.getData();  // Ligne 122
            dispose();  // Ligne 123 - Fermeture de LoginFrame
            MainFrame mainFrame = new MainFrame(networkManager, doctor);  // Ligne 124
            mainFrame.setVisible(true);  // Ligne 125
        } else {
            showError(resp != null ? resp.getMessage() : "Réponse invalide du serveur.");
        }
    } catch (Exception ex) {
        showError("Erreur inattendue : " + ex.getMessage());
    }
}
```

**Actions** :
- **Ligne 114** : Réactivation de l'interface (curseur normal, boutons activés)
- **Ligne 120** : `get()` - Récupération du résultat du SwingWorker
- **Ligne 121-125** : Si succès :
  - **Ligne 122** : Extraction de l'objet `Doctor` depuis la réponse
  - **Ligne 123** : `dispose()` - Fermeture de la fenêtre de login
  - **Ligne 124** : Création de la fenêtre principale `MainFrame` avec les données du médecin
  - **Ligne 125** : Affichage de la fenêtre principale
- **Sinon** : Affichage du message d'erreur

---

## 🔄 SCHÉMA RÉCAPITULATIF DU FLUX

```
┌─────────────────────────────────────────────────────────────────┐
│                        CÔTÉ CLIENT                              │
└─────────────────────────────────────────────────────────────────┘

LoginFrame.performLogin()
    │
    ├─► NetworkManager.connect()
    │       └─► new Socket("127.0.0.1", 5000)  [Connexion TCP]
    │
    └─► NetworkManager.sendRequest(RequeteLogin)
            └─► output.writeObject(RequeteLogin)  [Sérialisation]
                    │
                    │
                    ▼
        ╔═══════════════════════════════════════╗
        ║       RÉSEAU TCP/IP (Socket)          ║
        ╚═══════════════════════════════════════╝
                    │
                    │
                    ▼

┌─────────────────────────────────────────────────────────────────┐
│                        CÔTÉ SERVEUR                             │
└─────────────────────────────────────────────────────────────────┘

ConsultationServer.accept()
    │
    └─► connectionQueue.addConnection(socket)
            │
            └─► notify()  [Réveille un worker]
                    │
                    ▼
ConnectionWorker.getConnection()  [Worker du pool de threads]
    │
    └─► handleClient(socket)
            │
            ├─► ois.readObject()  [Désérialisation de RequeteLogin]
            │
            └─► protocol.traiter(requete)
                    │
                    └─► requete.traite(daoFactory)
                            │
                            └─► RequeteLogin.traite()
                                    │
                                    └─► doctorDAO.login(login, password)
                                            │
                                            │
                                            ▼
                                    ╔═══════════════════════════╗
                                    ║   BASE DE DONNÉES MySQL   ║
                                    ║                           ║
                                    ║   SELECT * FROM doctor    ║
                                    ║   WHERE ...               ║
                                    ╚═══════════════════════════╝
                                            │
                                            ├─► Résultat trouvé : Doctor
                                            └─► Non trouvé : null
                                            │
                                            ▼
                                    new ReponseTraitee(success, message, doctor)
                                            │
                                            │
            ┌───────────────────────────────┘
            │
            └─► oos.writeObject(ReponseTraitee)  [Sérialisation]
                    │
                    │
                    ▼
        ╔═══════════════════════════════════════╗
        ║       RÉSEAU TCP/IP (Socket)          ║
        ╚═══════════════════════════════════════╝
                    │
                    │
                    ▼

┌─────────────────────────────────────────────────────────────────┐
│                    RETOUR CÔTÉ CLIENT                           │
└─────────────────────────────────────────────────────────────────┘

NetworkManager.sendRequest()
    │
    └─► input.readObject()  [Désérialisation de ReponseTraitee]
            │
            └─► return ReponseTraitee
                    │
                    ▼
SwingWorker.done()
    │
    ├─► resp.isSuccess() == true ?
    │       │
    │       ├─► YES: Doctor doctor = resp.getData()
    │       │       │
    │       │       └─► new MainFrame(networkManager, doctor)
    │       │               └─► mainFrame.setVisible(true)
    │       │                       │
    │       │                       └─► [Interface principale affichée]
    │       │
    │       └─► NO: showError(resp.getMessage())
    │
    └─► [Fin du traitement]
```

---

## ⏱️ POINTS BLOQUANTS (SYNCHRONISATION)

| Étape | Méthode | Type de Blocage |
|-------|---------|-----------------|
| Client | `Socket.connect()` | Bloquant jusqu'à connexion établie |
| Client | `ObjectInputStream.readObject()` | Bloquant jusqu'à réception de la réponse |
| Serveur | `ServerSocket.accept()` | Bloquant jusqu'à nouvelle connexion |
| Serveur | `ConnectionQueue.getConnection()` | Bloquant (wait) jusqu'à socket disponible |
| Serveur | `ObjectInputStream.readObject()` | Bloquant jusqu'à réception de la requête |
| Serveur | `PreparedStatement.executeQuery()` | Bloquant jusqu'à réponse de la BD |

---

## 🧵 GESTION DES THREADS

### Côté Client
- **Thread principal (EDT)** : Gère l'interface graphique
- **SwingWorker thread** : Exécute `doInBackground()` pour les opérations réseau
- **EDT** : Exécute `done()` pour mettre à jour l'interface

### Côté Serveur
- **Thread principal** : Boucle `accept()` pour accepter les connexions
- **Pool de N threads workers** : Chaque worker traite les requêtes de manière indépendante
- Chaque worker a sa propre instance de `CAPProtocol` avec un `DAOFactory`

---

## 📊 OBJETS ÉCHANGÉS

### RequeteLogin (Client → Serveur)
```java
class RequeteLogin implements Requete {
    private String login;
    private String password;
}
```

### ReponseTraitee (Serveur → Client)
```java
class ReponseTraitee {
    private boolean success;
    private String message;
    private Object data;  // Doctor si succès, null sinon
}
```

### Doctor (Contenu de la réponse)
```java
class Doctor {
    private Integer id;
    private String first_name;
    private String last_name;
    private Integer specialite_id;
    private String password;
}
```

---

## 🔐 SÉCURITÉ

**⚠️ Points à améliorer** :
1. **Mot de passe en clair** : Le password est transmis et stocké sans hachage
2. **Pas de chiffrement** : Communication en clair sur le réseau
3. **Pas de protection contre les attaques par force brute**
4. **Injection SQL** : Bien que des PreparedStatement soient utilisés (protection partielle)

---

## 📝 RÉSUMÉ CHRONOLOGIQUE

1. **Utilisateur** appuie sur "Connexion"
2. **LoginFrame** lance un SwingWorker
3. **NetworkManager** établit une connexion TCP
4. **NetworkManager** envoie `RequeteLogin` sérialisée
5. **ConsultationServer** accepte la connexion
6. **ConnectionQueue** met le socket en file d'attente
7. **ConnectionWorker** récupère et traite le socket
8. **CAPProtocol** délègue à `RequeteLogin.traite()`
9. **DoctorDAO** exécute la requête SQL
10. **Base de données** retourne le résultat
11. **ReponseTraitee** est créée avec le résultat
12. **ConnectionWorker** envoie la réponse sérialisée
13. **NetworkManager** reçoit et désérialise la réponse
14. **SwingWorker** traite la réponse dans `done()`
15. **MainFrame** s'affiche si succès, sinon message d'erreur

---

## 📁 FICHIERS IMPLIQUÉS

### Client
- `consultation/client/LoginFrame.java`
- `consultation/client/NetworkManager.java`
- `consultation/client/MainFrame.java`

### Serveur
- `consultation/server/ConsultationServer.java`
- `consultation/server/ConnectionQueue.java`
- `consultation/server/ClientThreadPool.java`
- `consultation/server/ConnectionWorker.java`

### Protocole
- `consultation/server/protocol/CAPProtocol.java`
- `consultation/server/protocol/Requete.java`
- `consultation/server/protocol/RequeteLogin.java`
- `consultation/server/protocol/ReponseTraitee.java`

### DAO & Modèle
- `hepl/fead/model/dao/DAOFactory.java`
- `hepl/fead/model/dao/DoctorDAO.java`
- `hepl/fead/model/entity/Doctor.java`
- `hepl/fead/model/bd/ConnectBD.java`

---

**Auteur** : Analyse du projet eval_2  
**Date** : 18 novembre 2025
