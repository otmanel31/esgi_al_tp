package fr.esgi.sensor.service;

import fr.esgi.sensor.model.Sensor;
import fr.esgi.sensor.repository.SensorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SensorService {
    private static final Logger LOG = Logger.getLogger(SensorService.class);

    @Inject
    SensorRepository repository;

    public List<Sensor> getAllSensors() {
        return repository.findAll();
    }

    public Optional<Sensor> getSensorById(String id) {
        return repository.findById(id);
    }

    public Sensor createSensor(Sensor sensor) {
        LOG.infof("✅ Création capteur: %s", sensor.getId());
        return repository.save(sensor);
    }

    public Optional<Sensor> updateSensor(String id, Sensor sensor) {
        if (!repository.exists(id)) {
            return Optional.empty();
        }
        sensor.setId(id);
        LOG.infof("✅ MAJ capteur: %s", id);
        return Optional.of(repository.save(sensor));
    }

    public void updateLastCommunication(String id) {
        repository.updateLastCommunication(id, LocalDateTime.now());
        LOG.infof("✅ MAJ communication: %s", id);
    }

    public boolean deleteSensor(String id) {
        if (!repository.exists(id)) {
            return false;
        }
        repository.delete(id);
        LOG.infof("✅ Suppression capteur: %s", id);
        return true;
    }
}
