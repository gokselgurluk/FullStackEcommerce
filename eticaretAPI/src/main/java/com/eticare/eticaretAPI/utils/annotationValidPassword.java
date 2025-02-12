package com.eticare.eticaretAPI.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CustomPasswordValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface annotationValidPassword {
    String message() default  "Şifre en az 8 karakter olmalı, büyük harf, küçük harf, rakam ve özel karakter içermelidir.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default  {};

}
