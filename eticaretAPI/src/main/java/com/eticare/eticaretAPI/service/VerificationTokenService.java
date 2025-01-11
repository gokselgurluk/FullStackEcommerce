package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.VerificationToken;

public interface VerificationTokenService {


    String generateVerificationCode(Integer code);

    VerificationToken createVerificationToken (String email);

    Boolean validateVerificationCode(User user,String code );


}
