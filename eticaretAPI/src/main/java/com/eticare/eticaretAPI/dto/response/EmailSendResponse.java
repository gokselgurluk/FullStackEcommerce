package com.eticare.eticaretAPI.dto.response;

import com.eticare.eticaretAPI.entity.enums.SecretTypeEnum;
import com.eticare.eticaretAPI.entity.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailSendResponse {
    private SecretTypeEnum secretTypeEnum;
    private TokenType tokenType;
    private Integer remainingAttempts;
    private Date emailExpiryDate;

}
