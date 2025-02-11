package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.ForgotPasswordRequest.ForgotPasswordRequest;
import com.eticare.eticaretAPI.entity.EmailSend;
import com.eticare.eticaretAPI.service.*;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class EmailSendController {
    private final EmailSendService emailSendService;
    private final CodeService codeService;
    private final SmsService smsService;
    private  final JwtService jwtService;
    private final UserService userService;


    public EmailSendController(EmailSendService emailSendService, CodeService codeService, SmsService smsService, JwtService jwtService, UserService userService ) {
        this.emailSendService = emailSendService;
        this.codeService = codeService;
        this.smsService = smsService;
        this.jwtService = jwtService;
        this.userService = userService;
    
    }

/*
    @PostMapping("/remail-send-verification-code")
    @PreAuthorize("isAuthenticated()")
    public ResultData<?> emailSend(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            VerifyCode verifyCode = verificationService.sendVerifyCodeAndEmail(userDetails);

            Map<String, Object> responseCode = new HashMap<>();
            responseCode.put("mail", verifyCode.getUser().getEmail());
            responseCode.put("verificationCode", verifyCode.getCode());
            responseCode.put("expiryTime", verifyCode.getCodeExpiryDate());
            responseCode.put("sendCount", verifyCode.getSendCount());

            return ResultHelper.successWithData("Doğrulama kodu gönderildi",responseCode,HttpStatus.CREATED);
        } catch (Exception e) {
            return ResultHelper.errorWithData(e.getMessage(),userDetails.getUsername(), HttpStatus.BAD_REQUEST);
        }
    }
*/
@PostMapping("/send-activation-email")
    public ResultData<?> resendActivationEmail(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

    try {
       EmailSend emailSend = emailSendService.sendVerifyTokenEmail(customUserDetails.getUsername());
        return ResultHelper.successWithData("Doğrulama linki gönderildi",emailSend,HttpStatus.CREATED);
    } catch (Exception e) {
        return ResultHelper.errorWithData(e.getMessage(),null, HttpStatus.BAD_REQUEST);
    }
}

    @PostMapping("/forgot-password")
    public ResultData<?> forgotPassword(@RequestBody ForgotPasswordRequest request) throws MessagingException {
        // E-posta adresine sahip kullanıcıyı bul
        try {
            EmailSend emailSend =emailSendService.sendResetPasswordTokenEmail(request.getEmail());
            return ResultHelper.successWithData("Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.", emailSend, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResultHelper.errorWithData(e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

/*    @PostMapping("/send-activation-email")
    public ResultData<?> sendActivationEmail(@RequestHeader ("Authorization") String accessToken) {
        boolean isActivate = jwtService.isUserActive(accessToken);
        try {
            if(accessToken==null || !accessToken.startsWith("Bearer")){
                throw new RuntimeException("Yetkisiz Erişim");
            }
            if(isActivate){
                throw new RuntimeException("Hesap şuan aktif");
            }
           VerifyCode verifyCode = verificationService.sendVerifyCodeAndEmail(accessToken);

            Map<String, Object> responseCode = new HashMap<>();
            responseCode.put("mail", verifyCode.getUser().getEmail());
            responseCode.put("verificationCode", verifyCode.getCode());
            responseCode.put("expiryTime", verifyCode.getCodeExpiryDate());
            responseCode.put("sendCount", verifyCode.getSendCount());

            return ResultHelper.successWithData("Doğrulama kodu gönderildi",responseCode,HttpStatus.CREATED);
        } catch (Exception e) {
            return ResultHelper.errorWithData(e.getMessage(),null, HttpStatus.BAD_REQUEST);
        }
    }*/

    @PostMapping("/sms-send")
    @PreAuthorize("isAuthenticated()")
    public ResultData<?> smsSend(
            @RequestParam String phoneNumber,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResultHelper.Error500("Authentication failed. User is not logged in.");
        }
        // Yeni bir doğrulama token'ı oluştur
        String verificationCode = codeService.generateCode(6);
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
