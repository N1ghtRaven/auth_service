package works.red_eye.hood.auth.service;

import org.springframework.http.ResponseEntity;
import works.red_eye.hood.auth.dto.AuthInfo;
import works.red_eye.hood.auth.dto.RefreshInfo;
import works.red_eye.hood.auth.dto.Response;
import works.red_eye.hood.auth.exception.ForbiddenException;
import works.red_eye.hood.auth.exception.JwtAuthenticationException;
import works.red_eye.hood.auth.exception.NotFoundException;
import works.red_eye.hood.auth.exception.UnauthorizedException;

public interface TokenService {
    ResponseEntity<Response<AuthInfo>> issueTokens(String username, String password) throws NotFoundException, ForbiddenException;
    ResponseEntity<Response<RefreshInfo>> refreshTokens(String refreshToken, String fingerprint) throws JwtAuthenticationException, ForbiddenException, NotFoundException, UnauthorizedException;
    ResponseEntity<Response<?>> revokeFingerprint(String token, String fingerprint) throws JwtAuthenticationException, UnauthorizedException;
}