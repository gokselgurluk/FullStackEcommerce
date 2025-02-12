package com.eticare.eticaretAPI.utils;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;

public class CustomPasswordValidator implements ConstraintValidator<annotationValidPassword, String> {
//passay kutuphanesının bagımlılıgından gelıyor
    PasswordValidator validator = new PasswordValidator(Arrays.asList(
            new LengthRule(8, 20), // Şifre en az 8, en fazla 20 karakter olmalı
            new CharacterRule(EnglishCharacterData.UpperCase, 1), // En az 1 büyük harf
            new CharacterRule(EnglishCharacterData.LowerCase, 1), // En az 1 küçük harf
            new CharacterRule(EnglishCharacterData.Digit, 1), // En az 1 rakam
            new CharacterRule(EnglishCharacterData.Special, 1) // En az 1 özel karakter
    ));

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        RuleResult result = validator.validate(new PasswordData(password));

        if (!result.isValid()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Şifre gereksinimleri karşılamıyor!").addConstraintViolation();
        }

        return result.isValid();
    }
}