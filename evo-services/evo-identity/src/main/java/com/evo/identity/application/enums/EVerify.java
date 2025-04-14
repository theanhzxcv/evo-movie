package com.evo.identity.application.enums;

public enum EVerify {
    UNVERIFIED(0L),
    VERIFIED(1L);

    public final Long value;

    EVerify(Long value) {
        this.value = value;
    }

}
