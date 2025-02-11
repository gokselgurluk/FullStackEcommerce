package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.EmailSend;
import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.Code;

public interface CodeService {

    boolean activateUser(String email);

    String generateCode(Integer code);

    Code createVerifyCode(User user);

    Boolean isValidateCode(User user, String code );




}
