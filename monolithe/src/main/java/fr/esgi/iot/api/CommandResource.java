package fr.esgi.iot.api;

import fr.esgi.iot.model.Command;
import fr.esgi.iot.service.CommandService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

/**
 * API REST pour l'envoi de commandes aux capteurs.
 * 
 * PROBLÈME CRITIQUE : Cette API est SYNCHRONE et BLOQUANTE.
 * Lorsqu'un client envoie une commande, il doit attendre 2-5 secondes
 * pour obtenir une réponse. Cela :
 * - Bloque le thread HTTP
 * - Limite le nombre de requêtes concurrentes
 * - Crée une mauvaise expérience utilisateur
 * - Ne scale pas
 * 
 * SOLUTION attendue de l'étudiant :
 * - Implémenter un pattern asynchrone (POST retourne 202 Accepted + location)
 * - Le client interroge ensuite GET /api/commands/{id} pour le statut
 * - Ou utiliser WebSockets / Server-Sent Events pour les notifications
 */
@Path("/api/commands")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommandResource {

    private static final Logger LOG = Logger.getLogger(CommandResource.class);

    @Inject
    CommandService commandService;

    /**
     * Envoie une commande à un capteur et ATTEND la réponse (BLOQUANT).
     * POST /api/commands
     * 
     * Body:
     * {
     *   "sensorId": "TEMP-001",
     *   "command": "READ_VALUE"
     * }
     * 
     * PROBLÈME : Cette méthode bloque pendant 2-5 secondes !
     */
    @POST
    public Response sendCommand(CommandRequest request) {
        LOG.infof("📨 Réception d'une commande: %s pour capteur %s", 
                  request.command, request.sensorId);

        // Validation basique
        if (request.sensorId == null || request.sensorId.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Le champ 'sensorId' est obligatoire"))
                    .build();
        }

        if (request.command == null || request.command.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Le champ 'command' est obligatoire"))
                    .build();
        }

        try {
            // PROBLÈME : Appel SYNCHRONE et BLOQUANT
            // Le thread HTTP est bloqué pendant toute la durée de l'exécution
            Command result = commandService.sendCommandAndWait(
                    request.sensorId, 
                    request.command
            );

            LOG.infof("✅ Commande terminée: %s (status: %s)", result.getId(), result.getStatus());

            return Response.ok(result).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            LOG.errorf("❌ Erreur lors de l'envoi de la commande: %s", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erreur serveur: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupère l'état d'une commande.
     * GET /api/commands/{id}
     * 
     * NOTE : Cette méthode existe mais n'est pas utilisée dans le monolithe actuel.
     * L'étudiant devrait l'utiliser dans une architecture asynchrone.
     */
    @GET
    @Path("/{id}")
    public Response getCommandStatus(@PathParam("id") String commandId) {
        return commandService.getCommand(commandId)
                .map(command -> Response.ok(command).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Commande non trouvée: " + commandId))
                        .build());
    }

    /**
     * Classe pour la requête d'envoi de commande.
     */
    public static class CommandRequest {
        public String sensorId;
        public String command;
    }

    /**
     * Classe pour les messages d'erreur.
     */
    public static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
