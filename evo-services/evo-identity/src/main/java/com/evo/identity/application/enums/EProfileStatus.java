package com.evo.identity.application.enums;

public enum EProfileStatus {
    UNCOMPLETED(0L),
    COMPLETED(1L);

    public final Long value;

    EProfileStatus(Long value) {
        this.value = value;
    }
}
