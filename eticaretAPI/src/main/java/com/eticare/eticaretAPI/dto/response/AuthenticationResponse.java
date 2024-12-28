package com.eticare.eticaretAPI.dto.response;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String token;

    public AuthenticationResponse(String token) {
        this.token = token;
    }
}
