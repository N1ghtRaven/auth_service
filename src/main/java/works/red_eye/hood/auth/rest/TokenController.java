package works.red_eye.hood.auth.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import works.red_eye.hood.auth.dto.Response;
import works.red_eye.hood.auth.exception.ForbiddenException;
import works.red_eye.hood.auth.exception.JwtAuthenticationException;
import works.red_eye.hood.auth.exception.NotFoundException;
import works.red_eye.hood.auth.service.TokenService;

import java.security.NoSuchAlgorithmException;

@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<Response> refresh(@RequestParam("refresh_token") String refreshToken,
                                            @RequestParam(required = false) String fingerprint,
                                            @CookieValue(value = "__Secure-Fgp", required = false) String fgpCookie)
            throws ForbiddenException, NotFoundException, NoSuchAlgorithmException, JwtAuthenticationException {

        if (fgpCookie != null)
            return tokenService.refreshTokens(refreshToken, fgpCookie);
        else if (fingerprint != null)
            return tokenService.refreshTokens(refreshToken, fingerprint);
        else
            return ResponseEntity.ok(Response.error("Not present fingerprint"));
    }

}