package com.evo.identity.application.enums;

public enum ERootRole {
    ROOT(1L),
    NON_ROOT(0L);

    public final Long value;

    ERootRole(Long value) {
        this.value = value;
    }
}
