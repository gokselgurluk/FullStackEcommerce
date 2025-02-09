package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.Code;
import com.eticare.eticaretAPI.entity.EmailSend;
import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.enums.SecretTypeEnum;
import com.eticare.eticaretAPI.entity.enums.TokenType;

public interface EmailSendService {
    String sendVerificationEmail(String email, String code) ;

   // BufferedImage generateCodeImage(String code) throws IOException;

   // void createImageAndSendEmail(String email, String code);

    EmailSend saveOrUpdateEmailSend(User user, Token token, Code code, TokenType tokenType, SecretTypeEnum secretTypeEnum);

    boolean sendVerificationEmailWithMedia(Token token );

    void sendResetPasswordEmail(String email);

    EmailSend sendVerifyTokenEmail(String email);
}
