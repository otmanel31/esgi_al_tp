package fr.esgi.ingestion.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.time.LocalDateTime;

@RegisterRestClient(configKey = "timeseries")
@Path("/api/data")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TimeSeriesClient {
    
    @POST
    void save(SensorDataDTO data);
    
    class SensorDataDTO {
        public String sensorId;
        public LocalDateTime timestamp;
        public double value;
        public String unit;
    }
}
