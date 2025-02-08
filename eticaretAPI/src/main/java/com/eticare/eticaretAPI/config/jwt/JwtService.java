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
    private String SECRET_KEY ;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 *5; // 15 dakika
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 gün
    private static final long ACTIVATION_TOKEN_EXPIRATION = 1000 * 60 * 3 ; // 3 dk

private final UserService userService;

    public JwtService(UserService userService) {
        this.userService = userService;
    }


    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION)) // 7 gün geçerli
                .signWith(SignatureAlgorithm.HS256,getSigningKey())
                .compact();
    }
    // Access Token oluşturma
    public String generateAccessToken(User user) {
       // User user = userService.ge(email).orElseThrow(()->new RuntimeException("generatedAccesToken:Kullanıcı bulunamadı"));

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("active",user.isActive())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }
    public String generateActivationToken(User user,String verificationCode) {
        // User user = userService.ge(email).orElseThrow(()->new RuntimeException("generatedAccesToken:Kullanıcı bulunamadı"));
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("code",verificationCode)
                .claim("active",user.isActive())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACTIVATION_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }
    public String generateResetPasswordToken(User user) {
        // User user = userService.ge(email).orElseThrow(()->new RuntimeException("generatedAccesToken:Kullanıcı bulunamadı"));
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACTIVATION_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }



    public String extractTokenFromHttpRequest(HttpServletRequest request)  {
       String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new UnauthorizedException ("Token bulunamadı veya geçersiz.");
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token) ;
    }
    public boolean isUserActive(String token){
        Claims claims =extractAllClaims(token);
        Boolean isActive = claims.get("active", Boolean.class);
        return Boolean.TRUE.equals(isActive);
    }
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public String extractCode(String token) {
        Claims claims = extractAllClaims(token);
        String code = claims.get("code",String.class);
        return code;
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