package works.red_eye.hood.auth.exception;

public class UnauthorizedException extends AbstractException {
    public UnauthorizedException() {
        super("Fingerprint required");
    }
}