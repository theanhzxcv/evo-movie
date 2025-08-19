package com.evo.identity.application.enums;

public enum EResponseStatus {
    SUCCESS(1L),
    FAILED(0L);

    public final Long value;

    EResponseStatus(Long value) {
        this.value = value;
    }
}
