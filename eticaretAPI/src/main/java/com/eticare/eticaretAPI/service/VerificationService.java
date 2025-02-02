package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.config.jwt.CustomUserDetails;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerifyCode;
import jakarta.transaction.Transactional;

public interface VerificationService {

    boolean activateUser(String email, String code);

    String generateCode(Integer code);

    VerifyCode createVerifyCode(User user);

    Boolean isValidateCode(User user, String code );

    VerifyCode sendVerifyCodeAndEmail(CustomUserDetails userDetails);

    VerifyCode sendVerifyCodeAndEmail(String email);
}
