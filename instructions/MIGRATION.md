# Guide de migration vers microservices

## 🎯 Objectif

Transformer l'application monolithique en architecture microservices, en respectant les principes de découplage et de séparation des responsabilités.

---

## 📋 Étapes de migration

### Étape 1 : Conception de l'architecture (1h)

#### 1.1 Identifier les microservices

**Méthode recommandée : Bounded Contexts (DDD)**

Pour chaque responsabilité identifiée dans l'analyse, créez un microservice :

| Bounded Context | Nom du microservice | Responsabilités | Données gérées |
|-----------------|---------------------|-----------------|----------------|
| Gestion capteurs | `sensor-management-service` | CRUD capteurs, statut | Sensor |
| Ingestion données | `data-ingestion-service` | Réception messages, validation | - |
| Time-series | `timeseries-service` | Stockage historique | SensorData |
| Commandes | `command-service` | Envoi commandes (async) | Command |

**Nombre recommandé :** 3 à 6 microservices

**⚠️ Attention :** Ne créez pas trop de services ! Chaque service a un coût :
- Complexité opérationnelle
- Communication réseau
- Déploiement
- Monitoring

#### 1.2 Définir les communications

Pour chaque microservice, définissez :

**Exemple : `data-ingestion-service`**

| Communication | Vers | Type | Données |
|---------------|------|------|---------|
| Reçoit données | Broker (simulé) | Message | SensorData brut |
| Valide capteur | `sensor-management-service` | REST (GET) | Sensor |
| Envoie données | `timeseries-service` | REST (POST) | SensorData |
| Met à jour capteur | `sensor-management-service` | REST (PUT) | lastCommunication |

**Questions à se poser :**
- Communication **synchrone** (REST) ou **asynchrone** (messages) ?
- Qui appelle qui ?
- Que se passe-t-il en cas d'erreur ?

#### 1.3 Dessiner le diagramme C4 - Niveau 2 (Conteneurs)

**Exemple de structure :**

