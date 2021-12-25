package works.red_eye.hood.auth.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import works.red_eye.hood.auth.dto.Response;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleContentNotFound(NotFoundException e) {
        return getHandlerResponse(e, NOT_FOUND);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<Response> handleContentJwt(JwtAuthenticationException e) {
        return getHandlerResponse(e, BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Response> handleContentUnauthorized(UnauthorizedException e) {
        return getHandlerResponse(e, UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Response> handleContentForbidden(ForbiddenException e) {
        return getHandlerResponse(e, FORBIDDEN);
    }

    private ResponseEntity<Response> getHandlerResponse(AbstractException e, HttpStatus status) {
        return new ResponseEntity<>(Response.error(e.getDescription()), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(Response.error(String.valueOf(errors)), BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentTypeMismatchException e) {
        String name = e.getName();
        String type = null;
        if (e.getRequiredType() != null) {
            type = e.getRequiredType().getSimpleName();
        }
        Object value = e.getValue();
        String message = String.format("'%s' should be a valid '%s' and '%s' isn't", name, type, value);
        return new ResponseEntity<>(Response.error(message), BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response> handleInternalServerError(HttpServletRequest request, RuntimeException e) {
        String error = "500: " + e.getMessage().replace("\"", "'");
        String path = request.getRequestURI();
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ResponseEntity<>(Response.error(error, path), INTERNAL_SERVER_ERROR);
    }

}