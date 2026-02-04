package com.flm.irai.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenTtlMs;
    private final long refreshTokenTtlMs;

    public JwtService(
            @Value("${security.jwt.secret:ZmFrZS1zZWNyZXQtbm90LXByb2Qta2VlcC1tZS1zYWZlLTI1NmJpdA==}") String secret,
            @Value("${security.jwt.access-token-ttl-ms:3600000}") long accessTokenTtlMs,
            @Value("${security.jwt.refresh-token-ttl-ms:604800000}") long refreshTokenTtlMs
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenTtlMs = accessTokenTtlMs;
        this.refreshTokenTtlMs = refreshTokenTtlMs;
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails, accessTokenTtlMs, Map.of("type", "access"));
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, refreshTokenTtlMs, Map.of("type", "refresh"));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    private String buildToken(UserDetails userDetails, long ttlMs, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(ttlMs);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }
}
