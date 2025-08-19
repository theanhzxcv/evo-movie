package com.evo.identity.application.enums;

public enum EActivityType {
    LOGIN("LOGIN"),
    REGISTER("REGISTER"),
    CHANGE_PASSWORD("CHANGE_PASSWORD"),
    LOGOUT("LOGOUT"),
    LOCK_ACCOUNT("LOCK_ACCOUNT"),;

    public final String value;

    EActivityType(String value) {
        this.value = value;
    }
}
