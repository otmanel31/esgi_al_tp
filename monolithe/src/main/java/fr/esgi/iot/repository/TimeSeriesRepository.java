package fr.esgi.iot.repository;

import fr.esgi.iot.model.SensorData;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repository simulant une base de données time-series pour l'historique des données capteurs.
 * PROBLÈME : Couplage fort avec SensorRepository via les requêtes croisées.
 */
@ApplicationScoped
public class TimeSeriesRepository {

    // Simulation d'une base time-series (Map<SensorId, List<Data>>)
    private final Map<String, List<SensorData>> timeSeriesData = new ConcurrentHashMap<>();

    public void save(SensorData data) {
        timeSeriesData
                .computeIfAbsent(data.getSensorId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(data);
    }

    public List<SensorData> findBySensorId(String sensorId) {
        return new ArrayList<>(timeSeriesData.getOrDefault(sensorId, Collections.emptyList()));
    }

    public List<SensorData> findBySensorIdWithLimit(String sensorId, int limit) {
        List<SensorData> data = timeSeriesData.getOrDefault(sensorId, Collections.emptyList());
        return data.stream()
                .sorted(Comparator.comparing(SensorData::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<SensorData> findAll() {
        return timeSeriesData.values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(SensorData::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public void deleteAllBySensorId(String sensorId) {
        timeSeriesData.remove(sensorId);
    }
}
