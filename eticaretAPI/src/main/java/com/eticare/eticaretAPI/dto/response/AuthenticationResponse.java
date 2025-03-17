package com.eticare.eticaretAPI.dto.response;

import com.eticare.eticaretAPI.entity.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
    public class AuthenticationResponse {
        private String accessToken;
        private String refreshToken;
        private String email;
        private RoleEnum role;
        private boolean isActive;

        // Constructor, getter, setter
}
