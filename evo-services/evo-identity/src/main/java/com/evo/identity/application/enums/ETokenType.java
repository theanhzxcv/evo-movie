package com.evo.identity.application.enums;

public enum ETokenType {
    BEARER("Bearer");

    public final String value;

    ETokenType(String value) {
        this.value = value;
    }
}
