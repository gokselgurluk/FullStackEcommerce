package com.eticare.eticaretAPI.config.jwt;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
@Service
@Data
public class JwtService {

    @Value("${jwt.secret}")
    private String secretBase64;

    @PostConstruct
    private String getSecretKey() throws Exception {
        // Base64 formatındaki secret anahtarını çöz
        byte[] secretKeyBytes = Base64.getDecoder().decode(secretBase64);

        // HMAC için Mac nesnesini başlat
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
        mac.init(secretKeySpec);

        // Şifreli anahtarı sabit olarak atama
        return Base64.getEncoder().encodeToString(mac.doFinal("dummyMessage".getBytes()));
    }

}

