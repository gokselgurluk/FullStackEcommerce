package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.entity.VerifyCode;
import com.eticare.eticaretAPI.service.EmailService;
import com.eticare.eticaretAPI.service.SmsService;
import com.eticare.eticaretAPI.service.VerificationService;
import org.apache.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class EmailSendController {
    private final EmailService emailService;
    private final VerificationService verificationService;

    private final SmsService smsService;

    public EmailSendController(EmailService emailService, VerificationService verificationService, SmsService smsService) {
        this.emailService = emailService;
        this.verificationService = verificationService;
        this.smsService = smsService;
    }

    @PostMapping("/mail-send-verification-code")
    @PreAuthorize("isAuthenticated()")
    public ResultData<?> emailSend(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            VerifyCode verifyCode = verificationService.sendVerifyCodeAndEmail(userDetails);

            Map<String, Object> responseCode = new HashMap<>();
            responseCode.put("message", "Doğrulama kodu e-posta ile gönderildi.");
            responseCode.put("verificationCode", verifyCode.getCode());
            responseCode.put("expiryTime", verifyCode.getCodeExpiryDate());
            responseCode.put("sendCount", verifyCode.getSendCount());

            return ResultHelper.success(responseCode);
        } catch (Exception e) {
            return ResultHelper.notFound("E-posta gönderilirken bir hata oluştu"+ e.getMessage());
        }
    }

    @PostMapping("/sms-send")
    @PreAuthorize("isAuthenticated()")
    public ResultData<?> smsSend(
            @RequestParam String phoneNumber,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResultHelper.Error500("Authentication failed. User is not logged in.");
        }
        // Yeni bir doğrulama token'ı oluştur
        String verificationCode = verificationService.generateCode(6);
        // Doğrulama kodunu e-posta ile gönder
        smsService.sendSms(phoneNumber,verificationCode);
        // Detaylı bilgi için bir map oluştur
        Map<String,Object> responseCode = new HashMap<>();
        responseCode.put("message","Doğrulama kodu sms ile gönderildi.");
        responseCode.put("verificationCode",verificationCode);//test için code alıyoruz normalde code u gostermeyecegız


        // Başarı mesajı ile birlikte ek bilgileri döndür
        return   ResultHelper.success(responseCode);
    }
}
