package ai.jev.demo.jev;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * One answer. Which fields are set depends on {@code type}:
 * noul → {@code noul}; choice → {@code choice}, {@code probabilities}, {@code confidence};
 * score → {@code score}, {@code legend}, {@code probabilities}, {@code confidence}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Answer(
        String type,
        Double noul,
        String choice,
        Double score,
        Map<String, String> legend,
        Map<String, Double> probabilities,
        Double confidence) {
}
