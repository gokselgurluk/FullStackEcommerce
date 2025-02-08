package com.eticare.eticaretAPI.dto.request.ForgotPasswordRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String resetPasswordToken ;
}
