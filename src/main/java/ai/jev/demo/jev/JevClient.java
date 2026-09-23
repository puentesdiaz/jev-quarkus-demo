package ai.jev.demo.jev;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Optional;

/**
 * Asks Jev a set of independent questions about one state in a single request.
 * Retries 429 (rate limited) and 529 (overloaded) with exponential backoff, as the docs recommend.
 */
@ApplicationScoped
public class JevClient {

    private static final Logger LOG = Logger.getLogger(JevClient.class);

    @Inject
    @RestClient
    JevApi api;

    @ConfigProperty(name = "jev.api-key")
    Optional<String> apiKey;

    @ConfigProperty(name = "jev.model", defaultValue = "jev-latest")
    String model;

    @ConfigProperty(name = "jev.max-retries", defaultValue = "3")
    int maxRetries;

    public SystemOneResponse ask(Object state, Map<String, Question> questions) {
        String key = apiKey.filter(k -> !k.isBlank())
                .orElseThrow(() -> new JevException("TYPESAFE_API_KEY is not set. Add it to .env or export it."));
        SystemOneRequest request = new SystemOneRequest(state, model, questions);
        long backoffMs = 500;
        for (int attempt = 0; ; attempt++) {
            try {
                return api.systemOne("Bearer " + key, request);
            } catch (WebApplicationException e) {
                int status = e.getResponse().getStatus();
                boolean retryable = status == 429 || status == 529;
                if (!retryable || attempt >= maxRetries) {
                    String body = readBody(e);
                    throw new JevException("Jev returned HTTP " + status + ": " + body, status, e);
                }
                LOG.warnf("Jev returned %d, retrying in %d ms", status, backoffMs);
                sleep(backoffMs);
                backoffMs *= 2;
            } catch (ProcessingException e) {
                throw new JevException("Could not reach Jev: " + e.getMessage(), 0, e);
            }
        }
    }

    private static String readBody(WebApplicationException e) {
        try {
            return e.getResponse().readEntity(String.class);
        } catch (RuntimeException ignored) {
            return "(no body)";
        }
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new JevException("Interrupted while retrying Jev request");
        }
    }
}
