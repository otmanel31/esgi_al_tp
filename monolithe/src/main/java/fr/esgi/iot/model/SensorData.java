package fr.esgi.iot.model;

import java.time.LocalDateTime;

/**
 * Modèle représentant une donnée de capteur.
 * Stocké dans la "base de données" time-series (simulée en mémoire).
 */
public class SensorData {
    private String sensorId;
    private LocalDateTime timestamp;
    private double value;
    private String unit; // °C, %, hPa, etc.

    public SensorData() {
    }

    public SensorData(String sensorId, LocalDateTime timestamp, double value, String unit) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.value = value;
        this.unit = unit;
    }

    // Getters et Setters
    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "sensorId='" + sensorId + '\'' +
                ", timestamp=" + timestamp +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                '}';
    }
}
