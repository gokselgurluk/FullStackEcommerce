package com.eticare.eticaretAPI.dto.request.ForgotPasswordRequest;

import com.eticare.eticaretAPI.utils.annotationValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {
    @Email
    private String email;
    @NotBlank(message = "Password cannot be empty or blank")
    @annotationValidPassword
    private String password;
    @NotBlank(message = "Password cannot be empty or blank")
    @annotationValidPassword
    private String confirmPassword;
    private String resetPasswordToken ;
}
