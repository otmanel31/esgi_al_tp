# Architecture Microservices - Explications détaillées

## 🎯 Objectif du découpage

L'objectif de cette migration est de transformer un monolithe en une architecture microservices **scalable, résiliente et maintenable**.

---

## 📊 Analyse du monolithe

### Responsabilités identifiées

| Responsabilité | Classes concernées | Problèmes |
|----------------|-------------------|-----------|
| **Gestion capteurs** | SensorRepository, partie de SensorDataResource | Couplé avec la consultation des données |
| **Ingestion données** | MessageBrokerSimulator, SensorDataService | Trop de responsabilités (validation + stockage) |
| **Stockage time-series** | TimeSeriesRepository, partie de SensorDataResource | Requêtes croisées avec les capteurs |
| **Commandes** | CommandService, CommandResource | **Appels synchrones bloquants** |

### Violations SOLID identifiées

#### 1. Single Responsibility Principle (SRP)

**SensorDataService** viole SRP :
- ✅ Valide les données
- ✅ Met à jour les capteurs
- ✅ Stocke dans la time-series
- ❌ **3 responsabilités** = violation !

**Solution microservices** : Séparer en 3 services distincts

#### 2. Open/Closed Principle (OCP)

Ajouter un nouveau type de capteur nécessite de modifier :
- `SensorDataService.isValueValid()` (switch/case)
- Logique de validation hard-codée

**Solution** : Pattern Strategy ou validation configurable

#### 3. Dependency Inversion Principle (DIP)

Les services dépendent d'implémentations concrètes (repositories) au lieu d'abstractions.

**Solution** : Introduire des interfaces

---

## 🏗️ Architecture cible

### Découpage en 4 microservices


```
┌───────────────────────────────────────────────────────────┐
│                    SYSTÈME IOT                            │
│                                                           │
│  ┌───────────────┐         ┌───────────────┐              │
│  │   Sensor      │◄────────│    Data       │              │
│  │  Management   │  REST   │  Ingestion    │              │
│  │   :8081       │         │   :8082       │              │
│  └───────┬───────┘         └───────┬───────┘              │
│          │                         │                      │
│          │ REST                    │ REST                 │
│          │                         │                      │
│  ┌───────▼───────┐         ┌───────▼───────┐              │
│  │   Command     │         │  TimeSeries   │              │
│  └───────────────┘         └───────────────┘              │
└───────────────────────────────────────────────────────────┘
```

---

## 🔍 Détail des microservices

### 1. Sensor Management Service

**Bounded Context** : Gestion du référentiel des capteurs

**Responsabilité unique** : CRUD des capteurs + métadonnées

**Données** :
```java
class Sensor {
    String id;
    String name;
    String type;
    String location;
    LocalDateTime lastCommunication;
    String status;
}
```

**Endpoints** :
- `GET /api/sensors` - Liste
- `GET /api/sensors/{id}` - Détails
- `POST /api/sensors` - Création
- `PUT /api/sensors/{id}` - Mise à jour
- `PUT /api/sensors/{id}/communication` - MAJ dernière com
- `DELETE /api/sensors/{id}` - Suppression

**Appelé par** :
- Data Ingestion (vérification capteur)
- Clients externes (consultation)

### 2. Data Ingestion Service

**Bounded Context** : Orchestration de l'ingestion

**Responsabilité unique** : Réception + validation + orchestration

**Workflow** :
1. Reçoit message du broker
2. Vérifie capteur existe (appel sensor-management)
3. Valide les données
4. Stocke dans time-series
5. Met à jour dernière communication

**Endpoints** :
- `POST /api/ingest` - Ingestion

**Appelle** :
- Sensor Management (GET + PUT)
- TimeSeries (POST)

**Amélioration vs monolithe** :
- Séparation orchestration / stockage
- Gestion d'erreurs avec retry/fallback

### 3. TimeSeries Service

**Bounded Context** : Historique des mesures

**Responsabilité unique** : Stockage + consultation time-series

**Données** :
```java
class SensorData {
    String sensorId;
    LocalDateTime timestamp;
    double value;
    String unit;
}
```

**Endpoints** :
- `POST /api/data` - Stockage
- `GET /api/data/{sensorId}` - Historique
- `GET /api/data/{sensorId}/latest` - Dernière valeur

**Scalabilité** :
- Peut être scalé indépendamment
- En production : InfluxDB, TimescaleDB

### 4. Command Service

**Bounded Context** : Commandes vers capteurs

**Responsabilité unique** : Envoi asynchrone de commandes

**Données** :
```java
class Command {
    String id;
    String sensorId;
    String command;
    LocalDateTime sentAt;
    String status; // PENDING, SUCCESS, FAILED
    String response;
}
```

**Endpoints** :
- `POST /api/commands` - Envoi (**202 Accepted**)
- `GET /api/commands/{id}` - Statut

**Amélioration MAJEURE** : Asynchronisme

**Avant (Monolithe)** :
```java
POST /api/commands
↓ (bloque 2-5s)
Retourne 200 OK + résultat
```

**Après (Microservices)** :
```java
POST /api/commands
↓ (immédiat)
Retourne 202 Accepted + commandId

GET /api/commands/{id}
↓
Retourne statut (PENDING/SUCCESS/FAILED)
```
## 🔗 Communication inter-services

