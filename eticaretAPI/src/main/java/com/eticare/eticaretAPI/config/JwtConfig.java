package com.eticare.eticaretAPI.config;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Base64;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {

    private String secret;

    @Bean
    public Key secretKey() {
        //return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        return Keys.hmacShaKeyFor(secret.getBytes());// Anahtarı oluşturuyoruz
    }
}
