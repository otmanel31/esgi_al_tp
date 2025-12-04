package fr.esgi.command.model;

import java.time.LocalDateTime;

public class Command {
    private String id;
    private String sensorId;
    private String command;
    private LocalDateTime sentAt;
    private String status;
    private String response;

    public Command() {
    }

    public Command(String id, String sensorId, String command) {
        this.id = id;
        this.sensorId = sensorId;
        this.command = command;
        this.sentAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
}
