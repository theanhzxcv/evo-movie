package com.evo.identity.application.enums;

public enum ETokenExpiration {
    ACCESS_TOKEN(5L * 60L), // 5 min
    REFRESH_TOKEN(30L * 60L); // 30 min

    public final Long value;

    ETokenExpiration(Long value) {
        this.value = value;
    }
}
