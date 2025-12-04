package fr.esgi.ingestion.service;

import fr.esgi.ingestion.client.SensorManagementClient;
import fr.esgi.ingestion.client.TimeSeriesClient;
import fr.esgi.ingestion.model.SensorDataMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class IngestionService {
    private static final Logger LOG = Logger.getLogger(IngestionService.class);

    @Inject
    @RestClient
    SensorManagementClient sensorClient;

    @Inject
    @RestClient
    TimeSeriesClient timeSeriesClient;

    public void ingest(SensorDataMessage message) {
        LOG.infof("📥 Ingestion donnée: %s = %.2f %s", 
                  message.getSensorId(), message.getValue(), message.getUnit());

        try {
            // 1. Vérifier capteur existe
            var sensor = sensorClient.getSensorById(message.getSensorId());
            if (sensor == null || !"ACTIVE".equals(sensor.status)) {
                LOG.warnf("⚠️ Capteur invalide: %s", message.getSensorId());
                return;
            }

            // 2. Valider données
            if (!isValueValid(sensor.type, message.getValue())) {
                LOG.warnf("⚠️ Valeur invalide: %.2f pour type %s", 
                          message.getValue(), sensor.type);
                return;
            }

            // 3. Stocker dans timeseries
            var data = new TimeSeriesClient.SensorDataDTO();
            data.sensorId = message.getSensorId();
            data.timestamp = message.getTimestamp();
            data.value = message.getValue();
            data.unit = message.getUnit();
            timeSeriesClient.save(data);

            // 4. MAJ dernière communication
            sensorClient.updateLastCommunication(message.getSensorId());

            LOG.infof("✅ Donnée ingérée: %s", message.getSensorId());

        } catch (Exception e) {
            LOG.errorf("❌ Erreur ingestion: %s", e.getMessage());
        }
    }

    private boolean isValueValid(String type, double value) {
        return switch (type) {
            case "TEMPERATURE" -> value >= -50 && value <= 100;
            case "HUMIDITY" -> value >= 0 && value <= 100;
            case "PRESSURE" -> value >= 800 && value <= 1200;
            default -> true;
        };
    }
}
