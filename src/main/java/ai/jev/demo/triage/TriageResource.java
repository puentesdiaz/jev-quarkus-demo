package ai.jev.demo.triage;

import ai.jev.demo.jev.JevException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.List;
import java.util.Map;

@Path("/api/triage")
@Produces(MediaType.APPLICATION_JSON)
public class TriageResource {

    @Inject
    TriageService service;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public TriageResult triage(@Valid TriageRequest request) {
        return service.triage(request.message());
    }

    @GET
    @Path("/examples")
    public List<String> examples() {
        return List.of(
                "I've contacted you three times and I'm still waiting. Our checkout has been down since this morning and we're losing sales. Put me through to a manager NOW.",
                "Hi! Could you tell me how much it costs to add 20 more seats to our team plan?",
                "I was charged twice for my September invoice. Can you refund the duplicate?",
                "I can't log in after resetting my password, it says the link expired.",
                "Thanks for the update. Everything is working now.",
                "BEST CRYPTO SIGNALS!!! 500% returns guaranteed, click here to join our VIP group");
    }

    @ServerExceptionMapper
    public Response mapJevError(JevException e) {
        // 503 when Jev could not be reached or the key is missing; 502 when Jev answered with an error.
        int status = e.status() == 0 ? 503 : 502;
        return Response.status(status).type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", e.getMessage())).build();
    }
}
