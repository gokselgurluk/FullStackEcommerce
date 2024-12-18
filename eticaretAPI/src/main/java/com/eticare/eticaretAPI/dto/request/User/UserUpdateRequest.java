package com.eticare.eticaretAPI.dto.request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @Size(max = 50, message = "First name must be less than 50 characters.")
    private String firstName; // Ad, maksimum 50 karakter olabilir.

    @Size(max = 50, message = "Last name must be less than 50 characters.")
    private String lastName;  // Soyad, maksimum 50 karakter olabilir.

    @Email(message = "Invalid email format.")
    private String email;     // E-posta adresi, geçerli bir e-posta formatında olmalıdır.

    @Size(max = 15, message = "Phone number must be less than 15 characters.")
    private String phoneNumber; // Telefon numarası, maksimum 15 karakter uzunluğunda olmalıdır.

    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String password;  // Şifre, en az 8 karakter uzunluğunda olmalıdır.
}
