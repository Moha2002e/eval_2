# 🚀 Guide de développement du Serveur Réservation en C

## 📋 Vue d'ensemble

Le **Serveur Réservation** est le composant manquant critique du projet. Il doit :
- Être écrit en **C avec threads POSIX**
- Écouter sur **2 ports simultanément**
- Implémenter **2 protocoles** : CBP et ACBP
- Utiliser un modèle **multi-threads "à la demande"**

---

## 🏗️ Architecture du serveur

```
┌─────────────────────────────────────────────────────┐
│         PROCESSUS : Serveur Réservation             │
├─────────────────────────────────────────────────────┤
│                                                      │
│  ┌──────────────────┐      ┌───────────────────┐  │
│  │  Socket PORT_CBP │      │ Socket PORT_ADMIN │  │
│  │   (ex: 8080)     │      │   (ex: 8081)      │  │
│  └────────┬─────────┘      └────────┬──────────┘  │
│           │                          │              │
│           │ accept()                 │ accept()     │
│           ▼                          ▼              │
│  ┌────────────────┐        ┌────────────────────┐ │
│  │ Thread CBP     │        │ Thread ACBP        │ │
│  │ (Patients)     │        │ (Admin)            │ │
│  └────────────────┘        └────────────────────┘ │
│           │                          │              │
│           └──────────┬───────────────┘              │
│                      ▼                               │
│         ┌────────────────────────┐                  │
│         │ Liste clients          │                  │
│         │ (thread-safe)          │                  │
│         │ - Mutex protection     │                  │
│         └────────────────────────┘                  │
└─────────────────────────────────────────────────────┘
```

---

## 📂 Structure des fichiers

### Arborescence recommandée

```
serveur_reservation/
├── src/
│   ├── main.c                    # Point d'entrée
│   ├── server_config.c/h         # Configuration (ports, threads)
│   ├── thread_manager.c/h        # Gestion threads POSIX
│   ├── client_manager.c/h        # Liste des clients connectés
│   ├── protocole_cbp.c/h         # Protocole réservation
│   ├── protocole_acbp.c/h        # Protocole admin
│   ├── network_utils.c/h         # send_line(), recv_line()
│   └── logger.c/h                # Logs formatés
├── include/
│   └── types.h                   # Structures communes
├── Makefile                      # Compilation
├── config.conf                   # Fichier de configuration
└── README.md                     # Instructions

```

---

## 📝 Structures de données clés

### 1. Structure Client CBP

```c
// types.h
typedef struct {
    int socket_fd;              // Descripteur socket
    char ip_address[16];        // Adresse IP (ex: "192.168.0.100")
    char nom[100];              // Nom du patient
    char prenom[100];           // Prénom du patient
    int patient_id;             // ID patient en BD
    time_t connected_at;        // Timestamp connexion
    pthread_t thread_id;        // ID du thread
    int is_active;              // 1 = connecté, 0 = déconnecté
} Client;
```

### 2. Liste thread-safe des clients

```c
// client_manager.h
typedef struct {
    Client clients[MAX_CLIENTS];  // Tableau de clients
    int count;                     // Nombre de clients actifs
    pthread_mutex_t mutex;         // Mutex pour accès concurrent
} ClientList;

// Fonctions
void client_list_init(ClientList *list);
int client_list_add(ClientList *list, Client *client);
void client_list_remove(ClientList *list, int socket_fd);
char* client_list_to_string(ClientList *list); // Pour LIST_CLIENTS
void client_list_destroy(ClientList *list);
```

---

## 🔌 Protocole ACBP (Admin Consultation Booking Protocol)

### Spécifications

**Port** : `PORT_ADMIN` (ex: 8081)  
**Type** : Serveur de requêtes (connexion courte)  
**Format** : Texte (délimité par `#`)

### Commande : LIST_CLIENTS

**Requête client** :
```
LIST_CLIENTS#
```

**Réponse serveur** (succès) :
```
LIST_CLIENTS#ok#192.168.0.100;Dupont;Jean;1234
192.168.0.101;Martin;Sophie;5678
192.168.0.102;Bernard;Luc;9012

```

