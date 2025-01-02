package com.eticare.eticaretAPI.dto.response;


import com.eticare.eticaretAPI.entity.Token;
import com.eticare.eticaretAPI.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String surname;
    private String email;
    private Role roleEnum;
    private Date createdAt;
    private Date lastLogin;
    private boolean active;
    private List<String> AccessTokens;
    private List<String> RefreshTokens;
}
