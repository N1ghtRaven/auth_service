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

@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<Response> token(@RequestParam String username, @RequestParam String password)
            throws ForbiddenException, NotFoundException {

        return tokenService.issueTokens(username, password);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Response> refresh(@RequestParam("refresh_token") String refreshToken,
                                            @RequestParam(value = "fingerprint", required = false) String fgp,
                                            @CookieValue(value = "__Secure-Fgp", required = false) String fgpCookie)
            throws ForbiddenException, NotFoundException, JwtAuthenticationException {

        String fingerprint = getFingerprint(fgp, fgpCookie);
        if (fingerprint != null)
            return tokenService.refreshTokens(refreshToken, fingerprint);
        else
            return ResponseEntity.ok(Response.error("Not present fingerprint"));
    }

    @PostMapping("/revoke")
    public ResponseEntity<Response> revoke(@RequestParam("refresh_token") String token,
                                           @RequestParam(value = "fingerprint", required = false) String fgp,
                                           @CookieValue(value = "__Secure-Fgp", required = false) String fgpCookie)
            throws JwtAuthenticationException {

        String fingerprint = getFingerprint(fgp, fgpCookie);
        if (fingerprint != null)
            return tokenService.revokeFingerprint(token, fingerprint);
        else
            return ResponseEntity.ok(Response.error("Not present fingerprint"));
    }

    private String getFingerprint(String param, String cookie) {
        return cookie != null ? cookie : param;
    }

}