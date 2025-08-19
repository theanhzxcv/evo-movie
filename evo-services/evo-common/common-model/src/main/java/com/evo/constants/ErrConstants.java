package com.evo.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrConstants {
    /* Server */
    SYSTEM_ERROR_001("SYSTEM_ERROR_001",
            "An unexpected error occurred on the server. Please try again later.",
            HttpStatus.INTERNAL_SERVER_ERROR),

    SESSION_ERROR_001("SESSION_ERROR_002",
            "Looks like something went wrong. Please log in again to continue.",
            HttpStatus.UNAUTHORIZED),

    ACCESS_DENIED_001("ACCESS_DENIED_001",
            "Access denied. Please try again later.",
            HttpStatus.FORBIDDEN),

    INPUT_ERROR_001("INPUT_ERROR_001",
            "This field cannot be left blank or contain invalid characters.",
            HttpStatus.BAD_REQUEST),

    INPUT_ERROR_002("INPUT_ERROR_002",
            "Please enter valid information in this field.",
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
    LOGIN_ERROR_001("LOGIN_ERROR_001",
            "The current password you entered is incorrect. Please try again.",
            HttpStatus.BAD_REQUEST),

    LOGIN_ERROR_002("LOGIN_ERROR_002",
            "Your account has been temporarily locked due to multiple failed login attempts. Please try again later.",
            HttpStatus.LOCKED),

    LOGIN_ERROR_003("LOGIN_ERROR_003",
            "Your account has been temporarily locked for 15 minutes. Please try again later.",
            HttpStatus.LOCKED),

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
            HttpStatus.CONFLICT),

    AUTH_ERROR_005("AUTH_ERROR_005",
            "The OTP you entered is incorrect. Please check and try again.",
            HttpStatus.UNAUTHORIZED),

    /* User */
    USER_ERROR_001("USER_ERROR_001",
            "This user is already been deleted.",
            HttpStatus.NOT_FOUND),

    USER_ERROR_002("USER_ERROR_002",
            "This user is currently available and does not need restoration.",
            HttpStatus.CONFLICT),

    USER_ERROR_003("USER_ERROR_003",
            "We couldn't find an email associated with your account. Please update your email to continue.",
            HttpStatus.BAD_REQUEST),

    USER_ERROR_004("USER_ERROR_004",
            "Your account has already been verified.",
            HttpStatus.BAD_REQUEST),

    USER_ERROR_005("USER_ERROR_005",
            "Verification information is invalid.",
            HttpStatus.BAD_REQUEST),

    USER_ERROR_006("USER_ERROR_006",
            "The verification link has expired. Please request a new one.",
            HttpStatus.BAD_REQUEST),

    ACCOUNT_ERROR_001("USER_ERROR_007",
            "Feature access is restricted due to unverified account. A verification link will be sent to your email.",
            HttpStatus.BAD_REQUEST),

    ACCOUNT_ERROR_002("USER_ERROR_008",
            "Feature access is restricted due to unverified account. Please provide an email address.",
            HttpStatus.BAD_REQUEST),

    USER_DETAIL_ERROR_001("USER_DETAIL_ERROR_001",
            "No account found with the provided username. Please check your credentials and try again.",
            HttpStatus.NOT_FOUND),

    USER_DETAIL_ERROR_002("USER_DETAIL_ERROR_002",
            "The username is already taken. Please choose a different one.",
            HttpStatus.CONFLICT),

    USER_DETAIL_ERROR_003("USER_DETAIL_ERROR_003",
            "The email address is already registered. Please use a different email.",
            HttpStatus.CONFLICT),

    TFA_ERROR_001(
            "TFA_ERROR_001",
            "Two-Factor Authentication is already enabled for this account.",
            HttpStatus.CONFLICT),

    TFA_ERROR_002(
            "TFA_ERROR_002",
            "Two-Factor Authentication is already disabled for this account.",
            HttpStatus.CONFLICT),

    CHANGE_PASSWORD_ERROR_001(
            "CHANGE_PASSWORD_ERROR_001",
            "New password must not be the same as the current password.",
            HttpStatus.CONFLICT),

    CHANGE_PASSWORD_ERROR_002(
            "CHANGE_PASSWORD_ERROR_002",
            "You have failed to change your password %d times. If you reach 5 failed attempts, your account will be temporarily locked for 15 minutes as a security precaution.",
            HttpStatus.CONFLICT),

    CHANGE_PASSWORD_ERROR_003(
            "CHANGE_PASSWORD_ERROR_003",
            "You have failed to change your password 4 times. One more failed attempt may temporarily lock your account for 15 minutes.",
            HttpStatus.CONFLICT),

    CHANGE_PASSWORD_ERROR_004(
            "CHANGE_PASSWORD_ERROR_003",
            "Failed to change your password. Please check your current password and try again.",
            HttpStatus.CONFLICT),

    /* Role */
    ROLE_ERROR_001("ROLE_ERROR_001",
            "This role has already been deleted.",
            HttpStatus.NOT_FOUND),

    ROLE_ERROR_002("ROLE_ERROR_002",
            "This role is currently available and does not need restoration",
            HttpStatus.CONFLICT),

    ROLE_DETAIL_ERROR_001("ROLE_DETAIL_ERROR_001",
            "Role not found",
            HttpStatus.NOT_FOUND),

    ROLE_DETAIL_ERROR_002("ROLE_DETAIL_ERROR_002",
            "Role already exists",
            HttpStatus.CONFLICT),

    CHANGE_ROLE_ERROR_001("CHANGE_ROLE_ERROR_001",
            "New role should be different from the current role. Please choose another one.",
            HttpStatus.BAD_REQUEST),

    /* Permission */
    PERMISSION_ERROR_001("PERMISSION_ERROR_001",
            "This permission has already been deleted.",
            HttpStatus.NOT_FOUND),

    PERMISSION_ERROR_002("PERMISSION_ERROR_002",
            "This permission is currently available and does not need restoration.",
            HttpStatus.CONFLICT),

    PERMISSION_DETAIL_ERROR_001("PERMISSION_DETAIL_ERROR_001",
            "Permission not found.",
            HttpStatus.NOT_FOUND),

    PERMISSION_DETAIL_ERROR_002("PERMISSION_DETAIL_ERROR_002",
            "Permission already exists.",
            HttpStatus.CONFLICT),

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