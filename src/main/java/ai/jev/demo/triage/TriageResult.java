package ai.jev.demo.triage;

import ai.jev.demo.jev.Answer;
import ai.jev.demo.jev.SystemOneResponse;

import java.util.List;
import java.util.Map;

/**
 * What the application does with the ticket (decided in code) plus the raw Jev judgments,
 * kept visible so thresholds can be tuned without re-running inference.
 */
public record TriageResult(
        String queue,
        Priority priority,
        boolean escalateToHuman,
        List<String> reasons,
        Map<String, Answer> judgments,
        String model,
        SystemOneResponse.Usage usage,
        long elapsedMs) {

    public enum Priority { LOW, NORMAL, HIGH }
}
