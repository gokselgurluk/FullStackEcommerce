package com.eticare.eticaretAPI.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {



    @Value("${jwt.secret}")
    private static String secretKey ;
    private final int validity=1000 * 60 * 60 * 10;


    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("www.e-ticaret.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +validity)) // 10 saat geçerli
                .signWith(SignatureAlgorithm.ES256,secretKey) // Yeni yöntem  // Bean'den gelen anahtarı kullanıyoruz
                .compact();
    }
    public boolean validateToken(String token) {
        if(getTokenUsername(token)!=null && isTokenExpired(token)) {
            return  true;
        }
       return false;
    }
    private Claims getClaims(String token) {
        Claims claims = Jwts.parserBuilder()  // Güncel yöntem: parserBuilder() kullanılıyor
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }
    public String getTokenUsername(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();

    }
    public boolean isTokenExpired(String token) {
            Claims claims = getClaims(token);
            // Token'ın son kullanma tarihi
            return claims.getExpiration().before(new Date(System.currentTimeMillis()));  // Eğer son kullanma tarihi geçmişse, true döner
        }


}
