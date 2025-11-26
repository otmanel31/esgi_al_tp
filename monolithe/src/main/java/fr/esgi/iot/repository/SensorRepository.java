package fr.esgi.iot.repository;

import fr.esgi.iot.model.Sensor;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository simulant une base de données relationnelle pour les capteurs.
 * PROBLÈME : Cette classe mélange la logique de stockage avec la logique métier.
 */
@ApplicationScoped
public class SensorRepository {

    // Simulation d'une table SQL en mémoire
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();

    public SensorRepository() {
        // Initialisation avec des données de test
        initializeTestData();
    }

    private void initializeTestData() {
        sensors.put("TEMP-001", new Sensor("TEMP-001", "Capteur Température Bureau", "TEMPERATURE", "Bureau A-203"));
        sensors.put("TEMP-002", new Sensor("TEMP-002", "Capteur Température Entrepôt", "TEMPERATURE", "Entrepôt Zone 1"));
        sensors.put("HUM-001", new Sensor("HUM-001", "Capteur Humidité Serre", "HUMIDITY", "Serre B"));
        sensors.put("PRESS-001", new Sensor("PRESS-001", "Capteur Pression Atelier", "PRESSURE", "Atelier C-12"));
    }

    public List<Sensor> findAll() {
        return new ArrayList<>(sensors.values());
    }

    public Optional<Sensor> findById(String id) {
        return Optional.ofNullable(sensors.get(id));
    }

    public Sensor save(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        return sensor;
    }

    public void updateLastCommunication(String sensorId, LocalDateTime timestamp) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor != null) {
            sensor.setLastCommunication(timestamp);
        }
    }

    public void delete(String id) {
        sensors.remove(id);
    }

    public boolean exists(String id) {
        return sensors.containsKey(id);
    }
}
