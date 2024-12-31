package com.eticare.eticaretAPI.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretKeyBase64;
    private final long validity = 1000 * 60 * 60 * 10;

    public String generateToken(String username) {
        if (secretKeyBase64 == null || secretKeyBase64.isEmpty()) {
            throw new IllegalArgumentException("JWT Secret key cannot be null or empty. Please check your configuration.");
        }

        SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyBase64));

        return Jwts.builder()
                .setSubject(username)
                .setIssuer("www.e-ticaret.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String usernameFromToken = getTokenUsername(token);
        return usernameFromToken.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private Claims getClaims(String token) {
        if (secretKeyBase64 == null || secretKeyBase64.isEmpty()) {
            throw new IllegalArgumentException("JWT Secret key cannot be null or empty. Please check your configuration.");
        }
        SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyBase64));
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getTokenUsername(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public boolean isTokenExpired(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().before(new Date(System.currentTimeMillis()));
    }
}