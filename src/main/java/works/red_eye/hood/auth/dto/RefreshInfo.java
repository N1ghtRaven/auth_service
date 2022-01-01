package works.red_eye.hood.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(title = "Refresh", description = "Сущность ответа на обновление токена")
public class RefreshInfo {

    @JsonProperty("access_token")
    @Schema(description = "Токен доступа")
    private String accessToken;

    @JsonProperty("expires_in")
    @Schema(description = "Время жизни токена доступа")
    private Integer expiresIn;

    @JsonProperty("token_type")
    @Schema(description = "Тип токена")
    private final String tokenType = "Bearer";
}