**Réponse serveur** (erreur) :
```
LIST_CLIENTS#error#Message d'erreur
```

### Implémentation

```c
// protocole_acbp.c

#include "protocole_acbp.h"
#include "client_manager.h"
#include "network_utils.h"
#include <string.h>
#include <stdio.h>

extern ClientList global_clients; // Liste globale

void* handle_acbp_client(void* arg) {
    int client_socket = *(int*)arg;
    free(arg);
    
    char buffer[1024];
    
    // Recevoir la commande
    if (recv_line(client_socket, buffer, sizeof(buffer)) <= 0) {
        close(client_socket);
        return NULL;
    }
    
    // Parser la commande
    if (strncmp(buffer, "LIST_CLIENTS#", 13) == 0) {
        // Obtenir la liste (thread-safe)
        pthread_mutex_lock(&global_clients.mutex);
        
        char response[4096] = "LIST_CLIENTS#ok#";
        
        for (int i = 0; i < global_clients.count; i++) {
            if (global_clients.clients[i].is_active) {
                char line[256];
                snprintf(line, sizeof(line), "%s;%s;%s;%d\n",
                    global_clients.clients[i].ip_address,
                    global_clients.clients[i].nom,
                    global_clients.clients[i].prenom,
                    global_clients.clients[i].patient_id
                );
                strcat(response, line);
            }
        }
        
        pthread_mutex_unlock(&global_clients.mutex);
        
        // Envoyer la réponse
        send_line(client_socket, response);
        
    } else {
        send_line(client_socket, "LIST_CLIENTS#error#Commande inconnue\n");
    }
    
    close(client_socket);
    return NULL;
}
```

---

## 🔌 Protocole CBP (Consultation Booking Protocol)

### Spécifications

**Port** : `PORT_CBP` (ex: 8080)  
**Type** : Serveur de connexions (connexion persistante)  
**Format** : Texte (délimité par `#`)

### Commandes proposées

#### 1. LOGIN_PATIENT
```
Requête  : LOGIN_PATIENT#nom#prenom
Réponse  : LOGIN_PATIENT#ok#patient_id
           LOGIN_PATIENT#error#Message erreur
```

#### 2. LIST_CONSULTATIONS
```
Requête  : LIST_CONSULTATIONS#date_debut#date_fin
Réponse  : LIST_CONSULTATIONS#ok#id;doctor;date;heure;disponible
           id;doctor;date;heure;disponible
           ...
```

#### 3. BOOK_CONSULTATION
```
Requête  : BOOK_CONSULTATION#consultation_id#raison
Réponse  : BOOK_CONSULTATION#ok#Réservation confirmée
           BOOK_CONSULTATION#error#Consultation déjà réservée
```

#### 4. CANCEL_BOOKING
```
Requête  : CANCEL_BOOKING#consultation_id
Réponse  : CANCEL_BOOKING#ok#Annulation confirmée
           CANCEL_BOOKING#error#Message erreur
```

#### 5. MY_BOOKINGS
```
Requête  : MY_BOOKINGS
Réponse  : MY_BOOKINGS#ok#id;doctor;date;heure;raison
           ...
```

#### 6. LOGOUT
```
Requête  : LOGOUT
Réponse  : LOGOUT#ok#À bientôt
```

### Implémentation

