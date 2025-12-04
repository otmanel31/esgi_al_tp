package fr.esgi.sensor.api;

import fr.esgi.sensor.model.Sensor;
import fr.esgi.sensor.service.SensorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @Inject
    SensorService service;

    @GET
    public List<Sensor> getAll() {
        return service.getAllSensors();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") String id) {
        return service.getSensorById(id)
                .map(sensor -> Response.ok(sensor).build())
                .orElse(Response.status(404).build());
    }

    @POST
    public Response create(Sensor sensor) {
        Sensor created = service.createSensor(sensor);
        return Response.status(201).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") String id, Sensor sensor) {
        return service.updateSensor(id, sensor)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(404).build());
    }

    @PUT
    @Path("/{id}/communication")
    public Response updateCommunication(@PathParam("id") String id) {
        service.updateLastCommunication(id);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        return service.deleteSensor(id) ? Response.noContent().build() : Response.status(404).build();
    }
}
