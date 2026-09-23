package ai.jev.demo.jev;

import java.util.Map;

/** Body for {@code POST /v1/systemone}. */
public record SystemOneRequest(Object state, String model, Map<String, Question> questions) {
}
