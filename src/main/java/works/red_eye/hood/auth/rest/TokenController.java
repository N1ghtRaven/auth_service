package works.red_eye.hood.auth.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import works.red_eye.hood.auth.dto.AuthInfo;
import works.red_eye.hood.auth.dto.RefreshInfo;
import works.red_eye.hood.auth.dto.Response;
import works.red_eye.hood.auth.exception.ForbiddenException;
import works.red_eye.hood.auth.exception.JwtAuthenticationException;
import works.red_eye.hood.auth.exception.NotFoundException;
import works.red_eye.hood.auth.exception.UnauthorizedException;
import works.red_eye.hood.auth.service.TokenService;

import static works.red_eye.hood.auth.dto.Response.Auth;
import static works.red_eye.hood.auth.dto.Response.Refresh;

@RestController
@Tag(name="Токены", description = "Работа с токенами")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    @Operation(summary = "Получить токен")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токены были созданы", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Auth.class))}),
            @ApiResponse(responseCode = "403", description = "Пользователь отключен", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Неверный логин или пароль", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Response<AuthInfo>> token(@RequestParam @Parameter(description = "Имя пользователя") String username,
                                                    @RequestParam @Parameter(description = "Пароль") String password)
            throws ForbiddenException, NotFoundException {

        return tokenService.issueTokens(username, password);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновить токен")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен был обновлен", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Refresh.class))}),
            @ApiResponse(responseCode = "400", description = "Неправильный токен", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Требуется отпечаток", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Пользователь отключен", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Response<RefreshInfo>> refresh(@RequestParam("refresh_token") @Parameter(description = "Токен обновления") String refreshToken,
                                                         @RequestParam(value = "fingerprint", required = false) @Parameter(description = "Отпечаток") String fgp,
                                                         @CookieValue(value = "__Secure-Fgp", required = false) @Parameter(description = "Отпечаток") String fgpCookie)
            throws ForbiddenException, NotFoundException, JwtAuthenticationException, UnauthorizedException {

        return tokenService.refreshTokens(refreshToken, getFingerprint(fgp, fgpCookie));
    }

    @PostMapping("/revoke")
    @Operation(summary = "Отозвать токен")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен был отозван", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "Неправильный токен", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Требуется отпечаток", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Response<?>> revoke(@RequestParam("refresh_token") @Parameter(description = "Токен обновления") String token,
                                              @RequestParam(value = "fingerprint", required = false) @Parameter(description = "Отпечаток") String fgp,
                                              @CookieValue(value = "__Secure-Fgp", required = false) @Parameter(description = "Отпечаток") String fgpCookie)
            throws JwtAuthenticationException, UnauthorizedException {

        return tokenService.revokeFingerprint(token, getFingerprint(fgp, fgpCookie));
    }

    private String getFingerprint(String param, String cookie) {
        return cookie != null ? cookie : param;
    }

}