### Pattern : REST Client avec résilience

**Exemple : Data Ingestion → Sensor Management**

```java
@RegisterRestClient(configKey = "sensor-management")
public interface SensorManagementClient {
    @GET
    @Path("/api/sensors/{id}")
    Sensor getSensorById(@PathParam("id") String id);
}

// Utilisation avec résilience
@Retry(maxRetries = 3, delay = 500)
@Timeout(value = 2, unit = SECONDS)
@Fallback(fallbackMethod = "getSensorFallback")
public Sensor getSensor(String sensorId) {
    return sensorClient.getSensorById(sensorId);
}
```

### Matrice des communications

| De → Vers | Sensor Mgmt | Data Ingestion | TimeSeries | Command |
|-----------|-------------|----------------|------------|---------|
| **Client** | REST GET | - | REST GET | REST POST/GET |
| **Data Ingestion** | REST GET/PUT | - | REST POST | - |
| **Broker Simulator** | - | REST POST | - | - |

---

## 🎯 Avantages de l'architecture

### 1. Scalabilité indépendante

**Scénario** : Pic de données capteurs

**Monolithe** : Scaler toute l'app (coûteux)

**Microservices** : Scaler uniquement `data-ingestion-service`

```bash
# Kubernetes
kubectl scale deployment data-ingestion --replicas=5
```

### 2. Déploiement indépendant

**Scénario** : Bug dans le service de commandes

**Monolithe** : Redéployer tout (downtime)

**Microservices** : Redéployer uniquement `command-service`

### 3. Résilience

**Scénario** : Panne de `timeseries-service`

**Monolithe** : Crash complet

**Microservices** :
- Data Ingestion continue (avec fallback)
- Sensor Management continue
- Command continue

### 4. API non-bloquante

**Scénario** : 100 commandes en parallèle

**Monolithe** :
- Max 10-20 threads HTTP
- Blocage mutuel
- Timeouts

**Microservices** :
- Retour immédiat (202)
- Pool de threads dédié
- Pas de blocage HTTP

---

## ⚠️ Trade-offs et limitations

### Complexité opérationnelle

| Aspect | Monolithe | Microservices |
|--------|-----------|---------------|
| **Nb de déploiements** | 1 | 4 |
| **Monitoring** | Simple | 4× plus complexe |
| **Logs** | Centralisés | Distribués |
| **Debugging** | Facile | Plus difficile |

### Latence réseau

**Monolithe** : Appels en mémoire (ns)

**Microservices** : Appels HTTP (ms)

**Exemple** : Ingestion de données
- Monolithe : 1 appel direct
- Microservices : 3 appels REST (sensor + timeseries + sensor)

### Consistance des données

**Problème** : Transactions distribuées

**Exemple** : Si `timeseries` échoue après `sensor` MAJ ?

**Solutions** :
- Saga Pattern
- Event Sourcing
- Compensation

---

## 🚀 Évolutions futures

### 1. Event-Driven Architecture

Remplacer REST par messaging :

```
Broker (Kafka/RabbitMQ)
    ↓ event: sensor.data.received
Data Ingestion
    ↓ event: sensor.data.validated
TimeSeries (consommateur)
```

### 2. API Gateway

Centraliser les appels clients :

```
Client → API Gateway → [Sensor | TimeSeries | Command]
```

### 3. Service Mesh

Gérer communication avec Istio/Linkerd :
- Load balancing automatique
- Circuit breaker
- mTLS entre services

### 4. Observabilité

**Logs** : ELK Stack (Elasticsearch, Logstash, Kibana)

**Metrics** : Prometheus + Grafana

**Tracing** : Jaeger (distributed tracing)

---

## 📚 Patterns appliqués

### 1. Database per Service

Chaque service a son stockage isolé (en mémoire ici).

**Avantage** : Découplage complet

**Inconvénient** : Pas de JOIN SQL

### 2. API Gateway Pattern (à implémenter)

Point d'entrée unique pour les clients.

### 3. Retry + Circuit Breaker

Résilience avec MicroProfile Fault Tolerance.

### 4. Async Request-Reply

Commandes asynchrones avec polling.

**Alternative** : WebSockets / Server-Sent Events

---

## 🎓 Conclusion

### Points clés du corrigé

1. ✅ **Séparation claire** des responsabilités
2. ✅ **Communication REST** avec gestion d'erreurs
3. ✅ **Asynchronisme** pour les commandes
4. ✅ **Scalabilité** indépendante
5. ✅ **Résilience** (retry, fallback, timeout)

### Ce qu'on a gagné

- Déploiement indépendant
- Scaling ciblé
- Isolation des pannes
- API non-bloquante
- Meilleure maintenabilité

### Ce qu'on a perdu

- Simplicité opérationnelle
- Latence (appels réseau)
- Débogage plus complexe

---

**Cette architecture est un bon compromis entre simplicité et bénéfices des microservices pour une application IoT.**

Ce corrigé comporte:
- le présent .md décrivant l'architecture globale
- Des diagrammes PlantUml. Vous pouvez installer une extension VSCODE ou autre plugin selon l'IDE afin de visualiser les schémas et diagrammes.
