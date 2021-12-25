package works.red_eye.hood.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class AbstractException extends Exception {
    private final String description;
}