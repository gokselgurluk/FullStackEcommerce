package com.eticare.eticaretAPI.dto.response;

import lombok.Data;

import java.util.Collection;

@Data
    public class AuthenticationResponse {
        private String accessTokens;;
        private String username;
        private Collection<?> roles;

        // Constructor, getter, setter

    public AuthenticationResponse(String token, String username, Collection<?> roles) {
        this.accessTokens = token;
        this.username = username;
        this.roles = roles;
    }
}
