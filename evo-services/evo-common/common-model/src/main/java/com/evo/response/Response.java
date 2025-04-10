package com.evo.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> implements Serializable {
    private int statusCode;
    private String statusDesc;
    protected T data;
    private String errCode;
    private String errDesc;
    private String message;
    private long timestamp;

    @JsonIgnore
    private Exception exception;

    public Response() {
        this.timestamp = Instant.now().toEpochMilli();
    }

    // Success response
    public static <T> Response<T> of(T res) {
        return new Response<T>().data(res).success();
    }

    public static <T> Response<T> created(T res) {
        Response<T> response = new Response<T>();
        response.setData(res);
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setStatusDesc(HttpStatus.CREATED.getReasonPhrase());
        return response;
    }


    public static <T> Response<T> ok() {
        return new Response<T>().success();
    }

    // Failure response
    public static <T> Response<T> fail(HttpStatus status, String message, Exception exception) {
        return new Response<T>().status(status).fail(message, exception);
    }

    public static <T> Response<T> fail(HttpStatus status, String message) {
        return new Response<T>().status(status).fail(message);
    }

    public static <T> Response<T> fail(HttpStatus status, Exception exception) {
        return new Response<T>().status(status).fail(exception);
    }

    public Response<T> data(T res) {
        this.data = res;
        return this;
    }

    public Response<T> success() {
        this.statusCode = HttpStatus.OK.value();
        this.statusDesc = HttpStatus.OK.getReasonPhrase();
        return this;
    }

    public Response<T> success(String message) {
        this.statusCode = HttpStatus.OK.value();
        this.statusDesc = HttpStatus.OK.getReasonPhrase();
        this.message = message;
        return this;
    }

    public Response<T> fail(String message, Exception exception) {
        this.message = message;
        this.exception = exception;
        return this;
    }

    public Response<T> fail(String message) {
        this.message = message;
        return this;
    }

    public Response<T> fail(Exception exception) {
        this.exception = exception;
        return this;
    }

    public Response<T> status(HttpStatus status) {
        this.statusCode = status.value();
        this.statusDesc = status.getReasonPhrase();
        return this;
    }

    public Response<T> error(String errCode, String errDesc) {
        this.errCode = errCode;
        this.errDesc = errDesc;
        return this;
    }
}
