package com.evo.identity.application.enums;

public enum EDefault {
    DEFAULT(1L),
    NOT_DEFAULT(0L);

    public final Long value;

    EDefault(Long value) {
        this.value = value;
    }
}