```c
// protocole_cbp.c

#include "protocole_cbp.h"
#include "client_manager.h"
#include <mysql/mysql.h>

extern ClientList global_clients;
extern MYSQL* db_connection; // Connexion MySQL globale

void* handle_cbp_client(void* arg) {
    int client_socket = *(int*)arg;
    free(arg);
    
    char buffer[1024];
    Client current_client = {0};
    current_client.socket_fd = client_socket;
    
    // Obtenir l'IP du client
    struct sockaddr_in addr;
    socklen_t addr_len = sizeof(addr);
    getpeername(client_socket, (struct sockaddr*)&addr, &addr_len);
    inet_ntop(AF_INET, &addr.sin_addr, current_client.ip_address, sizeof(current_client.ip_address));
    
    int authenticated = 0;
    
    while (1) {
        // Recevoir commande
        if (recv_line(client_socket, buffer, sizeof(buffer)) <= 0) {
            break;
        }
        
        // Parser commande
        char* cmd = strtok(buffer, "#");
        
        if (strcmp(cmd, "LOGIN_PATIENT") == 0) {
            char* nom = strtok(NULL, "#");
            char* prenom = strtok(NULL, "#");
            
            // Rechercher/créer patient en BD
            int patient_id = find_or_create_patient(db_connection, nom, prenom);
            
            if (patient_id > 0) {
                strcpy(current_client.nom, nom);
                strcpy(current_client.prenom, prenom);
                current_client.patient_id = patient_id;
                current_client.is_active = 1;
                current_client.thread_id = pthread_self();
                
                // Ajouter à la liste globale
                client_list_add(&global_clients, &current_client);
                
                authenticated = 1;
                
                char response[256];
                snprintf(response, sizeof(response), "LOGIN_PATIENT#ok#%d\n", patient_id);
                send_line(client_socket, response);
            } else {
                send_line(client_socket, "LOGIN_PATIENT#error#Patient introuvable\n");
            }
            
        } else if (strcmp(cmd, "LIST_CONSULTATIONS") == 0) {
            if (!authenticated) {
                send_line(client_socket, "LIST_CONSULTATIONS#error#Non authentifié\n");
                continue;
            }
            
            // Requête MySQL pour consultations libres
            char query[512];
            snprintf(query, sizeof(query),
                "SELECT c.id, CONCAT(d.first_name, ' ', d.last_name) AS doctor, "
                "c.date, c.hour FROM consultations c "
                "JOIN doctor d ON c.doctor_id = d.id "
                "WHERE c.patient_id IS NULL ORDER BY c.date, c.hour"
            );
            
            if (mysql_query(db_connection, query) == 0) {
                MYSQL_RES* result = mysql_store_result(db_connection);
                MYSQL_ROW row;
                
                send_line(client_socket, "LIST_CONSULTATIONS#ok#");
                
                while ((row = mysql_fetch_row(result)) != NULL) {
                    char line[256];
                    snprintf(line, sizeof(line), "%s;%s;%s;%s;1\n",
                        row[0], row[1], row[2], row[3]
                    );
                    send_line(client_socket, line);
                }
                
                mysql_free_result(result);
            }
            
        } else if (strcmp(cmd, "BOOK_CONSULTATION") == 0) {
            if (!authenticated) {
                send_line(client_socket, "BOOK_CONSULTATION#error#Non authentifié\n");
                continue;
            }
            
            int consultation_id = atoi(strtok(NULL, "#"));
            char* raison = strtok(NULL, "#");
            
            // UPDATE consultations SET patient_id = ?, reason = ? WHERE id = ? AND patient_id IS NULL
            char query[512];
            snprintf(query, sizeof(query),
                "UPDATE consultations SET patient_id = %d, reason = '%s' "
                "WHERE id = %d AND patient_id IS NULL",
                current_client.patient_id, raison, consultation_id
            );
            
            if (mysql_query(db_connection, query) == 0) {
                if (mysql_affected_rows(db_connection) > 0) {
                    send_line(client_socket, "BOOK_CONSULTATION#ok#Réservation confirmée\n");
                } else {
                    send_line(client_socket, "BOOK_CONSULTATION#error#Consultation déjà réservée\n");
                }
            } else {
                send_line(client_socket, "BOOK_CONSULTATION#error#Erreur BD\n");
            }
            
        } else if (strcmp(cmd, "LOGOUT") == 0) {
            client_list_remove(&global_clients, client_socket);
            send_line(client_socket, "LOGOUT#ok#À bientôt\n");
            break;
            
        } else {
            send_line(client_socket, "ERROR#Commande inconnue\n");
        }
    }
    
    // Nettoyer
    client_list_remove(&global_clients, client_socket);
    close(client_socket);
    return NULL;
}

// Fonction utilitaire
int find_or_create_patient(MYSQL* db, char* nom, char* prenom) {
    // Rechercher patient existant
    char query[256];
    snprintf(query, sizeof(query),
        "SELECT id FROM patient WHERE last_name = '%s' AND first_name = '%s'",
        nom, prenom
    );
    
    if (mysql_query(db, query) == 0) {
        MYSQL_RES* result = mysql_store_result(db);
        MYSQL_ROW row = mysql_fetch_row(result);
        
        if (row) {
            int id = atoi(row[0]);
            mysql_free_result(result);
            return id;
        }
        mysql_free_result(result);
    }
    
    // Créer nouveau patient
    snprintf(query, sizeof(query),
        "INSERT INTO patient (first_name, last_name) VALUES ('%s', '%s')",
        prenom, nom
    );
    
    if (mysql_query(db, query) == 0) {
        return (int)mysql_insert_id(db);
    }
    
    return -1;
}
```

