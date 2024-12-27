package com.eticare.eticaretAPI.dto.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }
}
