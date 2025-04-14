package com.evo.identity.application.enums;

public enum EDefaultRole {
    USER("USER"),
    MANAGER("MANAGER"),
    ADMIN("ADMIN");

    public final String value;

    EDefaultRole(String value) {
        this.value = value;
    }
}
