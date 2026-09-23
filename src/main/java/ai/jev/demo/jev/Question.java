package ai.jev.demo.jev;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A typed System One question. {@code instructions} and {@code criteria} may be strings,
 * objects or arrays, so they are kept as plain {@link Object}s and serialized as-is.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Question(String type, Object instructions, Object criteria) {

    /** Yes/no question: the answer is the probability of "yes". */
    public static Question noul(String instructions) {
        return new Question("noul", instructions, null);
    }

    public static Question noul(String instructions, String whenTrue, String whenFalse) {
        return new Question("noul", instructions, Map.of("true", whenTrue, "false", whenFalse));
    }

    /** Picks one option. Pass options in order; values describe each option. */
    public static Question choice(String instructions, Map<String, String> options) {
        return new Question("choice", instructions, new LinkedHashMap<>(options));
    }

    /** Rates along ordered levels (level 0 is the first description). */
    public static Question score(String instructions, List<String> levels) {
        return new Question("score", instructions, levels);
    }
}
