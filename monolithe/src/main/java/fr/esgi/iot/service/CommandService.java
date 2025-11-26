package fr.esgi.iot.service;

import fr.esgi.iot.model.Command;
import fr.esgi.iot.model.Sensor;
import fr.esgi.iot.repository.SensorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service métier gérant les commandes vers les capteurs.
 * 
 * PROBLÈME MAJEUR : Exécution SYNCHRONE et BLOQUANTE des commandes.
 * L'API attend une réponse hypothétique du capteur distant, ce qui :
 * - Bloque le thread HTTP
 * - Crée un timeout si le capteur ne répond pas
 * - Empêche la scalabilité
 * 
 * À AMÉLIORER par l'étudiant : Implémenter un pattern asynchrone
 * (ex: callbacks, CompletableFuture, messaging asynchrone).
 */
@ApplicationScoped
public class CommandService {

    private static final Logger LOG = Logger.getLogger(CommandService.class);

    @Inject
    SensorRepository sensorRepository;

    // Stockage en mémoire des commandes (devrait être une vraie BDD)
    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    /**
     * Envoie une commande à un capteur et ATTEND la réponse (SYNCHRONE).
     * 
     * PROBLÈME : Cette méthode simule un appel bloquant de 2-5 secondes.
     * En production, cela bloquerait le thread et limiterait la concurrence.
     */
    public Command sendCommandAndWait(String sensorId, String commandType) {
        LOG.infof("📤 Envoi de commande %s vers capteur %s", commandType, sensorId);

        // Vérification de l'existence du capteur
        Optional<Sensor> sensorOpt = sensorRepository.findById(sensorId);
        if (sensorOpt.isEmpty()) {
            LOG.warnf("⚠️ Capteur inconnu: %s", sensorId);
            throw new IllegalArgumentException("Capteur inconnu: " + sensorId);
        }

        Sensor sensor = sensorOpt.get();
        if (!"ACTIVE".equals(sensor.getStatus())) {
            LOG.warnf("⚠️ Capteur non actif: %s (status: %s)", sensorId, sensor.getStatus());
            throw new IllegalStateException("Le capteur n'est pas actif");
        }

        // Création de la commande
        String commandId = UUID.randomUUID().toString();
        Command command = new Command(commandId, sensorId, commandType);
        commands.put(commandId, command);

        // PROBLÈME : Simulation d'un appel SYNCHRONE et BLOQUANT
        // En réalité, on enverrait un message et on attendrait une réponse
        try {
            LOG.infof("⏳ Attente de la réponse du capteur %s... (BLOQUANT)", sensorId);
            
            // Simulation d'un délai réseau + traitement capteur (2-5 secondes)
            Thread.sleep(2000 + (long) (Math.random() * 3000));
            
            // Simulation d'une réponse aléatoire
            boolean success = Math.random() > 0.2; // 80% de succès
            
            if (success) {
                command.setStatus("SUCCESS");
                command.setResponse(generateSuccessResponse(commandType));
                LOG.infof("✅ Commande %s exécutée avec succès", commandId);
            } else {
                command.setStatus("FAILED");
                command.setResponse("Erreur: Timeout ou capteur injoignable");
                LOG.warnf("❌ Échec de la commande %s", commandId);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            command.setStatus("FAILED");
            command.setResponse("Erreur: Interruption du thread");
            LOG.errorf("❌ Erreur lors de l'envoi de la commande: %s", e.getMessage());
        }

        return command;
    }

    /**
     * Récupère l'état d'une commande.
     */
    public Optional<Command> getCommand(String commandId) {
        return Optional.ofNullable(commands.get(commandId));
    }

    /**
     * Génère une réponse simulée selon le type de commande.
     */
    private String generateSuccessResponse(String commandType) {
        switch (commandType) {
            case "READ_VALUE":
                return "Valeur actuelle: " + (15 + Math.random() * 15) + "°C";
            case "RESET":
                return "Capteur réinitialisé avec succès";
            case "CALIBRATE":
                return "Calibration effectuée. Nouveau offset: " + (Math.random() * 2 - 1);
            case "SET_INTERVAL":
                return "Intervalle de mesure modifié: 60s";
            default:
                return "Commande exécutée";
        }
    }

    /**
     * MÉTHODE ALTERNATIVE (non utilisée dans le monolithe).
     * Exemple de ce que l'étudiant devrait implémenter : envoi asynchrone.
     * 
     * Cette méthode retourne immédiatement un ID de commande.
     * Le client doit ensuite interroger l'API pour connaître le résultat.
     */
    public String sendCommandAsync(String sensorId, String commandType) {
        String commandId = UUID.randomUUID().toString();
        Command command = new Command(commandId, sensorId, commandType);
        commands.put(commandId, command);
        
        // TODO pour l'étudiant : Implémenter un traitement asynchrone
        // (ex: CompletableFuture, Queue, Message Broker)
        
        return commandId;
    }
}
