package fr.esgi.ingestion.model;

import java.time.LocalDateTime;

public class SensorDataMessage {
    private String sensorId;
    private double value;
    private String unit;
    private LocalDateTime timestamp;

    public SensorDataMessage() {
        this.timestamp = LocalDateTime.now();
    }

    public SensorDataMessage(String sensorId, double value, String unit) {
        this.sensorId = sensorId;
        this.value = value;
        this.unit = unit;
        this.timestamp = LocalDateTime.now();
    }

    // Getters & Setters
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
