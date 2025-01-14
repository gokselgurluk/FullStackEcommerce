package com.eticare.eticaretAPI.service;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface EmailService {
    String sendVerificationEmail(String email, String code) ;

    BufferedImage generateCodeImage(String code) throws IOException;

    void createImageAndSendEmail(String email, String code);

    void sendVerificationEmailWithMedia(String toEmail, String imagePath);
}
