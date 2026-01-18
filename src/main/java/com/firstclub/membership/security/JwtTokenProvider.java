package com.firstclub.membership.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        logger.debug("Generating JWT token for userId: {}, username: {}", 
                    userPrincipal.getId(), userPrincipal.getUsername());
        logger.trace("Token expiry date: {}", expiryDate);

        String token = Jwts.builder()
                .subject(Long.toString(userPrincipal.getId()))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();

        logger.debug("JWT token generated successfully for userId: {}", userPrincipal.getId());
        return token;
    }

    public Long getUserIdFromToken(String token) {
        logger.trace("Extracting userId from JWT token");
        
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId = Long.parseLong(claims.getSubject());
        logger.trace("Extracted userId: {} from token", userId);
        return userId;
    }

    public boolean validateToken(String token) {
        try {
            logger.trace("Validating JWT token");
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            logger.trace("JWT token validation successful");
            return true;
        } catch (Exception ex) {
            logger.warn("Invalid JWT token - {}: {}", ex.getClass().getSimpleName(), ex.getMessage());
            logger.debug("Token validation error details", ex);
        }
        return false;
    }
}
