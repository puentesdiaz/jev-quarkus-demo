package ai.jev.demo.jev;

public class JevException extends RuntimeException {

    private final int status;

    public JevException(String message) {
        this(message, 0, null);
    }

    public JevException(String message, int status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /** HTTP status returned by Jev, or 0 when the call never got a response. */
    public int status() {
        return status;
    }
}
