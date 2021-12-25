package works.red_eye.hood.auth.service;

import org.springframework.http.ResponseEntity;
import works.red_eye.hood.auth.dto.Response;
import works.red_eye.hood.auth.exception.ForbiddenException;
import works.red_eye.hood.auth.exception.JwtAuthenticationException;
import works.red_eye.hood.auth.exception.NotFoundException;
import works.red_eye.hood.auth.exception.UnauthorizedException;

import java.security.NoSuchAlgorithmException;

public interface TokenService {
    ResponseEntity<Response> issueTokens(String username, String password) throws NotFoundException, ForbiddenException, UnauthorizedException;
    ResponseEntity<Response> refreshTokens(String refreshToken, String fingerprint) throws JwtAuthenticationException, ForbiddenException, NotFoundException;
    ResponseEntity<Response> revokeFingerprint(String token, String fingerprint) throws JwtAuthenticationException;
}