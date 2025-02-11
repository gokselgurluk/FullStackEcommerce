package com.eticare.eticaretAPI.config.jwt;

import com.eticare.eticaretAPI.config.exeption.UnauthorizedException;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 dakika
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 gün
    private static final long ACTIVATION_TOKEN_EXPIRATION = 1000 * 60 * 2; // 3 dk



    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION)) // 7 gün geçerli
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("isActive", user.isActive())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    public String generateActivationToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("isActive", user.isActive())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACTIVATION_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    public String generateResetPasswordToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACTIVATION_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }


    public static String extractTokenFromHttpRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new UnauthorizedException("Token bulunamadı veya geçersiz.");
    }
    public String extractCode(String token) {
        Claims claims = extractAllClaims(token);
        String code = claims.get("code", String.class);
        return code;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    public boolean isUserActive(String token) {
        Claims claims = extractAllClaims(token);
        Boolean isActive = claims.get("isActive", Boolean.class);
        return Boolean.TRUE.equals(isActive);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}