package works.red_eye.hood.auth.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import works.red_eye.hood.auth.dto.AuthInfo;
import works.red_eye.hood.auth.dto.RefreshInfo;
import works.red_eye.hood.auth.dto.Response;
import works.red_eye.hood.auth.entity.User;
import works.red_eye.hood.auth.exception.ForbiddenException;
import works.red_eye.hood.auth.exception.JwtAuthenticationException;
import works.red_eye.hood.auth.exception.NotFoundException;
import works.red_eye.hood.auth.exception.UnauthorizedException;
import works.red_eye.hood.auth.service.FingerprintService;
import works.red_eye.hood.auth.service.RsaRegeneratorService;
import works.red_eye.hood.auth.service.TokenService;
import works.red_eye.hood.auth.service.UserService;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    @AllArgsConstructor
    @Getter
    private enum TokenType {
        ACCESS_TOKEN("access"),
        REFRESH_TOKEN("refresh");

        private final String name;
    }

    private final FingerprintService fingerprintService;
    private final UserService userService;
    private final Key signingKey;

    @Value("${jwt.access_token.expiration}")
    private Integer accessExpire;

    @Value("${jwt.refresh_token.expiration}")
    private Integer refreshExpire;

    public TokenServiceImpl(FingerprintService fingerprintService,
                            UserService userService,
                            RsaRegeneratorService rsaRegeneratorService) {

        this.fingerprintService = fingerprintService;
        this.userService = userService;
        this.signingKey = rsaRegeneratorService.getSigningKeyPair().getPrivate();
    }

    @Override
    public ResponseEntity<Response<AuthInfo>> issueTokens(String username, String password)
            throws NotFoundException, ForbiddenException {

        User user = userService.getUser(username);
        if (!userService.isCorrectPassword(user, password))
            throw new NotFoundException(user.getUsername());

        if (!user.isEnabled())
            throw new ForbiddenException(username);

        String fingerprint = UUID.randomUUID().toString();
        String accessToken = createAccessToken(username, fingerprint);
        String refreshToken = createRefreshToken(username, fingerprint);

        AuthInfo response = new AuthInfo();
        response.setAccessToken(accessToken);
        response.setExpiresIn(accessExpire);
        response.setRefreshToken(refreshToken);
        response.setRefreshExpiresIn(refreshExpire);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, getFingerprintCookie(fingerprint).toString())
                .body(new Response<>(response));
    }

    @Override
    public ResponseEntity<Response<RefreshInfo>> refreshTokens(String refreshToken, String fingerprint)
            throws JwtAuthenticationException, NotFoundException, ForbiddenException, UnauthorizedException {

        validateToken(refreshToken, fingerprint);
        String username = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(refreshToken).getBody().getSubject();

        User user = userService.getUser(username);
        if (!user.isEnabled())
            throw new ForbiddenException(username);

        RefreshInfo response = new RefreshInfo();
        response.setAccessToken(createAccessToken(username, fingerprint));
        response.setExpiresIn(accessExpire);

        return ResponseEntity.ok(new Response<>(response));
    }

    @Override
    public ResponseEntity<Response<?>> revokeFingerprint(String token, String fingerprint)
            throws JwtAuthenticationException, UnauthorizedException {

        validateToken(token, fingerprint);
        fingerprintService.revoke(fingerprint);
        return ResponseEntity.ok(Response.ok());
    }

    private void validateToken(String token, String fingerprint)
            throws JwtAuthenticationException, UnauthorizedException {

        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }

        if (!claims.getAudience().equals(TokenType.REFRESH_TOKEN.getName()))
            throw new JwtAuthenticationException("Invalid token audience");

        if (fingerprint == null)
            throw new UnauthorizedException();

        String jti = (String) claims.get("jti");
        if (!validateHash(fingerprint, jti))
            throw new JwtAuthenticationException("Invalid fingerprint");

        if (fingerprintService.isRevoked(fingerprint))
            throw new JwtAuthenticationException("Fingerprint has been revoked");
    }

    private String createAccessToken(String username, String fingerprint) {
        return createToken(username, fingerprint, TokenType.ACCESS_TOKEN, accessExpire);
    }

    private String createRefreshToken(String username, String fingerprint) {
        return createToken(username, fingerprint, TokenType.REFRESH_TOKEN, refreshExpire);
    }

    private String createToken(String username, String fingerprint, TokenType type, Integer tokenExpire) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("jti", DigestUtils.sha256Hex(fingerprint));

        Date now = new Date();
        Date expire = new Date(now.getTime() + (tokenExpire * 1000));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expire)
                .setAudience(type.getName())
                .signWith(SignatureAlgorithm.RS256, signingKey)
                .compact();
    }

    private ResponseCookie getFingerprintCookie(String fingerprint) {
        return ResponseCookie.from("__Secure-Fgp", fingerprint)
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .build();
    }

    private boolean validateHash(String data, String hash) {
        return hash.equalsIgnoreCase(DigestUtils.sha256Hex((data)));
    }

}