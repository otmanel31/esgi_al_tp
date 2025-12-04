package fr.esgi.timeseries.service;

import fr.esgi.timeseries.model.SensorData;
import fr.esgi.timeseries.repository.TimeSeriesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TimeSeriesService {
    private static final Logger LOG = Logger.getLogger(TimeSeriesService.class);

    @Inject
    TimeSeriesRepository repository;

    public void saveSensorData(SensorData data) {
        repository.save(data);
        LOG.infof("✅ Donnée sauvegardée: %s - %.2f %s", 
                  data.getSensorId(), data.getValue(), data.getUnit());
    }

    public List<SensorData> getHistory(String sensorId, int limit) {
        return repository.findBySensorIdWithLimit(sensorId, limit);
    }

    public Optional<SensorData> getLatest(String sensorId) {
        return repository.findLatestBySensorId(sensorId);
    }
}
