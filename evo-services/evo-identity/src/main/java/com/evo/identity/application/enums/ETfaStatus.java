package com.evo.identity.application.enums;

public enum ETfaStatus {
    ENABLED(1L),
    DISABLED(0L);

    public final Long value;

    ETfaStatus(Long value) {
        this.value = value;
    }
}
