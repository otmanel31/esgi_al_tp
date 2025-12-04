package fr.esgi.timeseries.model;

import java.time.LocalDateTime;

public class SensorData {
    private String sensorId;
    private LocalDateTime timestamp;
    private double value;
    private String unit;

    public SensorData() {
    }

    public SensorData(String sensorId, LocalDateTime timestamp, double value, String unit) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.value = value;
        this.unit = unit;
    }

    // Getters & Setters
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
