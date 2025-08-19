package com.evo.security;

import com.evo.constants.ErrConstants;
import com.evo.exception.AppException;
import com.evo.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Response<Object>> handleAppException(AppException ex) {
        int status = ex.getStatus() == 0 ? HttpStatus.BAD_REQUEST.value() : ex.getStatus();

        Response<Object> appExRes = new Response<>();
        appExRes.setStatusCode(status);
        appExRes.setStatusDesc(HttpStatus.valueOf(status).getReasonPhrase());
        appExRes.setErrCode(ex.getErrCode());
        appExRes.setErrDesc(ex.getErrDesc());
        appExRes.setException(ex);

        return ResponseEntity.status(status).body(appExRes);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String defaultMessage = Objects.requireNonNull(ex.getFieldError()).getDefaultMessage();
        ErrConstants errConst = ErrConstants.valueOf(defaultMessage);

        Response<Object> inputExRes = new Response<>();
        inputExRes.setStatusCode(errConst.getStatus().value());
        inputExRes.setStatusDesc(errConst.getStatus().getReasonPhrase());
        inputExRes.setErrCode(errConst.getErrCode());
        inputExRes.setErrDesc(errConst.getErrDesc());
        inputExRes.setException(ex);

        return ResponseEntity.status(errConst.getStatus().value()).body(inputExRes);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        ErrConstants errConst = ErrConstants.ACCESS_DENIED_001;

        Response<Object> accessDeniedExRes = new Response<>();
        accessDeniedExRes.setStatusCode(errConst.getStatus().value());
        accessDeniedExRes.setStatusDesc(errConst.getStatus().getReasonPhrase());
        accessDeniedExRes.setErrCode(errConst.getErrCode());
        accessDeniedExRes.setErrDesc(errConst.getErrDesc());

        return ResponseEntity.status(errConst.getStatus().value()).body(accessDeniedExRes);
    }
}
