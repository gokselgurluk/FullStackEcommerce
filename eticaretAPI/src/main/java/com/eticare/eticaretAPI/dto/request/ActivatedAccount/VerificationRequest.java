package com.eticare.eticaretAPI.dto.request.ActivatedAccount;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerificationRequest {
    private String tokenValue;
    private String codeValue;
    private String email;

}
