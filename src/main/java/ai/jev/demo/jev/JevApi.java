package ai.jev.demo.jev;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/** Low-level REST client for the TypeSafe System One endpoint. Use {@link JevClient} instead. */
@RegisterRestClient(configKey = "jev")
@Path("/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface JevApi {

    @POST
    @Path("/systemone")
    SystemOneResponse systemOne(@HeaderParam("Authorization") String authorization, SystemOneRequest request);
}
