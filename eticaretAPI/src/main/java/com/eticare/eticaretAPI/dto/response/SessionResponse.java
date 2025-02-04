package com.eticare.eticaretAPI.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class SessionResponse {
    private Long id;
    private String email;
    private String ipAddress;
    private String browser;
    private String os;
    private String device;
    private Date createdAt;
    private Date expiresAt;
}
