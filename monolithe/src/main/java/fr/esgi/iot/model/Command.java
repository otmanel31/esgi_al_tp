package fr.esgi.iot.model;

import java.time.LocalDateTime;

/**
 * Modèle représentant une commande envoyée à un capteur.
 */
public class Command {
    private String id;
    private String sensorId;
    private String command; // READ_VALUE, RESET, CALIBRATE, etc.
    private LocalDateTime sentAt;
    private String status; // PENDING, SUCCESS, FAILED, TIMEOUT
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

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "Command{" +
                "id='" + id + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", command='" + command + '\'' +
                ", sentAt=" + sentAt +
                ", status='" + status + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
