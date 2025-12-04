package fr.esgi.sensor.model;

import java.time.LocalDateTime;

public class Sensor {
    private String id;
    private String name;
    private String type;
    private String location;
    private LocalDateTime lastCommunication;
    private String status;

    public Sensor() {
    }

    public Sensor(String id, String name, String type, String location) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.lastCommunication = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getLastCommunication() { return lastCommunication; }
    public void setLastCommunication(LocalDateTime lastCommunication) { this.lastCommunication = lastCommunication; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