---

## 🧵 Gestion du main.c

### Point d'entrée du serveur

```c
// main.c

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <signal.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <mysql/mysql.h>

#include "types.h"
#include "client_manager.h"
#include "protocole_cbp.h"
#include "protocole_acbp.h"

// Variables globales
ClientList global_clients;
MYSQL* db_connection;
int server_running = 1;

// Ports de configuration
#define PORT_CBP 8080
#define PORT_ADMIN 8081

// Handler pour SIGINT (Ctrl+C)
void sigint_handler(int sig) {
    printf("\n[INFO] Arrêt du serveur...\n");
    server_running = 0;
}

// Thread pour gérer socket CBP
void* cbp_listener_thread(void* arg) {
    int server_socket = *(int*)arg;
    
    while (server_running) {
        struct sockaddr_in client_addr;
        socklen_t addr_len = sizeof(client_addr);
        
        int client_socket = accept(server_socket, (struct sockaddr*)&client_addr, &addr_len);
        
        if (client_socket < 0) {
            if (!server_running) break;
            perror("accept() CBP");
            continue;
        }
        
        printf("[CBP] Nouvelle connexion\n");
        
        // Créer thread détaché pour ce client
        pthread_t thread;
        int* socket_ptr = malloc(sizeof(int));
        *socket_ptr = client_socket;
        
        pthread_create(&thread, NULL, handle_cbp_client, socket_ptr);
        pthread_detach(thread);
    }
    
    return NULL;
}

// Thread pour gérer socket ACBP
void* acbp_listener_thread(void* arg) {
    int server_socket = *(int*)arg;
    
    while (server_running) {
        struct sockaddr_in client_addr;
        socklen_t addr_len = sizeof(client_addr);
        
        int client_socket = accept(server_socket, (struct sockaddr*)&client_addr, &addr_len);
        
        if (client_socket < 0) {
            if (!server_running) break;
            perror("accept() ACBP");
            continue;
        }
        
        printf("[ACBP] Nouvelle connexion Admin\n");
        
        // Créer thread détaché pour ce client
        pthread_t thread;
        int* socket_ptr = malloc(sizeof(int));
        *socket_ptr = client_socket;
        
        pthread_create(&thread, NULL, handle_acbp_client, socket_ptr);
        pthread_detach(thread);
    }
    
    return NULL;
}

int main(int argc, char* argv[]) {
    printf("========================================\n");
    printf("  SERVEUR RÉSERVATION - MULTI-THREADS  \n");
    printf("========================================\n");
    printf("Port CBP (patients)  : %d\n", PORT_CBP);
    printf("Port ACBP (admin)    : %d\n", PORT_ADMIN);
    printf("========================================\n\n");
    
    // Gérer Ctrl+C
    signal(SIGINT, sigint_handler);
    
    // Initialiser liste clients
    client_list_init(&global_clients);
    
    // Connexion MySQL
    db_connection = mysql_init(NULL);
    if (!mysql_real_connect(db_connection, 
        "192.168.0.16",  // Host
        "Student",       // User
        "PassStudent1_", // Password
        "PourStudent",   // Database
        0, NULL, 0)) {
        
        fprintf(stderr, "Erreur MySQL : %s\n", mysql_error(db_connection));
        return 1;
    }
    
    printf("[INFO] Connexion MySQL établie\n");
    
    // Créer socket CBP
    int socket_cbp = socket(AF_INET, SOCK_STREAM, 0);
    int opt = 1;
    setsockopt(socket_cbp, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));
    
    struct sockaddr_in addr_cbp;
    addr_cbp.sin_family = AF_INET;
    addr_cbp.sin_addr.s_addr = INADDR_ANY;
    addr_cbp.sin_port = htons(PORT_CBP);
    
    if (bind(socket_cbp, (struct sockaddr*)&addr_cbp, sizeof(addr_cbp)) < 0) {
        perror("bind() CBP");
        return 1;
    }
    
    if (listen(socket_cbp, 10) < 0) {
        perror("listen() CBP");
        return 1;
    }
    
    printf("[INFO] Socket CBP en écoute sur port %d\n", PORT_CBP);
    
    // Créer socket ACBP
    int socket_acbp = socket(AF_INET, SOCK_STREAM, 0);
    setsockopt(socket_acbp, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));
    
    struct sockaddr_in addr_acbp;
    addr_acbp.sin_family = AF_INET;
    addr_acbp.sin_addr.s_addr = INADDR_ANY;
    addr_acbp.sin_port = htons(PORT_ADMIN);
    
    if (bind(socket_acbp, (struct sockaddr*)&addr_acbp, sizeof(addr_acbp)) < 0) {
        perror("bind() ACBP");
        return 1;
    }
    
    if (listen(socket_acbp, 10) < 0) {
        perror("listen() ACBP");
        return 1;
    }
    
    printf("[INFO] Socket ACBP en écoute sur port %d\n", PORT_ADMIN);
    
    // Créer threads listeners
    pthread_t thread_cbp, thread_acbp;
    
    pthread_create(&thread_cbp, NULL, cbp_listener_thread, &socket_cbp);
    pthread_create(&thread_acbp, NULL, acbp_listener_thread, &socket_acbp);
    
    printf("[INFO] Serveur démarré. Ctrl+C pour arrêter.\n\n");
    
    // Attendre threads
    pthread_join(thread_cbp, NULL);
    pthread_join(thread_acbp, NULL);
    
    // Nettoyer
    close(socket_cbp);
    close(socket_acbp);
    mysql_close(db_connection);
    client_list_destroy(&global_clients);
    
    printf("[INFO] Serveur arrêté proprement.\n");
    
    return 0;
}
```

