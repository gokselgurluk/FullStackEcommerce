package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.entity.VerificationToken;
import com.eticare.eticaretAPI.service.EmailService;
import com.eticare.eticaretAPI.service.VerificationTokenService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.Position;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class EmailSendController {
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;

    public EmailSendController(EmailService emailService, VerificationTokenService verificationTokenService) {
        this.emailService = emailService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("/mail-send")
    @PreAuthorize("isAuthenticated()")
    public ResultData<?> emailSend(
            @RequestParam String email,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResultHelper.Error500("Authentication failed. User is not logged in.");
        }
        // Yeni bir doğrulama token'ı oluştur
        VerificationToken verificationToken =verificationTokenService.createVerificationToken(email);
        if (verificationToken == null) {
            throw new RuntimeException("Token olsuturulamadı");
        }
        // Doğrulama kodunu e-posta ile gönder
        emailService.createImageAndSendEmail(email,verificationToken.getCode());
        // Detaylı bilgi için bir map oluştur
        Map<String,Object> responseToken = new HashMap<>();
        responseToken.put("message","Doğrulama kodu e-posta ile gönderildi.");
        responseToken.put("verificationCode",verificationToken.getCode());//test için code alıyoruz normalde code u gostermeyecegız
        responseToken.put("expiryDate",verificationToken.getCodeExpiryDate());
        responseToken.put("sendCount",verificationToken.getSendCount());
        // Başarı mesajı ile birlikte ek bilgileri döndür
        return   ResultHelper.success(responseToken);
    }
}
