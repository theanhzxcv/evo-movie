package com.evo.identity.application.enums;

public enum EActive {
    ACTIVE(1L),
    INACTIVE(0L);

    public final Long value;

    EActive(Long value) {
        this.value = value;
    }
}
