# Sensor Management Service

Microservice gérant le cycle de vie des capteurs IoT.

## Port
8081

## Endpoints
- `GET /api/sensors` - Liste tous les capteurs
- `GET /api/sensors/{id}` - Détails d'un capteur
- `POST /api/sensors` - Créer un capteur
- `PUT /api/sensors/{id}` - Mettre à jour
- `PUT /api/sensors/{id}/communication` - MAJ dernière communication
- `DELETE /api/sensors/{id}` - Supprimer

## Démarrage
```bash
mvn quarkus:dev
```
