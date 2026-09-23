package ai.jev.demo.triage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TriageRequest(
        @NotBlank @Size(max = 10_000) String message) {
}
