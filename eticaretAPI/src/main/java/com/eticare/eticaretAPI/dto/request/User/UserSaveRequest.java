package com.eticare.eticaretAPI.dto.request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSaveRequest {
    @NotBlank(message = "Name cannot be empty or blank")
    private String name;

    @NotBlank(message = "Surname cannot be empty or blank")
    private String surname;

    @NotBlank(message = "Password cannot be empty or blank")
    private String password;

    @Email(message = "Email must be a valid format")
    @NotBlank(message = "Email cannot be empty or blank")
    private String email;
}
