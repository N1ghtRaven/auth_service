package works.red_eye.hood.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "BaseResponse", title = "BaseResponse", description = "Базовый ответ")
public class Response<T> {

    @Schema(title = "AuthResponse", description = "Ответ на запрос создания токенов")
    public static class Auth extends Response<AuthInfo> {}

    @Schema(title = "RefreshResponse", description = "Ответ на запрос обновления токена")
    public static class Refresh extends Response<RefreshInfo> {}

    @Schema(description = "Результат запроса")
    private boolean result;

    @Schema(description = "Описание ошибки")
    private String error;

    @Schema(description = "Полезная нагрузка ответа")
    private T data;

    public Response(T data) {
        this(true, null, data);
    }

    public static Response<?> ok() {
        return new Response<>(true, null, null);
    }

    public static Response<?> error(String error) {
        return new Response<>(false, error, null);
    }

}