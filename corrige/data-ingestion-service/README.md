# Data Ingestion Service

Microservice orchestrant l'ingestion et la validation des données capteurs.

## Port
8082

## Endpoints
- `POST /api/ingest` - Ingérer une donnée capteur

## Dépendances
- Sensor Management Service (8081)
- TimeSeries Service (8083)

## Démarrage
```bash
mvn quarkus:dev
```
