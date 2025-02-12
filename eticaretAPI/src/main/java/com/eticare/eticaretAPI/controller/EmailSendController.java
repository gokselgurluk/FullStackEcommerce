package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.config.jwt.JwtService;
import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.ForgotPasswordRequest.ForgotPasswordRequest;
import com.eticare.eticaretAPI.dto.response.EmailSendResponse;
import com.eticare.eticaretAPI.entity.EmailSend;
import com.eticare.eticaretAPI.service.*;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private  final IModelMapperService modelMapperService;


    public EmailSendController(EmailSendService emailSendService, CodeService codeService, SmsService smsService, JwtService jwtService, UserService userService, IModelMapperService modelMapperService) {
        this.emailSendService = emailSendService;
        this.codeService = codeService;
        this.smsService = smsService;
        this.jwtService = jwtService;
        this.userService = userService;

        this.modelMapperService = modelMapperService;
    }


@PostMapping("/send-activation-email")
    public ResponseEntity<?> resendActivationEmail(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

    try {
       EmailSend emailSend = emailSendService.sendVerifyTokenEmail(customUserDetails.getUsername());
        EmailSendResponse emailSendResponse = this.modelMapperService.forResponse().map(emailSend,EmailSendResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResultHelper.successWithData("Doğrulama linki gönderildi",emailSendResponse,HttpStatus.CREATED));
    } catch (Exception e) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultHelper.errorWithData(e.getMessage(),null, HttpStatus.BAD_REQUEST));
    }
}

    @PostMapping("/forgot-password")
    public ResultData<?> forgotPassword(@RequestBody ForgotPasswordRequest request) throws MessagingException {
        // E-posta adresine sahip kullanıcıyı bul
        try {
            EmailSend emailSend =emailSendService.sendResetPasswordTokenEmail(request.getEmail());
            EmailSendResponse emailSendResponse = this.modelMapperService.forResponse().map(emailSend,EmailSendResponse.class);

            return ResultHelper.successWithData("Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.", emailSendResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResultHelper.errorWithData(e.getMessage(), null, HttpStatus.BAD_REQUEST);
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

        // Doğrulama kodunu e-posta ile gönder
        smsService.sendSms(phoneNumber);

        // Başarı mesajı ile birlikte ek bilgileri döndür
        return ResultHelper.successWithData("Doğrulama kodu sms ile gönderildi.",null,HttpStatus.CREATED);

    }


}
