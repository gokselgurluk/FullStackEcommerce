package com.eticare.eticaretAPI.dto.request.ActivatedAccount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationRequest {
    private String tokenValue;
    private String codeValue;
    private String email;

}
