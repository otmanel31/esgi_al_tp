package fr.esgi.ingestion.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "sensor-management")
@Path("/api/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SensorManagementClient {
    
    @GET
    @Path("/{id}")
    SensorDTO getSensorById(@PathParam("id") String id);
    
    @PUT
    @Path("/{id}/communication")
    void updateLastCommunication(@PathParam("id") String id);
    
    class SensorDTO {
        public String id;
        public String name;
        public String type;
        public String status;
    }
}
