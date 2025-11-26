# TP Architecture Logicielle - Migration Monolithe vers Microservices

## 📚 Contexte pédagogique

**Niveau** : Ingénieur 4ème année  
**Durée** : 4h en présentiel + travail à la maison  
**Domaine** : IoT - Gestion de capteurs connectés

## Ressource montée en compétence Java Quarkus

- **Baeldung** : https://www.baeldung.com/quarkus-io
- **Site officiel** : https://quarkus.io/guides/getting-started
- **Youtube** : https://www.youtube.com/watch?v=KZnQ5R8Kd4I, https://www.youtube.com/watch?v=spWnqkbFgH4

### Modules concernés

- **Module 1** : Introduction à l'architecture logicielle (concepts, composants, principes SOLID)
- **Module 2** : Styles et patterns architecturaux (monolithique vs microservices, attributs de qualité)

---

## 🎯 Objectifs pédagogiques

À l'issue de ce TP, vous serez capable de :

1. **Analyser** les limites d'une architecture monolithique
2. **Identifier** les responsabilités et domaines fonctionnels d'une application
3. **Concevoir** un découpage en microservices cohérent
4. **Implémenter** une architecture microservices avec communication inter-services
5. **Documenter** une architecture logicielle (diagrammes C4 ou équivalent)
6. **Appliquer** les principes SOLID et de séparation des responsabilités

---

## 📖 Contexte métier

Vous travaillez sur une plateforme IoT qui gère des capteurs connectés (température, humidité, pression, etc.).

### Fonctionnalités actuelles

1. **Réception de données capteurs** : 
   - Les capteurs envoient leurs données via un broker de messages (simulé)
   - Les données sont de types variés (température, humidité, etc.)

2. **Stockage des données** :
   - **Base relationnelle** (simulée en mémoire) : informations du capteur + dernière date de communication
   - **Base time-series** (simulée en mémoire) : historique des valeurs mesurées

3. **API de consultation** :
   - Récupération des données capteurs
   - Consultation de l'historique

4. **API de commande** :
   - Envoi de commandes aux capteurs distants
   - Attente synchrone (bloquante) d'un retour hypothétique

---

## 🏗️ Architecture actuelle (Monolithe)

L'application fournie est un **monolithe** en Java avec Quarkus.

