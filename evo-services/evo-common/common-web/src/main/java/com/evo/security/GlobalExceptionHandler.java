package com.evo.security;

import com.evo.constans.ErrConstans;
import com.evo.exception.AppException;
import com.evo.response.Response;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public Response<Object> handleAppException(AppException ex) {
        int status = ex.getStatus() == 0 ? HttpStatus.BAD_REQUEST.value() : ex.getStatus();

        Response<Object> response = new Response<>();
        response.setStatusCode(status);
        response.setStatusDesc(HttpStatus.valueOf(status).getReasonPhrase());
        response.setErrCode(ex.getErrCode());
        response.setErrDesc(ex.getErrDesc());
        response.setException(ex);

        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Object> handleAppException(MethodArgumentNotValidException ex) {
        String defaultMessage = Objects.requireNonNull(ex.getFieldError()).getDefaultMessage();
        ErrConstans errConst = ErrConstans.valueOf(defaultMessage);
//        var constraintViolation =
//                ex.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
//        Map<String, Object> attributes = constraintViolation.getConstraintDescriptor().getAttributes();

        Response<Object> response = new Response<>();
        response.setStatusCode(errConst.getStatus().value());
        response.setStatusDesc(errConst.getStatus().getReasonPhrase());
        response.setErrCode(errConst.getErrCode());
        response.setErrDesc(errConst.getErrDesc());
//        response.setData(attributes);
        response.setException(ex);

        return response;
    }
}
