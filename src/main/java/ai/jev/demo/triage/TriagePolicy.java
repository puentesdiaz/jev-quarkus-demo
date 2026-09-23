package ai.jev.demo.triage;

import ai.jev.demo.jev.Answer;
import ai.jev.demo.jev.SystemOneResponse;
import ai.jev.demo.triage.TriageResult.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static ai.jev.demo.triage.TriageQuestions.*;

/** Explicit business rules over Jev's judgments. No model calls happen here. */
@ApplicationScoped
public class TriagePolicy {

    @ConfigProperty(name = "triage.escalate-noul-threshold", defaultValue = "0.8")
    double noulThreshold;

    @ConfigProperty(name = "triage.escalate-frustration-score", defaultValue = "1.5")
    double frustrationThreshold;

    @ConfigProperty(name = "triage.min-route-confidence", defaultValue = "0.6")
    double minRouteConfidence;

    public TriageResult decide(SystemOneResponse response, long elapsedMs) {
        Answer department = response.answer(DEPARTMENT);
        double urgent = response.answer(IS_URGENT).noul();
        double wantsHuman = response.answer(WANTS_HUMAN).noul();
        double frustration = response.answer(FRUSTRATION).score();
        double spam = response.answer(IS_SPAM).noul();

        List<String> reasons = new ArrayList<>();

        if (spam >= noulThreshold) {
            reasons.add(String.format(Locale.ROOT, "Probably spam (p=%.2f)", spam));
            return new TriageResult("spam", Priority.LOW, false, reasons,
                    response.answers(), response.model(), response.usage(), elapsedMs);
        }

        String queue;
        if (department.confidence() >= minRouteConfidence) {
            queue = department.choice();
            reasons.add(String.format(Locale.ROOT, "Routed to %s (confidence %.2f)", queue, department.confidence()));
        } else {
            queue = "manual-review";
            reasons.add(String.format(Locale.ROOT, "Team unclear: best guess %s with confidence %.2f < %.2f",
                    department.choice(), department.confidence(), minRouteConfidence));
        }

        boolean isUrgent = urgent >= noulThreshold;
        boolean isAngry = frustration >= frustrationThreshold;
        boolean asksForHuman = wantsHuman >= noulThreshold;

        if (isUrgent) reasons.add(String.format(Locale.ROOT, "Urgent (p=%.2f)", urgent));
        if (isAngry) reasons.add(String.format(Locale.ROOT, "High frustration (score %.2f of 2)", frustration));
        if (asksForHuman) reasons.add(String.format(Locale.ROOT, "Asks for a human (p=%.2f)", wantsHuman));

        Priority priority = isUrgent || isAngry ? Priority.HIGH
                : frustration >= 0.75 ? Priority.NORMAL
                : Priority.LOW;
        boolean escalate = asksForHuman || (isUrgent && isAngry);

        return new TriageResult(queue, priority, escalate, reasons,
                response.answers(), response.model(), response.usage(), elapsedMs);
    }
}
