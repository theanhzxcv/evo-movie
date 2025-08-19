package com.evo.identity.application.enums;

public enum ETokenType {
    ACCESS("Access"),
    RESET("Reset"),
    TFA("Tfa");

    public final String value;

    ETokenType(String value) {
        this.value = value;
    }
}
