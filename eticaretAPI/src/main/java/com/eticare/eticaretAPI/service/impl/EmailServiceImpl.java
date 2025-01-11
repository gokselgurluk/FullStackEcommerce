package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private  final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public String sendVerificationEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply@springMailService");
        message.setTo(email);
        message.setSubject("Account Verification Code");
        message.setText("Your verification code is: " + code + "\nThis code is valid for 2 minutes.");
        javaMailSender.send(message);

        return "Mesaj gönderildi";
    }
}
