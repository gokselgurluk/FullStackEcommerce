package com.eticare.eticaretAPI.dto.request.User;

import com.eticare.eticaretAPI.entity.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class UserUpdateRequest {
    @Positive(message = "Id pozıtıf olmalı")
    private Long id ;

    @NotBlank(message = "Name cannot be empty or blank")
    @Size(max = 50, message = "First name must be less than 50 characters.")
    private String name;

    @NotBlank(message = "Surname cannot be empty or blank")
    @Size(max = 50, message = "Last name must be less than 50 characters.")
    private String surname;

    @NotBlank(message = "Password cannot be empty or blank")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String password; // Password hash yapılmalı

    @Email(message = "Email must be a valid format")
    @NotBlank(message = "Email cannot be empty or blank")
    private String email;

    private Boolean active; // Kullanıcı aktif/pasif durumu

    //private Date lastLogin; // Son giriş tarihi (isteğe bağlı)

    private Role roleEnum; // Kullanıcı rolü (isteğe bağlı)

}
