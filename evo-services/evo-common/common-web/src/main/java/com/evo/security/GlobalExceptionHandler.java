package com.evo.security;

import com.evo.constants.ErrConstants;
import com.evo.exception.AppException;
import com.evo.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public Response<Object> handleAppException(AppException ex) {
        int status = ex.getStatus() == 0 ? HttpStatus.BAD_REQUEST.value() : ex.getStatus();

        Response<Object> appExRes = new Response<>();
        appExRes.setStatusCode(status);
        appExRes.setStatusDesc(HttpStatus.valueOf(status).getReasonPhrase());
        appExRes.setErrCode(ex.getErrCode());
        appExRes.setErrDesc(ex.getErrDesc());
        appExRes.setException(ex);

        return appExRes;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String defaultMessage = Objects.requireNonNull(ex.getFieldError()).getDefaultMessage();
        ErrConstants errConst = ErrConstants.valueOf(defaultMessage);

        Response<Object> response = new Response<>();
        response.setStatusCode(errConst.getStatus().value());
        response.setStatusDesc(errConst.getStatus().getReasonPhrase());
        response.setErrCode(errConst.getErrCode());
        response.setErrDesc(errConst.getErrDesc());
        response.setException(ex);

        return response;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Response<Object> handleAccessDeniedException(AccessDeniedException ex) {
        ErrConstants errConst = ErrConstants.ACCESS_DENIED_001;

        Response<Object> accessDeniedRes = new Response<>();
        accessDeniedRes.setStatusCode(errConst.getStatus().value());
        accessDeniedRes.setStatusDesc(errConst.getStatus().getReasonPhrase());
        accessDeniedRes.setErrCode(errConst.getErrCode());
        accessDeniedRes.setErrDesc(errConst.getErrDesc());

        return accessDeniedRes;
    }
}
