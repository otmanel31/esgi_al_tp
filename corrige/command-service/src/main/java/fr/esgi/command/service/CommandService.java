package fr.esgi.command.service;

import fr.esgi.command.model.Command;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class CommandService {
    private static final Logger LOG = Logger.getLogger(CommandService.class);

    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    public String sendCommandAsync(String sensorId, String commandType) {
        String commandId = UUID.randomUUID().toString();
        Command command = new Command(commandId, sensorId, commandType);
        commands.put(sensorId, command);

        LOG.infof("📤 Commande créée (ASYNC): %s", commandId);

        CompletableFuture.runAsync(() -> processCommand(command));

        return commandId;
    }

    private void processCommand(Command command) {
        try {
            LOG.infof("⏳ Traitement commande: %s", command.getId());
            Thread.sleep(2000 + (long) (Math.random() * 3000));

            boolean success = Math.random() > 0.2;
            if (success) {
                command.setStatus("SUCCESS");
                command.setResponse(generateResponse(command.getCommand()));
                LOG.infof("✅ Commande réussie: %s", command.getId());
            } else {
                command.setStatus("FAILED");
                command.setResponse("Timeout capteur");
                LOG.warnf("❌ Commande échouée: %s", command.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            command.setStatus("FAILED");
            command.setResponse("Erreur: " + e.getMessage());
        }
    }

    public Optional<Command> getCommand(String commandId) {
        return Optional.ofNullable(commands.get(commandId));
    }

    private String generateResponse(String commandType) {
        return switch (commandType) {
            case "READ_VALUE" -> "Valeur: " + (15 + Math.random() * 15) + "°C";
            case "RESET" -> "Capteur réinitialisé";
            case "CALIBRATE" -> "Calibration OK";
            default -> "Commande exécutée";
        };
    }
}
