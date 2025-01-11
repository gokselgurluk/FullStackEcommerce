package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.entity.VerificationToken;

public interface EmailService {
    String sendVerificationEmail(String email, String code) ;

}
