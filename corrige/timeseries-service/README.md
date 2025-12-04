# TimeSeries Service

Microservice gérant le stockage et la consultation de l'historique des données capteurs.

## Port
8083

## Endpoints
- `POST /api/data` - Stocker une donnée
- `GET /api/data/{sensorId}?limit=50` - Historique d'un capteur
- `GET /api/data/{sensorId}/latest` - Dernière valeur

## Démarrage
```bash
mvn quarkus:dev
```
