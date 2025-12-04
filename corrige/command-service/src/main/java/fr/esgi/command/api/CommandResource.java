package fr.esgi.command.api;

import fr.esgi.command.service.CommandService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/api/commands")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommandResource {

    @Inject
    CommandService service;

    @POST
    public Response sendCommand(CommandRequest request) {
        String commandId = service.sendCommandAsync(request.sensorId, request.command);
        return Response.status(202)
                .entity(Map.of("commandId", commandId, "status", "PENDING"))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getStatus(@PathParam("id") String id) {
        return service.getCommand(id)
                .map(cmd -> Response.ok(cmd).build())
                .orElse(Response.status(404).build());
    }

    public static class CommandRequest {
        public String sensorId;
        public String command;
    }
}
