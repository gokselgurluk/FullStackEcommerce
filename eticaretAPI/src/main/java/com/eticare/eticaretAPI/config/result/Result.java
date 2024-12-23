package com.eticare.eticaretAPI.config.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result {
    private boolean status;
    private String message;
    private String httpCode;
}
