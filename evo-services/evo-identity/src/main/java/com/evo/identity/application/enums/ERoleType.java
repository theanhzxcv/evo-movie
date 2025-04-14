package com.evo.identity.application.enums;

public enum ERoleType {
    CONFIGURATION("Configuration Role"),
    ADMIN("Admin Role"),
    USER("User Role");

    public final String value;

    ERoleType(String value) {
        this.value = value;
    }
}
