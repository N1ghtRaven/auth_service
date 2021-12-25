package works.red_eye.hood.auth.exception;

public class NotFoundException extends AbstractException {

    public NotFoundException(String name) {
        super(name);
    }

    public NotFoundException(String name, Long id) {
        super(String.format("%s id=%d not found", name, id));
    }

    public NotFoundException(String name, String key) {
        super(String.format("%s key=%s not found", name, key));
    }

}