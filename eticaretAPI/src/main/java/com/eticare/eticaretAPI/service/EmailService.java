package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.entity.VerificationToken;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface EmailService {
    String sendVerificationEmail(String email, String code) ;

    BufferedImage generateCodeImage(String code) throws IOException;

    void createImageAndSendEmail(String email, String code);

    void sendVerificationEmailWithImage(String toEmail, String imagePath);
}
