package com.eticare.eticaretAPI.config.result;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ResultData<T> extends Result {
    private  T data ;
    public ResultData(boolean status, String message, String httpCode, T data) {
        super(status, message, httpCode);
        this.data=data;
    }

    public ResultData(boolean status, String message, int httpCode, T data) {
        super(status, message, String.valueOf(httpCode));
        this.data=data;

    }

}
