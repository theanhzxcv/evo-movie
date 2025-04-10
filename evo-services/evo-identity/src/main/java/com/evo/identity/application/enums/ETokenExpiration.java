package com.evo.identity.application.enums;

public enum ETokenExpiration {
    ACCESS_TOKEN(60 * 60),
    REFRESH_TOKEN(7 * 24 * 60 * 60);

    public final int value;

    ETokenExpiration(int value) {
        this.value = value;
    }
}
