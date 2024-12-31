package com.eticare.eticaretAPI.config.jwt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Message")
public class JwtMessageController {

    @GetMapping
    public ResponseEntity<String> mesajver() {
        String mesajBody = "Jwt Çalışıyor";
        return ResponseEntity.ok(mesajBody);
    }
}
