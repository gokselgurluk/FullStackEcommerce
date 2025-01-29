package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerifyCode;

public interface VerificationService {

    boolean activateUser(String email, String code);

    String generateCode(Integer code);

    VerifyCode createVerifyCode(String email);

    Boolean isValidateCode(User user, String code );

    VerifyCode sendVerifyCodeAndEmail(CustomUserDetails userDetails);
}
