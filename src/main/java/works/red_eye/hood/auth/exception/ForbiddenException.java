package works.red_eye.hood.auth.exception;

public class ForbiddenException extends AbstractException {
    public ForbiddenException(String name) {
        super(String.format("User %s disabled", name));
    }
}