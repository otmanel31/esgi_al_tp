package fr.esgi.iot.api;

import fr.esgi.iot.model.Sensor;
import fr.esgi.iot.model.SensorData;
import fr.esgi.iot.repository.SensorRepository;
import fr.esgi.iot.service.SensorDataService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * API REST pour la lecture des données capteurs.
 * 
 * PROBLÈME : Cette ressource REST mélange plusieurs responsabilités :
 * - Gestion des capteurs (liste, détails)
 * - Consultation des données time-series
 * 
 * En architecture microservices, cela devrait être séparé en :
 * - Un service "Sensor Management"
 * - Un service "Sensor Data / Time-Series"
 */
@Path("/api/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorDataResource {

    @Inject
    SensorRepository sensorRepository;

    @Inject
    SensorDataService sensorDataService;

    /**
     * Récupère la liste de tous les capteurs.
     * GET /api/sensors
     */
    @GET
    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    /**
     * Récupère les détails d'un capteur spécifique.
     * GET /api/sensors/{id}
     */
    @GET
    @Path("/{id}")
    public Response getSensorById(@PathParam("id") String id) {
        return sensorRepository.findById(id)
                .map(sensor -> Response.ok(sensor).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Capteur non trouvé: " + id))
                        .build());
    }

    /**
     * Récupère l'historique des données d'un capteur.
     * GET /api/sensors/{id}/data?limit=50
     * 
     * PROBLÈME : Requête croisée entre la gestion des capteurs et les données.
     * En microservices, cela nécessiterait une communication inter-services.
     */
    @GET
    @Path("/{id}/data")
    public Response getSensorData(
            @PathParam("id") String id,
            @QueryParam("limit") @DefaultValue("50") int limit) {
        
        // Vérification de l'existence du capteur
        if (!sensorRepository.exists(id)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Capteur non trouvé: " + id))
                    .build();
        }

        List<SensorData> data = sensorDataService.getSensorHistory(id, limit);
        return Response.ok(data).build();
    }

    /**
     * Récupère toutes les données de tous les capteurs (dernières valeurs).
     * GET /api/sensors/data/all
     * 
     * PROBLÈME : Endpoint potentiellement coûteux sans pagination.
     */
    @GET
    @Path("/data/all")
    public List<SensorData> getAllSensorData() {
        return sensorDataService.getAllSensorData();
    }

    /**
     * Classe interne pour les messages d'erreur.
     */
    public static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
