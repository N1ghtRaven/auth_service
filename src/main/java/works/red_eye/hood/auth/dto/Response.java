package works.red_eye.hood.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Response {

    private boolean result;
    private String error;
    private Object data;
    private String path;

    public static Response ok() {
        return new Response(true, null, null, null);
    }

    public static Response ok(Object data) {
        return new Response(true, null, data, null);
    }

    public static Response error(String error) {
        return new Response(false, error, null, null);
    }

    public static Response error(String error, String path) {
        return new Response(false, error, null, path);
    }

}