package com.eticare.eticaretAPI.service;

public interface EmailService {
    void sendVerificationEmail(String email, String code) ;

}
