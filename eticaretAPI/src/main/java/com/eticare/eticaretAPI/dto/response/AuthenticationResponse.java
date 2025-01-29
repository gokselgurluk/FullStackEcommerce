package com.eticare.eticaretAPI.dto.response;

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
        private String accessToken;;
        private String email;
        private Collection<?> roles;
        private boolean isActive;

        // Constructor, getter, setter
}
