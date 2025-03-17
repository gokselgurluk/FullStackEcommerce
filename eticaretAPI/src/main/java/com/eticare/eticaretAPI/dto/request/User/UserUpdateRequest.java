package com.eticare.eticaretAPI.dto.request.User;

import com.eticare.eticaretAPI.entity.enums.RoleEnum;
import com.eticare.eticaretAPI.utils.annotationValidPassword;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class UserUpdateRequest {
    @Positive(message = "Id pozıtıf olmalı")
    private Long id ;

    @NotBlank(message = "Name cannot be empty or blank")
    @Size(max = 50, message = "First name must be less than 50 characters.")
    private String username;

    @NotBlank(message = "Surname cannot be empty or blank")
    @Size(max = 50, message = "Last name must be less than 50 characters.")
    private String surname;

    @NotBlank(message = "Password cannot be empty or blank")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    @annotationValidPassword
    private String password; // Password hash yapılmalı

    @Email(message = "Email must be a valid format")
    @NotBlank(message = "Email cannot be empty or blank")
    private String email;

   /* private Boolean active; // Kullanıcı aktif/pasif durumu

    private Date accountLockedTime;

    private long diffLockedTime ;

    private int incrementFailedLoginAttempts;

    private boolean isAccountLocked  ; // Varsayılan olarak kullanıcı aktif değil

    private RoleEnum roleEnum; // Kullanıcı rolü (isteğe bağlı)
*/
}
