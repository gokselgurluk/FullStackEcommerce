package com.eticare.eticaretAPI.dto.request.User;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Positive(message = "ID Değeri pozitif olmak zorunda")
    private long id;
    @NotBlank(message = "Name cannot be empty or blank")
    @Size(max = 50, message = "First name must be less than 50 characters.")
    private String firstName; // Ad, maksimum 50 karakter olabilir.

    @NotBlank(message = "Surname cannot be empty or blank")
    @Size(max = 50, message = "Last name must be less than 50 characters.")
    private String lastName;  // Soyad, maksimum 50 karakter olabilir.


    @NotBlank(message = "Email cannot be empty or blank")
    @Email(message = "Email must be a valid format")
    private String email;     // E-posta adresi, geçerli bir e-posta formatında olmalıdır.

    @NotBlank(message = "Phone cannot be empty or blank")
    @Size(max = 15, message = "Phone number must be less than 15 characters.")
    private String phoneNumber; // Telefon numarası, maksimum 15 karakter uzunluğunda olmalıdır.

    @NotBlank(message = "Password cannot be empty or blank")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String password;  // Şifre, en az 8 karakter uzunluğunda olmalıdır.
}
