package fr.esgi.timeseries.repository;

import fr.esgi.timeseries.model.SensorData;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class TimeSeriesRepository {
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

    public Optional<SensorData> findLatestBySensorId(String sensorId) {
        return timeSeriesData.getOrDefault(sensorId, Collections.emptyList())
                .stream()
                .max(Comparator.comparing(SensorData::getTimestamp));
    }
}
