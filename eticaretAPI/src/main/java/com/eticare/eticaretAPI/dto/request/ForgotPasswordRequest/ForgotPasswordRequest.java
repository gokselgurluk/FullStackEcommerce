package com.eticare.eticaretAPI.dto.request.ForgotPasswordRequest;

import com.eticare.eticaretAPI.utils.annotationValidPassword;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {
    @Email
    private String email;
    @annotationValidPassword
    private String password;
    @annotationValidPassword
    private String confirmPassword;
    private String resetPasswordToken ;
}
