package fr.esgi.iot.service;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Simulateur de broker de messages (ex: MQTT, RabbitMQ).
 * Génère des données de capteurs toutes les 10 secondes.
 * 
 * PROBLÈME : Cette classe mélange la simulation du broker avec le traitement métier.
 * En production, ceci serait un vrai listener de messages.
 */
@ApplicationScoped
public class MessageBrokerSimulator {

    private static final Logger LOG = Logger.getLogger(MessageBrokerSimulator.class);

    @Inject
    SensorDataService sensorDataService;

    private final Random random = new Random();
    private final String[] sensorIds = {"TEMP-001", "TEMP-002", "HUM-001", "PRESS-001"};

    /**
     * Simule la réception de messages du broker toutes les 10 secondes.
     * En réalité, ceci serait un @Incoming d'un vrai broker.
     */
    @Scheduled(every = "10s", delay = 1)
    public void simulateIncomingMessages() {
        String sensorId = sensorIds[random.nextInt(sensorIds.length)];
        double value = generateRandomValue(sensorId);
        String unit = getUnitForSensor(sensorId);

        LOG.infof("📨 Message reçu du broker - Capteur: %s, Valeur: %.2f %s", sensorId, value, unit);

        // Appel direct au service métier (couplage fort)
        sensorDataService.processSensorData(sensorId, value, unit, LocalDateTime.now());
    }

    private double generateRandomValue(String sensorId) {
        if (sensorId.startsWith("TEMP")) {
            return 15 + random.nextDouble() * 15; // 15-30°C
        } else if (sensorId.startsWith("HUM")) {
            return 40 + random.nextDouble() * 40; // 40-80%
        } else if (sensorId.startsWith("PRESS")) {
            return 980 + random.nextDouble() * 60; // 980-1040 hPa
        }
        return 0.0;
    }

    private String getUnitForSensor(String sensorId) {
        if (sensorId.startsWith("TEMP")) return "°C";
        if (sensorId.startsWith("HUM")) return "%";
        if (sensorId.startsWith("PRESS")) return "hPa";
        return "";
    }
}