---

## 📦 Makefile

```makefile
# Makefile pour Serveur Réservation

CC = gcc
CFLAGS = -Wall -Wextra -pthread -I./include -I/usr/include/mysql
LDFLAGS = -pthread -lmysqlclient

SRC_DIR = src
OBJ_DIR = obj
BIN_DIR = bin

TARGET = $(BIN_DIR)/serveur_reservation

SRCS = $(wildcard $(SRC_DIR)/*.c)
OBJS = $(SRCS:$(SRC_DIR)/%.c=$(OBJ_DIR)/%.o)

all: directories $(TARGET)

directories:
	@mkdir -p $(OBJ_DIR) $(BIN_DIR)

$(TARGET): $(OBJS)
	$(CC) $(OBJS) -o $@ $(LDFLAGS)
	@echo "✅ Compilation terminée : $(TARGET)"

$(OBJ_DIR)/%.o: $(SRC_DIR)/%.c
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -rf $(OBJ_DIR) $(BIN_DIR)
	@echo "🧹 Nettoyage effectué"

run: all
	./$(TARGET)

.PHONY: all clean run directories
```

---

## 🧪 Tests

### 1. Test avec le Client Admin Java existant

```bash
# Terminal 1 : Lancer le serveur C
cd serveur_reservation
make run

# Terminal 2 : Lancer le Client Admin Java
cd eval_2
mvn exec:java -Dexec.mainClass="hepl.fead.adminclient.AdminClient" -Dexec.args="localhost 8081"
```

**Résultat attendu** :
- Le client Admin se connecte au serveur C
- Affiche la liste des clients CBP connectés

### 2. Test protocole CBP avec netcat

```bash
# Terminal 1 : Serveur
./bin/serveur_reservation

# Terminal 2 : Client telnet
telnet localhost 8080

# Commandes à tester
LOGIN_PATIENT#Dupont#Jean
LIST_CONSULTATIONS#2025-01-01#2025-12-31
BOOK_CONSULTATION#123#Grippe
LOGOUT
```

