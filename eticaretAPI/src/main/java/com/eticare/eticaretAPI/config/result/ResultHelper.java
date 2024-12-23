package com.eticare.eticaretAPI.config.result;

import org.springframework.http.HttpStatusCode;

import java.util.List;

public class ResultHelper {
    public static <T> ResultData<T> Error500(T data){
        return  new ResultData<>(true,ResultMessages.VALIDATION_ERROR,500,data);
    }

    public static <T> ResultData<T> created(T data) {
        return new ResultData<>(true, ResultMessages.CREATED,"201",data);
    }
    public static Result validationError(List<String> data) {
        return new ResultData<>(false, ResultMessages.VALIDATION_ERROR, 400, data);
    }

    public static<T> ResultData<T> notFound(T data) {
        return new ResultData<>(false, ResultMessages.NOT_FOUND, 404,data);
    }

    public static <T> ResultData<T> success(T data) {
        return new ResultData<>(true, ResultMessages.OK, 200, data);
    }
}
