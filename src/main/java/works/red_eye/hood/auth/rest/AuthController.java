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
import works.red_eye.hood.auth.exception.UnauthorizedException;
import works.red_eye.hood.auth.service.TokenService;

import java.security.NoSuchAlgorithmException;

@RestController
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestParam String username, @RequestParam String password)
            throws ForbiddenException, UnauthorizedException, NotFoundException, NoSuchAlgorithmException {

        return tokenService.issueTokens(username, password);
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(@RequestParam(required = false) String fingerprint,
                                           @CookieValue(value = "__Secure-Fgp", required = false) String fgpCookie)
            throws ForbiddenException, NotFoundException, NoSuchAlgorithmException, JwtAuthenticationException {

        if (fgpCookie != null)
            return tokenService.revokeFingerprint(fgpCookie);
        else if (fingerprint != null)
            return tokenService.revokeFingerprint(fingerprint);
        else
            return ResponseEntity.ok(Response.error("Not present fingerprint"));
    }

}