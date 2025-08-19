package com.evo.identity.application.enums;

public enum ELockThreshold {
    THRESHOLD_MIN(1L),
    THRESHOLD_WARN_LOW(2L),
    THRESHOLD_WARN_HIGH(3L),
    THRESHOLD_WAIT_LOCK(4L),
    THRESHOLD_LOCK(5L),
    LOCK_DURATION(15L),
    RESET(0L),
    ;

    public final Long value;

    ELockThreshold(Long value) {
        this.value = value;
    }
}
