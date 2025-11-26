# Guide d'analyse du monolithe

## 🎯 Objectif

Analyser l'application monolithique pour identifier ses responsabilités, ses problèmes et préparer le découpage en microservices.

---

## 📋 Étapes d'analyse

### 1. Exploration du code (30 min)

#### 1.1 Lancer l'application

```bash
cd monolithe
mvn quarkus:dev
```

Observez les logs au démarrage. Que se passe-t-il ?

#### 1.2 Tester les endpoints

```bash
# Liste des capteurs
curl http://localhost:8080/api/sensors

# Données d'un capteur
curl http://localhost:8080/api/sensors/TEMP-001/data?limit=10

# Envoyer une commande (ATTENTION: va bloquer 2-5 secondes)
curl -X POST http://localhost:8080/api/commands \
  -H "Content-Type: application/json" \
  -d '{"sensorId":"TEMP-001","command":"READ_VALUE"}'
```

**Questions à se poser :**
- Combien de temps prend la commande ?
- Que se passe-t-il si j'envoie plusieurs commandes en parallèle ?
- Comment sont générées les données capteurs ?

#### 1.3 Explorer la structure du code

Naviguez dans les packages :
- `model/` : Quels sont les objets métier ?
- `repository/` : Comment sont stockées les données ?
- `service/` : Quelles sont les logiques métier ?
- `api/` : Quels sont les endpoints exposés ?

---

### 2. Identification des responsabilités (30 min)

#### 2.1 Méthode : Analyse par couches

Pour chaque classe de service, identifiez :

| Classe | Responsabilité principale | Dépendances | Problèmes |
|--------|---------------------------|-------------|-----------|
| `MessageBrokerSimulator` | ? | ? | ? |
| `SensorDataService` | ? | ? | ? |
| `CommandService` | ? | ? | ? |

**Exemple de réponse attendue :**

| Classe | Responsabilité principale | Dépendances | Problèmes |
|--------|---------------------------|-------------|-----------|
| `MessageBrokerSimulator` | Simuler la réception de messages du broker | `SensorDataService` | Couplage direct avec le service métier |
| `SensorDataService` | Traiter et stocker les données capteurs | `SensorRepository`, `TimeSeriesRepository` | Trop de responsabilités (validation + stockage) |
| `CommandService` | Envoyer des commandes aux capteurs | `SensorRepository` | Appels synchrones bloquants |

#### 2.2 Méthode : Analyse par domaine métier (DDD)

Identifiez les **bounded contexts** :

1. **Gestion des capteurs** (Sensor Management)
   - Quelles classes sont concernées ?
   - Quelles données ?
   - Quelles opérations ?

2. **Ingestion de données** (Data Ingestion)
   - Quelles classes sont concernées ?
   - D'où viennent les données ?
   - Où sont-elles stockées ?

3. **Consultation des données** (Data Query)
   - Quelles classes sont concernées ?
   - Quels types de requêtes ?

4. **Commande et contrôle** (Command & Control)
   - Quelles classes sont concernées ?
   - Comment fonctionnent les commandes ?
   - Quel est le problème majeur ?

---

### 3. Identification des problèmes (15 min)

#### 3.1 Problèmes architecturaux

Remplissez ce tableau :

| Problème | Description | Impact | Fichier concerné |
|----------|-------------|--------|------------------|
| Couplage fort | ? | ? | ? |
| Violation SRP | ? | ? | ? |
| API synchrone | ? | ? | ? |
| Scalabilité | ? | ? | ? |
| Résilience | ? | ? | ? |

#### 3.2 Problèmes techniques

**Questions à répondre :**

1. **Pourquoi l'API `/api/commands` est-elle problématique ?**
   - Regardez `CommandService.sendCommandAndWait()`
   - Que fait `Thread.sleep()` ?
   - Quel est l'impact sur les threads HTTP ?

2. **Que se passe-t-il si `SensorDataService` plante ?**
   - Est-ce que l'API REST continue de fonctionner ?
   - Est-ce que le broker simulator continue ?

3. **Comment scaler cette application ?**
   - Peut-on scaler uniquement l'API de lecture ?
   - Peut-on scaler uniquement l'ingestion de données ?

---

### 4. Application des principes SOLID (15 min)

Pour chaque principe, identifiez les violations :

#### Single Responsibility Principle (SRP)

**Question :** Quelles classes ont plusieurs responsabilités ?

**Exemple :** `SensorDataService`
- ✅ Traitement des données
- ✅ Validation
- ✅ Mise à jour des capteurs
- ✅ Stockage time-series
- ❌ **Violation de SRP** : trop de responsabilités !

**À faire :** Identifiez d'autres violations

#### Open/Closed Principle (OCP)

**Question :** Que se passe-t-il si on veut ajouter un nouveau type de capteur ?
- Quels fichiers faut-il modifier ?
- Est-ce facile d'ajouter une nouvelle validation ?

#### Dependency Inversion Principle (DIP)

**Question :** Les services dépendent-ils d'abstractions ou d'implémentations concrètes ?
- Regardez les `@Inject` dans les services
- Y a-t-il des interfaces ?
- Les repositories sont-ils abstraits ?

---

### 5. Diagramme de dépendances (15 min)

Dessinez un diagramme montrant les dépendances actuelles.

**Exemple de notation :**

