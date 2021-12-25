package works.red_eye.hood.auth.exception;

public class BadRequestException extends AbstractException {
    public BadRequestException(String description) {
        super(description);
    }
}