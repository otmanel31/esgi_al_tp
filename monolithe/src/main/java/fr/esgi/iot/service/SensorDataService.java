package fr.esgi.iot.service;

import fr.esgi.iot.model.Sensor;
import fr.esgi.iot.model.SensorData;
import fr.esgi.iot.repository.SensorRepository;
import fr.esgi.iot.repository.TimeSeriesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service métier gérant les données des capteurs.
 * 
 * PROBLÈME : Ce service a trop de responsabilités :
 * - Traitement des données entrantes
 * - Mise à jour des capteurs
 * - Stockage dans la time-series
 * - Logique de validation
 * 
 * Violation du principe SRP (Single Responsibility Principle).
 */
@ApplicationScoped
public class SensorDataService {

    private static final Logger LOG = Logger.getLogger(SensorDataService.class);

    @Inject
    SensorRepository sensorRepository;

    @Inject
    TimeSeriesRepository timeSeriesRepository;

    /**
     * Traite les données reçues d'un capteur.
     * PROBLÈME : Méthode trop longue avec plusieurs responsabilités.
     */
    public void processSensorData(String sensorId, double value, String unit, LocalDateTime timestamp) {
        LOG.infof("🔄 Traitement de la donnée - Capteur: %s, Valeur: %.2f %s", sensorId, value, unit);

        // Vérification de l'existence du capteur
        Optional<Sensor> sensorOpt = sensorRepository.findById(sensorId);
        if (sensorOpt.isEmpty()) {
            LOG.warnf("⚠️ Capteur inconnu: %s", sensorId);
            return;
        }

        Sensor sensor = sensorOpt.get();

        // Validation de la valeur (logique métier couplée)
        if (!isValueValid(sensor.getType(), value)) {
            LOG.warnf("⚠️ Valeur invalide pour le capteur %s: %.2f", sensorId, value);
            sensor.setStatus("ERROR");
            sensorRepository.save(sensor);
            return;
        }

        // Mise à jour de la dernière communication (responsabilité du repository?)
        sensor.setLastCommunication(timestamp);
        sensor.setStatus("ACTIVE");
        sensorRepository.save(sensor);

        // Sauvegarde dans la time-series
        SensorData data = new SensorData(sensorId, timestamp, value, unit);
        timeSeriesRepository.save(data);

        LOG.infof("✅ Donnée traitée et sauvegardée pour le capteur: %s", sensorId);
    }

    /**
     * Validation des valeurs selon le type de capteur.
     * PROBLÈME : Logique de validation mélangée avec le service.
     */
    private boolean isValueValid(String sensorType, double value) {
        switch (sensorType) {
            case "TEMPERATURE":
                return value >= -50 && value <= 100;
            case "HUMIDITY":
                return value >= 0 && value <= 100;
            case "PRESSURE":
                return value >= 800 && value <= 1200;
            default:
                return true;
        }
    }

    public List<SensorData> getSensorHistory(String sensorId, int limit) {
        return timeSeriesRepository.findBySensorIdWithLimit(sensorId, limit);
    }

    public List<SensorData> getAllSensorData() {
        return timeSeriesRepository.findAll();
    }
}
