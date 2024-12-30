package com.eticare.eticaretAPI.dto.response;

import lombok.Data;

import java.util.Collection;

@Data
    public class AuthenticationResponse {
        private String token;
        private String username;
        private Collection<?> roles;

        // Constructor, getter, setter

    public AuthenticationResponse(String token, String username, Collection<?> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }
}
