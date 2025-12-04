package fr.esgi.timeseries.api;

import fr.esgi.timeseries.model.SensorData;
import fr.esgi.timeseries.service.TimeSeriesService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/data")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimeSeriesResource {

    @Inject
    TimeSeriesService service;

    @POST
    public Response save(SensorData data) {
        service.saveSensorData(data);
        return Response.status(201).build();
    }

    @GET
    @Path("/{sensorId}")
    public List<SensorData> getHistory(
            @PathParam("sensorId") String sensorId,
            @QueryParam("limit") @DefaultValue("50") int limit) {
        return service.getHistory(sensorId, limit);
    }

    @GET
    @Path("/{sensorId}/latest")
    public Response getLatest(@PathParam("sensorId") String sensorId) {
        return service.getLatest(sensorId)
                .map(data -> Response.ok(data).build())
                .orElse(Response.status(404).build());
    }
}
