package ai.jev.demo.triage;

import ai.jev.demo.jev.JevClient;
import ai.jev.demo.jev.SystemOneResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;

@ApplicationScoped
public class TriageService {

    @Inject
    JevClient jev;

    @Inject
    TriagePolicy policy;

    public TriageResult triage(String message) {
        Map<String, Object> state = Map.of("ticket", Map.of("message", message));

        long start = System.nanoTime();
        SystemOneResponse response = jev.ask(state, TriageQuestions.all());
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        return policy.decide(response, elapsedMs);
    }
}
