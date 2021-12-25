package works.red_eye.hood.auth.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
    public ResponseEntity<Response> issueTokens(String username, String password)
            throws NotFoundException, ForbiddenException, UnauthorizedException {

        User user = userService.getUser(username);
        if (!userService.isCorrectPassword(user, password))
            throw new UnauthorizedException();

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
                .body(Response.ok(response));
    }

    @Override
    public ResponseEntity<Response> refreshTokens(String refreshToken, String fingerprint)
            throws JwtAuthenticationException, NotFoundException, ForbiddenException {

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

        return ResponseEntity.ok(Response.ok(response));
    }

    @Override
    public ResponseEntity<Response> revokeFingerprint(String token, String fingerprint)
            throws JwtAuthenticationException {

        validateToken(token, fingerprint);
        fingerprintService.revoke(fingerprint);
        return ResponseEntity.ok(Response.ok());
    }

    private void validateToken(String token, String fingerprint)
            throws JwtAuthenticationException {

        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }

        if (claims.getExpiration().before(new Date()))
            throw new JwtAuthenticationException("JWT token is expired");

        String fingerprintHash = (String) claims.get("fingerprint");
        if (!validateHash(fingerprint, fingerprintHash))
            throw new JwtAuthenticationException("Invalid fingerprint");

        if (fingerprintService.isRevoked(fingerprint))
            throw new JwtAuthenticationException("Fingerprint has been revoked");
    }

    private String createAccessToken(String username, String fingerprint) {
        return createToken(username, fingerprint, accessExpire);
    }

    private String createRefreshToken(String username, String fingerprint) {
        return createToken(username, fingerprint, refreshExpire);
    }

    private String createToken(String username, String fingerprint, Integer tokenExpire) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("fingerprint", DigestUtils.sha256Hex(fingerprint));

        Date now = new Date();
        Date expire = new Date(now.getTime() + (tokenExpire * 1000));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expire)
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