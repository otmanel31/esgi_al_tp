# Command Service

Microservice gérant l'envoi asynchrone de commandes vers les capteurs.

## Port
8084

## Endpoints
- `POST /api/commands` - Envoyer commande (202 Accepted)
- `GET /api/commands/{id}` - Statut commande

## Démarrage
```bash
mvn quarkus:dev
```
