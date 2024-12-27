package com.eticare.eticaretAPI.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key secretKey;
    // private static final String SECRET_KEY = "you";

    public JwtUtil(Key secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 saat geçerli
                .signWith(secretKey) // Yeni yöntem  // Bean'den gelen anahtarı kullanıyoruz
                .compact();
    }


    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)  // Bean'den gelen anahtarı kullanıyoruz
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();

    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

        public boolean isTokenExpired(String token) {
            Claims claims = Jwts.parserBuilder()  // Güncel yöntem: parserBuilder() kullanılıyor
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();  // Token'ın son kullanma tarihi
            return expiration.before(new Date());  // Eğer son kullanma tarihi geçmişse, true döner
        }
}
