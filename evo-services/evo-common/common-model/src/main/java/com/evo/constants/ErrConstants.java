package com.evo.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrConstants {
    /* Server */
    SYSTEM_ERROR_001("SYSTEM_ERROR_001",
            "An unexpected error occurred on the server. Please try again later.",
            HttpStatus.INTERNAL_SERVER_ERROR),

    ACCESS_DENIED_001("ACCESS_DENIED_001",
            "Access denied. Please try again later.",
            HttpStatus.FORBIDDEN),

    INPUT_ERROR_001("INPUT_ERROR_001",
            "Required field is missing or empty.",
            HttpStatus.BAD_REQUEST),

    INPUT_ERROR_002("INPUT_ERROR_002",
            "This field must be between 4 and 255 characters.",
            HttpStatus.BAD_REQUEST),

    EMAIL_ERROR_001("EMAIL_ERROR_001",
            "Invalid email format. Must contain '@' and a domain (e.g., user@example.com).",
            HttpStatus.BAD_REQUEST),

    PASSWORD_ERROR_001("PASSWORD_ERROR_001",
            "Password must be between 8 and 255 characters.",
            HttpStatus.BAD_REQUEST),

    PASSWORD_ERROR_002("PASSWORD_ERROR_002",
            "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.",
            HttpStatus.BAD_REQUEST),

    /* Auth */
    AUTH_ERROR_001("AUTH_ERROR_001",
            "Your account has been locked. Please contact support for assistance.",
            HttpStatus.FORBIDDEN),

    AUTH_ERROR_002("AUTH_ERROR_002",
            "Incorrect password. Please try again or reset your password if you have forgotten it.",
            HttpStatus.UNAUTHORIZED),

    AUTH_ERROR_003("AUTH_ERROR_003",
            "Login failed. Please check your username or password and try again.",
            HttpStatus.UNAUTHORIZED),

    AUTH_ERROR_004("AUTH_ERROR_004",
            "Registration failed. Please verify your information and try again.",
            HttpStatus.BAD_REQUEST),


    /* User */
    USER_DETAIL_ERROR_001("USER_DETAIL_ERROR_001",
            "No account found with the provided username. Please check your credentials and try again.",
            HttpStatus.NOT_FOUND),

    USER_DETAIL_ERROR_002("USER_DETAIL_ERROR_002",
            "The username is already taken. Please choose a different one.",
            HttpStatus.BAD_REQUEST),

    USER_DETAIL_ERROR_003("USER_DETAIL_ERROR_003",
            "The email address is already registered. Please use a different email.",
            HttpStatus.BAD_REQUEST),

    /* Role */
    ROLE_ERROR_001("ROLE_ERROR_001",
            "The role has already been deleted.",
            HttpStatus.BAD_REQUEST),

    ROLE_ERROR_002("ROLE_ERROR_002",
            "The role is currently available and does not need restoration",
            HttpStatus.BAD_REQUEST),

    ROLE_DETAIL_ERROR_001("ROLE_DETAIL_ERROR_001",
            "Role not found",
            HttpStatus.NOT_FOUND),

    ROLE_DETAIL_ERROR_002("ROLE_DETAIL_ERROR_002",
            "Role already exists",
            HttpStatus.BAD_REQUEST),

    CHANGE_ROLE_ERROR_001("CHANGE_ROLE_ERROR_001",
            "New role should be different from the current role. Please choose another one.",
            HttpStatus.BAD_REQUEST),

    /* Permission */
    PERMISSION_ERROR_001("PERMISSION_ERROR_001",
            "The permission has already been deleted.",
            HttpStatus.BAD_REQUEST),

    PERMISSION_ERROR_002("PERMISSION_ERROR_002",
            "The permission is currently available and does not need restoration.",
            HttpStatus.BAD_REQUEST),

    PERMISSION_DETAIL_ERROR_001("PERMISSION_DETAIL_ERROR_001",
            "Permission not found.",
            HttpStatus.NOT_FOUND),

    PERMISSION_DETAIL_ERROR_002("PERMISSION_DETAIL_ERROR_002",
            "Permission already exists.",
            HttpStatus.BAD_REQUEST),


    ;

    private String errCode;
    private String errDesc;
    private HttpStatus status;

    ErrConstants(String errCode, String errDesc, HttpStatus status) {
        this.errCode = errCode;
        this.errDesc = errDesc;
        this.status = status;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public void setErrDesc(String errDesc) {
        this.errDesc = errDesc;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
