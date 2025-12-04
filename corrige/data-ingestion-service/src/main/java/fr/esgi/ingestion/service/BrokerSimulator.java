package fr.esgi.ingestion.service;

import fr.esgi.ingestion.model.SensorDataMessage;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.Random;

@ApplicationScoped
public class BrokerSimulator {
    private static final Logger LOG = Logger.getLogger(BrokerSimulator.class);

    @Inject
    IngestionService ingestionService;

    private final Random random = new Random();
    private final String[] sensorIds = {"TEMP-001", "TEMP-002", "HUM-001", "PRESS-001"};

    @Scheduled(every = "10s", delay = 5)
    public void simulateMessages() {
        String sensorId = sensorIds[random.nextInt(sensorIds.length)];
        double value = generateValue(sensorId);
        String unit = getUnit(sensorId);

        LOG.infof("📨 Broker simule message: %s", sensorId);
        ingestionService.ingest(new SensorDataMessage(sensorId, value, unit));
    }

    private double generateValue(String sensorId) {
        if (sensorId.startsWith("TEMP")) return 15 + random.nextDouble() * 15;
        if (sensorId.startsWith("HUM")) return 40 + random.nextDouble() * 40;
        return 980 + random.nextDouble() * 60;
    }

    private String getUnit(String sensorId) {
        if (sensorId.startsWith("TEMP")) return "°C";
        if (sensorId.startsWith("HUM")) return "%";
        return "hPa";
    }
}
