package ai.jev.demo.triage;

import ai.jev.demo.jev.Answer;
import ai.jev.demo.jev.JevClient;
import ai.jev.demo.jev.SystemOneResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** Tests the policy end to end with a mocked Jev, so no API key or network is needed. */
@QuarkusTest
class TriageResourceTest {

    @InjectMock
    JevClient jev;

    private static Answer noul(double p) {
        return new Answer("noul", p, null, null, null, null, null);
    }

    private static Answer choice(String option, double confidence) {
        return new Answer("choice", null, option, null, null, Map.of(option, 0.9), confidence);
    }

    private static Answer score(double value) {
        return new Answer("score", null, null, value, Map.of("0", "calm", "1", "upset", "2", "angry"),
                Map.of(), 0.9);
    }

    private void jevAnswers(Answer department, double urgent, double human, double frustration, double spam) {
        when(jev.ask(any(), any())).thenReturn(new SystemOneResponse("jev-test", Map.of(
                "department", department,
                "isUrgent", noul(urgent),
                "wantsHuman", noul(human),
                "frustration", score(frustration),
                "isSpam", noul(spam)),
                new SystemOneResponse.Usage(300, 40)));
    }

    @Test
    void angryUrgentCustomerIsEscalated() {
        jevAnswers(choice("technical", 0.9), 0.97, 0.95, 1.9, 0.01);

        given().contentType("application/json").body(Map.of("message", "Checkout is down!"))
                .when().post("/api/triage")
                .then().statusCode(200)
                .body("queue", equalTo("technical"))
                .body("priority", equalTo("HIGH"))
                .body("escalateToHuman", equalTo(true));
    }

    @Test
    void lowConfidenceRoutingGoesToManualReview() {
        jevAnswers(choice("billing", 0.3), 0.1, 0.05, 0.2, 0.02);

        given().contentType("application/json").body(Map.of("message", "hmm"))
                .when().post("/api/triage")
                .then().statusCode(200)
                .body("queue", equalTo("manual-review"))
                .body("priority", equalTo("LOW"))
                .body("reasons[0]", startsWith("Team unclear"));
    }

    @Test
    void spamIsShortCircuited() {
        jevAnswers(choice("other", 0.9), 0.0, 0.0, 0.0, 0.98);

        given().contentType("application/json").body(Map.of("message", "BUY CRYPTO"))
                .when().post("/api/triage")
                .then().statusCode(200)
                .body("queue", equalTo("spam"))
                .body("reasons", hasItem(startsWith("Probably spam")));
    }

    @Test
    void blankMessageIsRejected() {
        given().contentType("application/json").body(Map.of("message", " "))
                .when().post("/api/triage")
                .then().statusCode(400);
    }

    @Test
    void examplesAreListed() {
        given().when().get("/api/triage/examples")
                .then().statusCode(200)
                .body("size()", equalTo(6));
    }
}
