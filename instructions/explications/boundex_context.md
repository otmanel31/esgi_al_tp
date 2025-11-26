# Analyse par domaine métier et Bounded Contexts

## 🎯 Importance en architecture

**l'analyse par domaine métier est CRUCIALE** en architecture moderne pour plusieurs raisons :

### 1. **Alignement métier-technique**
- Les microservices doivent refléter l'organisation métier
- Facilite la communication entre développeurs et experts métier
- Réduit les malentendus et les erreurs de conception

### 2. **Autonomie des équipes**
- Chaque équipe peut posséder un domaine complet
- Déploiements indépendants
- Évolutions sans coordination excessive

### 3. **Évolutivité**
- Ajout de nouvelles fonctionnalités sans impacter l'ensemble
- Suppression ou remplacement de fonctionnalités obsolètes
- Scalabilité ciblée selon les besoins métier

---

## 📦 Bounded Context (DDD)

### Définition

Un **Bounded Context** est une **frontière explicite** dans laquelle :
- Un **modèle de domaine** spécifique est valide
- Les termes métier ont une **signification précise**
- Les **règles métier** sont cohérentes

### Analogie simple

Imaginez une entreprise :
- En **Comptabilité**, "Client" = personne qui doit de l'argent
- En **Marketing**, "Client" = personne à fidéliser
- En **Support**, "Client" = personne ayant des problèmes

**Même mot, contextes différents !**

---

## 🔍 Dans votre projet IoT

### Exemple 1 : Capteur

**Dans le contexte "Gestion des Capteurs"** (Sensor Management) :
```java
class Sensor {
    String id;
    String name;
    SensorType type;
    Status status;  // ACTIVE, INACTIVE
}
```
→ Focus sur l'**identité** et la **configuration**

**Dans le contexte "Ingestion de Données"** :
```java
class Sensor {
    String id;
    // Pas besoin du nom ou du statut !
}
```
→ Focus sur l'**identifiant** uniquement

**Dans le contexte "Commande et Contrôle"** :
```java
class Sensor {
    String id;
    String firmwareVersion;
    boolean supportsRemoteControl;
}
```
→ Focus sur les **capacités** du capteur

### Exemple 2 : Les 4 Bounded Contexts de votre monolithe

```
┌─────────────────────────────────────┐
│   MONOLITHE ACTUEL (PROBLÈME)      │
│                                     │
│  ┌──────────────────────────────┐  │
│  │ Tout est mélangé !           │  │
│  │ - API REST                   │  │
│  │ - Ingestion données          │  │
│  │ - Envoi commandes            │  │
│  │ - Stockage                   │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│   APRÈS DÉCOUPAGE (SOLUTION)        │
│                                     │
│  ┌─────────────┐  ┌──────────────┐ │
│  │ Sensor Mgmt │  │ Data Query   │ │
│  │ (CRUD)      │  │ (Read-only)  │ │
│  └─────────────┘  └──────────────┘ │
│                                     │
│  ┌─────────────┐  ┌──────────────┐ │
│  │ Data Ingest │  │ Command Svc  │ │
│  │ (Write-only)│  │ (Async)      │ │
│  └─────────────┘  └──────────────┘ │
└─────────────────────────────────────┘
```

---

## ✅ Avantages des Bounded Contexts

### 1. **Modèles simplifiés**
Chaque contexte a seulement ce dont il a besoin.

### 2. **Moins de couplage**
Les contextes communiquent par **événements** ou **API**, pas par partage de code.

### 3. **Technologies adaptées**
- **Ingestion** → Kafka + Time-series DB
- **Query** → Cache + Read-optimized DB
- **Commands** → Queue asynchrone
- **Management** → CRUD classique

### 4. **Scalabilité indépendante**
Si l'ingestion reçoit 1M msg/sec, seul ce service scale.

---

## 🎓 Pour votre TP

Lors de votre analyse, identifiez :

1. **Quelles classes appartiennent à quel contexte ?**
2. **Où sont les frontières naturelles ?**
3. **Quelles données doivent être partagées/dupliquées ?**
4. **Quels événements permettent la communication ?**

**Astuce :** Cherchez les verbes métier !
- **Enregistrer** un capteur → Sensor Management
- **Recevoir** des données → Data Ingestion
- **Consulter** l'historique → Data Query
- **Envoyer** une commande → Command Service