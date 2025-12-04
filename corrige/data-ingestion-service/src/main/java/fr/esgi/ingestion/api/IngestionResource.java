package fr.esgi.ingestion.api;

import fr.esgi.ingestion.model.SensorDataMessage;
import fr.esgi.ingestion.service.IngestionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/ingest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IngestionResource {

    @Inject
    IngestionService service;

    @POST
    public Response ingest(SensorDataMessage message) {
        service.ingest(message);
        return Response.ok().build();
    }
}
