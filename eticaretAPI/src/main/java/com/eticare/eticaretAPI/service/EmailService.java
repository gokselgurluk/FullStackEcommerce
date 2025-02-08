package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerifyCode;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface EmailService {
    String sendVerificationEmail(String email, String code) ;

   // BufferedImage generateCodeImage(String code) throws IOException;

   // void createImageAndSendEmail(String email, String code);

    void sendVerificationEmailWithMedia(User user , VerifyCode verifyCode);

    void sendResetPasswordEmail(String email);
}