---

## 🎯 Checklist de développement

### Phase 1 : Infrastructure (2-3 heures)
- [ ] Créer structure projet (dossiers src/, include/, etc.)
- [ ] Implémenter `client_manager.c` (liste thread-safe)
- [ ] Implémenter `network_utils.c` (send_line, recv_line)
- [ ] Tester mutex et liste clients

### Phase 2 : Protocole ACBP (2 heures)
- [ ] Implémenter `protocole_acbp.c`
- [ ] Commande LIST_CLIENTS
- [ ] Tester avec Client Admin Java

### Phase 3 : Protocole CBP (4-5 heures)
- [ ] Connexion MySQL (libmysqlclient)
- [ ] Commande LOGIN_PATIENT
- [ ] Commande LIST_CONSULTATIONS
- [ ] Commande BOOK_CONSULTATION
- [ ] Commande MY_BOOKINGS
- [ ] Commande CANCEL_BOOKING
- [ ] Commande LOGOUT

### Phase 4 : Main et intégration (2 heures)
- [ ] Implémenter `main.c` avec 2 sockets
- [ ] Threads listeners (CBP et ACBP)
- [ ] Gestion signaux (SIGINT)
- [ ] Tests d'intégration complets

### Phase 5 : Documentation (1 heure)
- [ ] README avec instructions compilation
- [ ] Documentation protocoles
- [ ] Exemples d'utilisation

---

## 📚 Dépendances

### Bibliothèques requises

```bash
# Ubuntu/Debian
sudo apt-get install libmysqlclient-dev
sudo apt-get install mysql-client

# Vérification
gcc --version
mysql_config --version
```

### Headers nécessaires

```c
#include <pthread.h>        // Threads POSIX
#include <sys/socket.h>     // Sockets
#include <netinet/in.h>     // Structures sockaddr_in
#include <arpa/inet.h>      // inet_ntop()
#include <mysql/mysql.h>    // MySQL C API
#include <signal.h>         // Gestion signaux
#include <string.h>         // Manipulation strings
```

---

## 🐛 Debugging

### Afficher les clients connectés

```c
// Dans main.c, ajouter thread de monitoring

void* monitor_thread(void* arg) {
    while (server_running) {
        sleep(10);
        
        pthread_mutex_lock(&global_clients.mutex);
        printf("\n[MONITOR] Clients connectés : %d\n", global_clients.count);
        for (int i = 0; i < global_clients.count; i++) {
            if (global_clients.clients[i].is_active) {
                printf("  - %s %s (ID: %d, IP: %s)\n",
                    global_clients.clients[i].prenom,
                    global_clients.clients[i].nom,
                    global_clients.clients[i].patient_id,
                    global_clients.clients[i].ip_address
                );
            }
        }
        pthread_mutex_unlock(&global_clients.mutex);
    }
    return NULL;
}
```

### Valgrind (fuites mémoire)

```bash
valgrind --leak-check=full ./bin/serveur_reservation
```

---

## 🎯 Critères de réussite

1. ✅ **Compilation sans warning** : `gcc -Wall -Wextra`
2. ✅ **2 sockets en écoute simultanée** : PORT_CBP et PORT_ADMIN
3. ✅ **Client Admin Java fonctionne** : Affiche liste clients
4. ✅ **Commande LIST_CLIENTS correcte** : Format `IP;Nom;Prénom;ID`
5. ✅ **Thread-safety** : Pas de race conditions sur liste clients
6. ✅ **Protocole CBP fonctionnel** : Login, liste, réservation
7. ✅ **Gestion propre** : SIGINT, fermeture threads, free() mémoire
8. ✅ **Logs clairs** : Affichage connexions/déconnexions

---

## 📞 Support

Si vous rencontrez des problèmes :
1. Vérifier les logs du serveur C
2. Tester avec `netcat` ou `telnet`
3. Utiliser `gdb` pour debugging
4. Vérifier les permissions firewall (ports 8080/8081)

---

**Bon courage ! 🚀**

*Document créé le 2025-01-05*

