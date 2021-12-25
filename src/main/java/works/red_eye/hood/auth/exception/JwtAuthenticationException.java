package works.red_eye.hood.auth.exception;

public class JwtAuthenticationException extends AbstractException {
    public JwtAuthenticationException(String name) {
        super(name);
    }
}