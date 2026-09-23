package ai.jev.demo.jev;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SystemOneResponse(String model, Map<String, Answer> answers, Usage usage) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(
            @JsonProperty("input_tokens") int inputTokens,
            @JsonProperty("output_tokens") int outputTokens) {
    }

    public Answer answer(String id) {
        Answer answer = answers == null ? null : answers.get(id);
        if (answer == null) {
            throw new JevException("Jev response is missing answer '" + id + "'");
        }
        return answer;
    }
}
